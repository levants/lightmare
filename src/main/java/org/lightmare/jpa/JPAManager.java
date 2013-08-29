package org.lightmare.jpa;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.ConnectionSemaphore;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.jta.HibernateConfig;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.NamingUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Creates and caches {@link EntityManagerFactory} for each ejb bean
 * {@link Class}'s appropriate field (annotated by @PersistenceContext)
 * 
 * @author Levan
 * 
 */
public class JPAManager {

    private List<String> classes;

    private String path;

    private URL url;

    private Map<Object, Object> properties;

    private boolean swapDataSource;

    private boolean scanArchives;

    private ClassLoader loader;

    private static final Logger LOG = Logger.getLogger(JPAManager.class);

    private JPAManager() {
    }

    /**
     * Checks if entity persistence.xml {@link URL} is provided
     * 
     * @return boolean
     */
    private boolean checkForURL() {
	return ObjectUtils.notNull(url)
		&& ObjectUtils.available(url.toString());
    }

    /**
     * Checks if entity classes or persistence.xml path are provided
     * 
     * @param classes
     * @return boolean
     */
    private boolean checkForBuild() {
	return ObjectUtils.available(classes) || ObjectUtils.available(path)
		|| checkForURL() || swapDataSource || scanArchives;
    }

    /**
     * Added transaction properties for JTA data sources
     */
    private void addTransactionManager() {
	if (properties == null) {
	    properties = new HashMap<Object, Object>();
	}
	properties.put(HibernateConfig.FACTORY_KEY,
		HibernateConfig.FACTORY_VALUE);
	properties.put(HibernateConfig.PLATFORM_KEY,
		HibernateConfig.PLATFORM_VALUE);
    }

    /**
     * Creates {@link EntityManagerFactory} by hibernate or by extended builder
     * {@link Ejb3ConfigurationImpl} if entity classes or persistence.xml file
     * path are provided
     * 
     * @see Ejb3ConfigurationImpl#configure(String, Map) and
     *      Ejb3ConfigurationImpl#createEntityManagerFactory()
     * 
     * @param unitName
     * @return {@link EntityManagerFactory}
     */
    @SuppressWarnings("deprecation")
    private EntityManagerFactory buildEntityManagerFactory(String unitName)
	    throws IOException {

	EntityManagerFactory emf;
	Ejb3ConfigurationImpl cfg;

	boolean pathCheck = ObjectUtils.available(path);
	boolean urlCheck = checkForURL();

	Ejb3ConfigurationImpl.Builder builder = new Ejb3ConfigurationImpl.Builder();

	if (loader == null) {
	    loader = LibraryLoader.getContextClassLoader();
	}

	if (ObjectUtils.available(classes)) {
	    builder.setClasses(classes);
	    // Loads entity classes to current ClassLoader instance
	    LibraryLoader.loadClasses(classes, loader);
	}

	if (pathCheck || urlCheck) {
	    Enumeration<URL> xmls;
	    ConfigLoader configLoader = new ConfigLoader();
	    if (pathCheck) {
		xmls = configLoader.readFile(path);
	    } else {
		xmls = configLoader.readURL(url);
	    }

	    builder.setXmls(xmls);
	    String shortPath = configLoader.getShortPath();
	    builder.setShortPath(shortPath);
	}

	builder.setSwapDataSource(swapDataSource);
	builder.setScanArchives(scanArchives);
	builder.setOverridenClassLoader(loader);

	cfg = builder.build();

	if (ObjectUtils.isFalse(swapDataSource)) {
	    addTransactionManager();
	}

	Ejb3ConfigurationImpl configured = cfg.configure(unitName, properties);

	emf = ObjectUtils.notNull(configured) ? configured
		.buildEntityManagerFactory() : null;

	return emf;
    }

    /**
     * Checks if entity classes or persistence.xml file path are provided to
     * create {@link EntityManagerFactory}
     * 
     * @see #buildEntityManagerFactory(String, String, Map, List)
     * 
     * @param unitName
     * @param properties
     * @param path
     * @param classes
     * @return {@link EntityManagerFactory}
     * @throws IOException
     */
    private EntityManagerFactory createEntityManagerFactory(String unitName)
	    throws IOException {
	EntityManagerFactory emf;
	if (checkForBuild()) {
	    emf = buildEntityManagerFactory(unitName);
	} else if (properties == null) {
	    emf = Persistence.createEntityManagerFactory(unitName);
	} else {
	    emf = Persistence.createEntityManagerFactory(unitName, properties);
	}

	return emf;
    }

