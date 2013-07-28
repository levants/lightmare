package org.lightmare.jpa;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
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

    // Keeps unique EntityManagerFactories builded by unit names
    private static final ConcurrentMap<String, ConnectionSemaphore> CONNECTIONS = new ConcurrentHashMap<String, ConnectionSemaphore>();

    private List<String> classes;

    private String path;

    private URL url;

    private Map<Object, Object> properties;

    private boolean swapDataSource;

    private boolean scanArchives;

    private ClassLoader loader;

    public static boolean pooledDataSource;

    private static final Logger LOG = Logger.getLogger(JPAManager.class);

    private JPAManager() {
    }

    public static boolean checkForEmf(String unitName) {

	boolean check = ObjectUtils.available(unitName);

	if (check) {
	    check = CONNECTIONS.containsKey(unitName);
	}

	return check;
    }

    public static ConnectionSemaphore getSemaphore(String unitName) {

	return CONNECTIONS.get(unitName);
    }

    private static ConnectionSemaphore createSemaphore(String unitName) {

	ConnectionSemaphore semaphore = CONNECTIONS.get(unitName);
	ConnectionSemaphore current = null;
	if (semaphore == null) {
	    semaphore = new ConnectionSemaphore();
	    semaphore.setUnitName(unitName);
	    semaphore.setInProgress(Boolean.TRUE);
	    semaphore.setCached(Boolean.TRUE);
	    current = CONNECTIONS.putIfAbsent(unitName, semaphore);
	}
	if (current == null) {
	    current = semaphore;
	}
	current.incrementUser();

	return current;
    }

    public static ConnectionSemaphore setSemaphore(String unitName,
	    String jndiName) {

	ConnectionSemaphore semaphore = null;

	if (ObjectUtils.available(unitName)) {

	    semaphore = createSemaphore(unitName);
	    if (ObjectUtils.available(jndiName)) {
		ConnectionSemaphore existent = CONNECTIONS.putIfAbsent(
			jndiName, semaphore);
		if (existent == null) {
		    semaphore.setJndiName(jndiName);
		}
	    }
	}

	return semaphore;
    }

    private static void awaitConnection(ConnectionSemaphore semaphore) {
	synchronized (semaphore) {
	    boolean inProgress = semaphore.isInProgress()
		    && !semaphore.isBound();
	    while (inProgress) {
		try {
		    semaphore.wait();
		    inProgress = semaphore.isInProgress()
			    && !semaphore.isBound();
		} catch (InterruptedException ex) {
		    inProgress = Boolean.FALSE;
		    LOG.error(ex.getMessage(), ex);
		}
	    }
	}

    }

    public static boolean isInProgress(String jndiName) {

	ConnectionSemaphore semaphore = CONNECTIONS.get(jndiName);
	boolean inProgress = ObjectUtils.notNull(semaphore);
	if (inProgress) {
	    inProgress = semaphore.isInProgress() && !semaphore.isBound();
	    if (inProgress) {
		awaitConnection(semaphore);
	    }
	}
	return inProgress;
    }

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
		JndiManager namingUtils = new JndiManager();
		try {
		    Context context = namingUtils.getContext();
		    String fullJndiName = NamingUtils
			    .createJpaJndiName(jndiName);
		    if (context.lookup(fullJndiName) == null) {
			namingUtils.getContext().rebind(fullJndiName,
				semaphore.getEmf());
		    }
		    semaphore.setBound(Boolean.TRUE);
		} catch (NamingException ex) {
		    throw new IOException(String.format(
			    "could not bind connection %s",
			    semaphore.getUnitName()), ex);
		}
	    } else {
		semaphore.setBound(Boolean.TRUE);
	    }
	}
    }

    public void setConnection(String unitName) throws IOException {
	ConnectionSemaphore semaphore = CONNECTIONS.get(unitName);
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
     * Gets {@link ConnectionSemaphore} from cache, awaits if connection
     * instantiation is in progress
     * 
     * @param unitName
     * @return {@link ConnectionSemaphore}
     * @throws IOException
     */
    public static ConnectionSemaphore getConnection(String unitName)
	    throws IOException {

	ConnectionSemaphore semaphore = CONNECTIONS.get(unitName);
	if (ObjectUtils.notNull(semaphore)) {
	    awaitConnection(semaphore);
	}

	return semaphore;
    }

    /**
     * Gets {@link EntityManagerFactory} from {@link ConnectionSemaphore},
     * awaits if connection
     * 
     * @param unitName
     * @return {@link EntityManagerFactory}
     * @throws IOException
     */
    public static EntityManagerFactory getEntityManagerFactory(String unitName)
	    throws IOException {

	EntityManagerFactory emf = null;
	ConnectionSemaphore semaphore = CONNECTIONS.get(unitName);
	if (ObjectUtils.notNull(semaphore)) {
	    awaitConnection(semaphore);
	    emf = semaphore.getEmf();
	}

	return emf;

    }

    /**
     * Unbinds connection from {@link javax.naming.Context}
     * 
     * @param semaphore
     */
    private static void unbindConnection(ConnectionSemaphore semaphore) {

	String jndiName = semaphore.getJndiName();
	if (ObjectUtils.notNull(jndiName) && semaphore.isBound()) {
	    JndiManager namingUtils = new JndiManager();
	    try {
		Context context = namingUtils.getContext();
		String fullJndiName = NamingUtils.createJpaJndiName(jndiName);
		if (ObjectUtils.notNull(context.lookup(fullJndiName))) {
		    context.unbind(fullJndiName);
		}
	    } catch (NamingException ex) {
		LOG.error(String.format(
			"Could not unbind jndi name %s cause %s", jndiName,
			ex.getMessage()), ex);
	    } catch (IOException ex) {
		LOG.error(String.format(
			"Could not unbind jndi name %s cause %s", jndiName,
			ex.getMessage()), ex);
	    }
	}
    }

    /**
     * Closes connection ({@link EntityManagerFactory}) in passed
     * {@link ConnectionSemaphore}
     * 
     * @param semaphore
     */
    private static void closeConnection(ConnectionSemaphore semaphore) {
	int users = semaphore.decrementUser();
	if (users <= 0) {
	    EntityManagerFactory emf = semaphore.getEmf();
	    closeEntityManagerFactory(emf);
	    unbindConnection(semaphore);
	    CONNECTIONS.remove(semaphore.getUnitName());
	    String jndiName = semaphore.getJndiName();
	    if (ObjectUtils.available(jndiName)) {
		CONNECTIONS.remove(jndiName);
		semaphore.setBound(Boolean.FALSE);
		semaphore.setCached(Boolean.FALSE);
	    }
	}
    }

    /**
     * Removes {@link ConnectionSemaphore} from cache and unbinds name from
     * {@link javax.naming.Context}
     * 
     * @param unitName
     */
    public static void removeConnection(String unitName) {

	ConnectionSemaphore semaphore = CONNECTIONS.get(unitName);
	if (ObjectUtils.notNull(semaphore)) {
	    awaitConnection(semaphore);
	    unbindConnection(semaphore);
	    closeConnection(semaphore);
	}
    }

    /**
     * Closes passed {@link EntityManagerFactory}
     * 
     * @param emf
     */
    private static void closeEntityManagerFactory(EntityManagerFactory emf) {

	if (ObjectUtils.notNull(emf) && emf.isOpen()) {
	    emf.close();
	}
    }

    /**
     * Closes all existing {@link EntityManagerFactory} instances kept in cache
     */
    public static void closeEntityManagerFactories() {
	Collection<ConnectionSemaphore> semaphores = CONNECTIONS.values();
	EntityManagerFactory emf;
	for (ConnectionSemaphore semaphore : semaphores) {
	    emf = semaphore.getEmf();
	    closeEntityManagerFactory(emf);
	}

	CONNECTIONS.clear();
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
	 * Adds boolean check if application uses pooled data source
	 * @param dsPooledType
	 * @return {@link Builder}
	 */
	public Builder setDataSourcePooledType(boolean dsPooledType) {
	    JPAManager.pooledDataSource = dsPooledType;
	    return this;
	}

	public Builder setClassLoader(ClassLoader loader) {
	    manager.loader = loader;

	    return this;
	}

	public JPAManager build() {
	    return manager;
	}
    }

}
