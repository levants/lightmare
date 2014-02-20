/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.jpa;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.ConnectionSemaphore;
import org.lightmare.config.Configuration;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.hibernate.jpa.HibernatePersistenceProviderExt;
import org.lightmare.jpa.jta.HibernateConfig;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.NamingUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

/**
 * Creates and caches {@link EntityManagerFactory} for each EJB bean
 * {@link Class}'s appropriate field (annotated by @PersistenceContext)
 * 
 * @author Levan Tsinadze
 * @since 0.0.79-SNAPSHOT
 */
public class JpaManager {

    // Entity classes
    private List<String> classes;

    // Path for configuration XML file
    private String path;

    // URL for configuration XML file
    private URL url;

    // Additional properties
    private Map<Object, Object> properties;

    private boolean swapDataSource;

    private boolean scanArchives;

    // Initialize level class loader
    private ClassLoader loader;

    // Error message for connection binding to JNDI names
    private static final String COULD_NOT_BIND_JNDI_ERROR = "could not bind connection";

    private static final Logger LOG = Logger.getLogger(JpaManager.class);

    /**
     * Private constructor to avoid initialization beside
     * {@link JpaManager.Builder} class
     */
    private JpaManager() {
    }

    /**
     * Checks if entity persistence.xml {@link URL} is provided
     * 
     * @return boolean
     */
    private boolean checkForURL() {
	return ObjectUtils.notNull(url) && StringUtils.valid(url.toString());
    }

    /**
     * Added transaction properties for JTA data sources
     */
    private void addTransactionManager() {

	if (properties == null) {
	    properties = new HashMap<Object, Object>();
	}

	HibernateConfig[] hibernateConfigs = HibernateConfig.values();
	for (HibernateConfig hibernateConfig : hibernateConfigs) {
	    properties.put(hibernateConfig.key, hibernateConfig.value);
	}
    }

    /**
     * Creates {@link EntityManagerFactory} by "Hibernate" or by extended
     * builder {@link Ejb3ConfigurationImpl} if entity classes or
     * persistence.xml file path are provided
     * 
     * @see Ejb3ConfigurationImpl#configure(String, Map) and
     *      Ejb3ConfigurationImpl#createEntityManagerFactory()
     * 
     * @param unitName
     * @return {@link EntityManagerFactory}
     */
    private EntityManagerFactory buildEntityManagerFactory(String unitName)
	    throws IOException {

	EntityManagerFactory emf;

	HibernatePersistenceProvider provider;
	boolean pathCheck = StringUtils.valid(path);
	boolean urlCheck = checkForURL();
	HibernatePersistenceProviderExt.Builder builder = new HibernatePersistenceProviderExt.Builder();
	if (loader == null) {
	    loader = LibraryLoader.getContextClassLoader();
	}

	if (CollectionUtils.valid(classes)) {
	    builder.setClasses(classes);
	    // Loads entity classes to current ClassLoader instance
	    LibraryLoader.loadClasses(classes, loader);
	}

	if (pathCheck || urlCheck) {
	    List<URL> xmls;
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
	provider = builder.build();

	if (ObjectUtils.notTrue(swapDataSource)) {
	    addTransactionManager();
	}

	emf = provider.createEntityManagerFactory(unitName, properties);

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

	EntityManagerFactory emf = buildEntityManagerFactory(unitName);

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

	if (ObjectUtils.notTrue(bound)) {
	    String jndiName = semaphore.getJndiName();
	    if (StringUtils.valid(jndiName)) {
		JndiManager jndiManager = new JndiManager();
		try {
		    String fullJndiName = NamingUtils
			    .createJpaJndiName(jndiName);
		    if (jndiManager.lookup(fullJndiName) == null) {
			jndiManager.rebind(fullJndiName, semaphore.getEmf());
		    }
		} catch (IOException ex) {
		    LOG.error(ex.getMessage(), ex);
		    String errorMessage = StringUtils.concat(
			    COULD_NOT_BIND_JNDI_ERROR, semaphore.getUnitName());
		    throw new IOException(errorMessage, ex);
		}
	    }
	}

	semaphore.setBound(Boolean.TRUE);
    }

    /**
     * Builds connection, wraps it in {@link ConnectionSemaphore} locks and
     * caches appropriate instance
     * 
     * @param unitName
     * @throws IOException
     */
    public void create(String unitName) throws IOException {

	ConnectionSemaphore semaphore = ConnectionContainer
		.getSemaphore(unitName);
	if (semaphore.isInProgress()) {
	    EntityManagerFactory emf = createEntityManagerFactory(unitName);
	    semaphore.setEmf(emf);
	    semaphore.setInProgress(Boolean.FALSE);
	    bindJndiName(semaphore);
	} else if (semaphore.getEmf() == null) {
	    String errorMessage = String.format(
		    "Connection %s was not in progress", unitName);
	    throw new IOException(errorMessage);
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
     * Closes passed {@link EntityManager} instance if it is not null and it is
     * open
     * 
     * @param em
     */
    public static void closeEntityManager(EntityManager em) {

	if (ObjectUtils.notNull(em) && em.isOpen()) {
	    em.close();
	}
    }

    /**
     * Builder class to create {@link JpaManager} class object
     * 
     * @author Levan Tsinadze
     * @since 0.0.79-SNAPSHOT
     */
    public static class Builder {

	private JpaManager manager;

	public Builder() {
	    manager = new JpaManager();
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
	 * Sets boolean check property to swap JTA data source value with non
	 * JTA data source value
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

	/**
	 * Sets all parameters from passed {@link Configuration} instance
	 * 
	 * @param configuration
	 * @return {@link Builder}
	 */
	public Builder configure(Configuration configuration) {

	    // Sets all parameters from Configuration class
	    setPath(configuration.getPersXmlPath())
		    .setProperties(configuration.getPersistenceProperties())
		    .setSwapDataSource(configuration.isSwapDataSource())
		    .setScanArchives(configuration.isScanArchives());

	    return this;
	}

	public JpaManager build() {
	    return manager;
	}
    }
}