    /**
     * Binds {@link EntityManagerFactory} to {@link javax.naming.InitialContext}
     * 
     * @param jndiName
     * @param unitName
     * @param emf
     * @throws IOException
     */
    private void bindJndiName(ConnectionSemaphore semaphore) throws IOException {
	boolean bound = semaphore.isBound();
	if (!bound) {
	    String jndiName = semaphore.getJndiName();
	    if (ObjectUtils.available(jndiName)) {
		JndiManager jndiManager = new JndiManager();
		try {
		    String fullJndiName = NamingUtils
			    .createJpaJndiName(jndiName);
		    if (jndiManager.lookup(fullJndiName) == null) {
			jndiManager.rebind(fullJndiName, semaphore.getEmf());
		    }
		    semaphore.setBound(Boolean.TRUE);
		} catch (IOException ex) {
		    LOG.error(ex.getMessage(), ex);
		    throw new IOException(String.format(
			    "could not bind connection %s",
			    semaphore.getUnitName()), ex);
		}
	    } else {
		semaphore.setBound(Boolean.TRUE);
	    }
	}
    }

    /**
     * Builds connection, wraps it in {@link ConnectionSemaphore} locks and
     * caches appropriate instance
     * 
     * @param unitName
     * @throws IOException
     */
    public void setConnection(String unitName) throws IOException {
	ConnectionSemaphore semaphore = ConnectionContainer
		.getSemaphore(unitName);
	if (semaphore.isInProgress()) {
	    EntityManagerFactory emf = createEntityManagerFactory(unitName);
	    semaphore.setEmf(emf);
	    semaphore.setInProgress(Boolean.FALSE);
	    bindJndiName(semaphore);
	} else if (semaphore.getEmf() == null) {
	    throw new IOException(String.format(
		    "Connection %s was not in progress", unitName));
	} else {
	    bindJndiName(semaphore);
	}
    }

    /**
     * Closes passed {@link EntityManagerFactory}
     * 
     * @param emf
     */
    public static void closeEntityManagerFactory(EntityManagerFactory emf) {

	if (ObjectUtils.notNull(emf) && emf.isOpen()) {
	    emf.close();
	}
    }

    /**
     * Builder class to create {@link JPAManager} class object
     * 
     * @author Levan
     * 
     */
    public static class Builder {

	private JPAManager manager;

	public Builder() {
	    manager = new JPAManager();
	    manager.scanArchives = Boolean.TRUE;
	}

	/**
	 * Sets {@link javax.persistence.Entity} class names to initialize
	 * 
	 * @param classes
	 * @return {@link Builder}
	 */
	public Builder setClasses(List<String> classes) {

	    manager.classes = classes;

	    return this;
	}

	/**
	 * Sets {@link URL} for persistence.xml file
	 * 
	 * @param url
	 * @return {@link Builder}
	 */
	public Builder setURL(URL url) {

	    manager.url = url;

	    return this;
	}

	/**
	 * Sets path for persistence.xml file
	 * 
	 * @param path
	 * @return {@link Builder}
	 */
	public Builder setPath(String path) {

	    manager.path = path;

	    return this;
	}

	/**
	 * Sets additional persistence properties
	 * 
	 * @param properties
	 * @return {@link Builder}
	 */
	public Builder setProperties(Map<Object, Object> properties) {

	    manager.properties = properties;
	    return this;
	}

	/**
	 * Sets boolean check property to swap jta data source value with non
	 * jta data source value
	 * 
	 * @param swapDataSource
	 * @return {@link Builder}
	 */
	public Builder setSwapDataSource(boolean swapDataSource) {
	    manager.swapDataSource = swapDataSource;
	    return this;
	}

	/**
	 * Sets boolean check to scan deployed archive files for
	 * {@link javax.persistence.Entity} annotated classes
	 * 
	 * @param scanArchives
	 * @return {@link Builder}
	 */
	public Builder setScanArchives(boolean scanArchives) {
	    manager.scanArchives = scanArchives;
	    return this;
	}

	/**
	 * Sets {@link ClassLoader} for persistence classes
	 * 
	 * @param loader
	 * @return {@link Builder}
	 */
	public Builder setClassLoader(ClassLoader loader) {
	    manager.loader = loader;

	    return this;
	}

	public JPAManager build() {
	    return manager;
	}
    }
}
