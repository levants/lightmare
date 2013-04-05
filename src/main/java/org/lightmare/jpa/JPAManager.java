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
import org.lightmare.ejb.meta.ConnectionSemaphore;
import org.lightmare.jndi.NamingUtils;
import org.lightmare.jpa.jta.HibernateConfig;
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
		    inProgress = false;
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

	boolean checkForPath = checkForPath();
	boolean checkForURL = checkForURL();
	boolean checkForClasses = checkForClasses();
	if (checkForPath || checkForURL) {
	    Enumeration<URL> xmls;
	    ConfigLoader configLoader = new ConfigLoader();
	    if (checkForPath) {
		xmls = configLoader.readFile(path);
	    } else {
		xmls = configLoader.readURL(url);
	    }
	    if (checkForClasses) {
		cfg = new Ejb3ConfigurationImpl(classes, xmls);
	    } else {
		cfg = new Ejb3ConfigurationImpl(xmls);
	    }
	    cfg.setShortPath(configLoader.getShortPath());
	} else {
	    cfg = new Ejb3ConfigurationImpl(classes);
	}

	cfg.setSwapDataSource(swapDataSource);
	cfg.setScanArchives(scanArchives);

	if (!swapDataSource) {
	    addTransactionManager();
	}

	Ejb3ConfigurationImpl configured = cfg.configure(unitName, properties);

	emf = ObjectUtils.notNull(configured) ? configured
		.buildEntityManagerFactory() : null;
	return emf;
    }

    /**
     * Checks if entity classes are provided
     * 
     * @return boolean
     */
    private boolean checkForClasses() {
	return ObjectUtils.available(classes);
    }

    /**
     * Checks if entity persistence.xml path is provided
     * 
     * @return boolean
     */
    private boolean checkForPath() {
	return ObjectUtils.available(path);
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
	return checkForClasses() || checkForPath() || checkForURL();
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
		NamingUtils namingUtils = new NamingUtils();
		try {
		    Context context = namingUtils.getContext();
		    if (context.lookup(jndiName) == null) {
			String fullJndiName = NamingUtils
				.createJpaJndiName(jndiName);
			namingUtils.getContext().bind(fullJndiName,
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
	    semaphore.setInProgress(false);
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
	    NamingUtils namingUtils = new NamingUtils();
	    try {
		Context context = namingUtils.getContext();
		if (ObjectUtils.notNull(context.lookup(jndiName))) {
		    context.unbind(jndiName);
		}
	    } catch (NamingException ex) {
		LOG.error(ex.getMessage(), ex);
	    } catch (IOException ex) {
		LOG.error(ex.getMessage(), ex);
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
	    closeConnection(semaphore);
	    NamingUtils namingUtils = new NamingUtils();
	    try {
		String jndiName = NamingUtils.createJpaJndiName(unitName);
		namingUtils.unbind(jndiName);
	    } catch (IOException ex) {
		LOG.error(String.format(
			"Could not unbind jndi name %s cause %s", unitName,
			ex.getMessage()), ex);
	    }
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

	public Builder setClasses(List<String> classes) {
	    manager.classes = classes;
	    return this;
	}

	public Builder setURL(URL url) {
	    manager.url = url;
	    return this;
	}

	public Builder setPath(String path) {
	    manager.path = path;
	    return this;
	}

	public Builder setProperties(Map<Object, Object> properties) {
	    manager.properties = properties;
	    return this;
	}

	public Builder setSwapDataSource(boolean swapDataSource) {
	    manager.swapDataSource = swapDataSource;
	    return this;
	}

	public Builder setScanArchives(boolean scanArchives) {
	    manager.scanArchives = scanArchives;
	    return this;
	}

	public Builder setDataSourcePooledType(boolean dsPooledType) {
	    JPAManager.pooledDataSource = dsPooledType;
	    return this;
	}

	public JPAManager build() {
	    return manager;
	}
    }

}
