package org.lightmare.cache;

import static org.lightmare.jpa.JPAManager.closeEntityManagerFactories;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.glassfish.jersey.server.model.Resource;
import org.lightmare.config.Configuration;
import org.lightmare.deploy.MetaCreator;
import org.lightmare.ejb.exceptions.BeanInUseException;
import org.lightmare.jpa.JPAManager;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.rest.utils.RestUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.fs.WatchUtils;

/**
 * Container class to save {@link MetaData} for bean interface {@link Class} and
 * connections {@link EntityManagerFactory}es for unit names
 * 
 * @author Levan
 * 
 */
public class MetaContainer {

    // Cached instance of MetaCreator
    private static MetaCreator creator;

    /**
     * {@link Configuration} container class for server
     */
    public static final Map<String, Configuration> CONFIGS = new ConcurrentHashMap<String, Configuration>();

    // Cached bean meta data
    private static final ConcurrentMap<String, MetaData> EJBS = new ConcurrentHashMap<String, MetaData>();

    // Cached bean class name by its URL for undeploy processing
    private static final ConcurrentMap<URL, Collection<String>> EJB_URLS = new ConcurrentHashMap<URL, Collection<String>>();

    // Cached REST resource classes
    private static final ConcurrentMap<Class<?>, Resource> REST_RESOURCES = new ConcurrentHashMap<Class<?>, Resource>();

    // Caches UserTransaction object per thread
    private static final ThreadLocal<UserTransaction> TRANSACTION_HOLDER = new ThreadLocal<UserTransaction>();

    private static final Logger LOG = Logger.getLogger(MetaContainer.class);

    /**
     * Gets cached {@link MetaCreator} object
     * 
     * @return
     */
    public static MetaCreator getCreator() {

	synchronized (MetaContainer.class) {

	    return creator;
	}
    }

    /**
     * Caches {@link MetaCreator} object
     * 
     * @param metaCreator
     */
    public static void setCreator(MetaCreator metaCreator) throws IOException {

	synchronized (MetaContainer.class) {
	    creator = metaCreator;
	}
    }

    /**
     * Removes all cached resources
     */
    public static void clear() {

	if (ObjectUtils.notNull(creator)) {
	    synchronized (MetaContainer.class) {
		creator.clear();
		creator = null;
	    }
	}
    }

    /**
     * Caches {@link Configuration} for specific {@link URL} array
     * 
     * @param archives
     * @param config
     */
    public static void putConfig(URL[] archives, Configuration config) {

	if (ObjectUtils.available(archives)) {
	    for (URL archive : archives) {
		String path = WatchUtils.clearPath(archive.getFile());
		if (CONFIGS.containsKey(path)) {
		    continue;
		}
		CONFIGS.put(path, config);
	    }
	}
    }

    /**
     * Gets {@link Configuration} from cache for specific {@link URL} array
     * 
     * @param archives
     * @param config
     */
    public static Configuration getConfig(URL[] archives) {

	Configuration config;
	URL archive = ObjectUtils.getFirst(archives);
	if (ObjectUtils.notNull(archive)) {
	    String path = WatchUtils.clearPath(archive.getFile());
	    config = CONFIGS.get(path);
	} else {
	    config = null;
	}

	return config;
    }

    /**
     * Adds {@link MetaData} to cache on specified bean name if absent and
     * returns previous value on this name or null if such value does not exists
     * 
     * @param beanName
     * @param metaData
     * @return
     */
    public static MetaData addMetaData(String beanName, MetaData metaData) {

	return EJBS.putIfAbsent(beanName, metaData);
    }

    /**
     * Check if {@link MetaData} is ceched for specified bean name if true
     * throws {@link BeanInUseException}
     * 
     * @param beanName
     * @param metaData
     * @throws BeanInUseException
     */
    public static void checkAndAddMetaData(String beanName, MetaData metaData)
	    throws BeanInUseException {
	MetaData tmpMeta = addMetaData(beanName, metaData);
	if (ObjectUtils.notNull(tmpMeta)) {
	    throw new BeanInUseException(String.format(
		    "bean %s is alredy in use", beanName));
	}
    }

    /**
     * Checks if bean with associated name deployed and if yes if is deployment
     * in progress
     * 
     * @param beanName
     * @return boolean
     */
    public static boolean checkMetaData(String beanName) {
	boolean check;
	MetaData metaData = EJBS.get(beanName);
	check = metaData == null;
	if (!check) {
	    check = metaData.isInProgress();
	}

	return check;
    }

    /**
     * Checks if bean with associated name deployed
     * 
     * @param beanName
     * @return boolean
     */
    public boolean checkBean(String beanName) {

	return EJBS.containsKey(beanName);
    }

    /**
     * Waits while {@link MetaData#isInProgress()} is true
     * 
     * @param metaData
     * @throws IOException
     */
    public static void awaitMetaData(MetaData metaData) throws IOException {
	boolean inProgress = metaData.isInProgress();
	if (inProgress) {
	    synchronized (metaData) {
		while (inProgress) {
		    try {
			metaData.wait();
			inProgress = metaData.isInProgress();
		    } catch (InterruptedException ex) {
			throw new IOException(ex);
		    }
		}
	    }
	}
    }

    /**
     * Gets deployed bean {@link MetaData} by name without checking deployment
     * progress
     * 
     * @param beanName
     * @return {@link MetaData}
     */
    public static MetaData getMetaData(String beanName) {
	return EJBS.get(beanName);
    }

    /**
     * Check if {@link MetaData} with associated name deployed and if it is
     * waits while {@link MetaData#isInProgress()} true before return it
     * 
     * @param beanName
     * @return {@link MetaData}
     * @throws IOException
     */
    public static MetaData getSyncMetaData(String beanName) throws IOException {
	MetaData metaData = getMetaData(beanName);
	if (metaData == null) {
	    throw new IOException(String.format("Bean %s is not deployed",
		    beanName));
	}
	awaitMetaData(metaData);

	return metaData;
    }

    /**
     * Gets bean name by containing archive {@link URL} address
     * 
     * @param url
     * @return
     */
    public static Collection<String> getBeanNames(URL url) {

	synchronized (MetaContainer.class) {

	    return EJB_URLS.get(url);
	}
    }

    /**
     * checks containing archive {@link URL} address
     * 
     * @param url
     * @return
     */
    public static boolean chackDeployment(URL url) {

	synchronized (MetaContainer.class) {

	    return EJB_URLS.containsKey(url);
	}
    }

    /**
     * Removes cached bean names {@link Collection} by containing file
     * {@link URL} as key
     * 
     * @param url
     */
    public static void removeBeanNames(URL url) {

	synchronized (MetaContainer.class) {
	    EJB_URLS.remove(url);
	}
    }

    /**
     * Caches bean name by {@link URL} of jar ear or any file
     * 
     * @param beanName
     */
    public static void addBeanName(URL url, String beanName) {

	synchronized (MetaContainer.class) {
	    Collection<String> beanNames = EJB_URLS.get(url);
	    if (ObjectUtils.notAvailable(beanNames)) {
		beanNames = new HashSet<String>();
		EJB_URLS.put(url, beanNames);
	    }

	    beanNames.add(beanName);
	}
    }

    /**
     * Lists set for deployed application {@link URL}'s
     * 
     * @return {@link Set}<URL>
     */
    public static Set<URL> listApplications() {

	Set<URL> apps = EJB_URLS.keySet();

	return apps;
    }

    /**
     * Clears connection from cache
     * 
     * @param metaData
     * @throws IOException
     */
    private static void clearConnection(MetaData metaData) throws IOException {

	Collection<ConnectionData> connections = metaData.getConnections();

	if (ObjectUtils.available(connections)) {
	    for (ConnectionData connection : connections) {
		// Gets connection to clear
		String unitName = connection.getUnitName();
		ConnectionSemaphore semaphore = connection.getConnection();
		if (semaphore == null) {
		    semaphore = JPAManager.getConnection(unitName);
		}
		if (ObjectUtils.notNull(semaphore)
			&& semaphore.decrementUser() <= 1) {
		    JPAManager.removeConnection(unitName);
		}
	    }
	}
    }

    /**
     * Removes bean (removes it's {@link MetaData} from cache) by bean class
     * name
     * 
     * @param beanName
     * @throws IOException
     */
    public static void undeployBean(String beanName) throws IOException {

	MetaData metaData = null;
	try {
	    metaData = getSyncMetaData(beanName);
	} catch (IOException ex) {
	    LOG.error(String.format("Could not get bean resources %s cause %s",
		    beanName, ex.getMessage()), ex);
	}
	// Removes MetaData from cache
	removeMeta(beanName);
	if (ObjectUtils.notNull(metaData)) {
	    // Removes appropriated resource class from REST service
	    RestUtils.remove(metaData.getBeanClass());
	    clearConnection(metaData);
	    ClassLoader loader = metaData.getLoader();
	    LibraryLoader.closeClassLoader(loader);
	    metaData = null;
	}
    }

    /**
     * Removes bean (removes it's {@link MetaData} from cache) by {@link URL} of
     * archive file
     * 
     * @param url
     * @throws IOException
     */
    public static void undeploy(URL url) throws IOException {

	synchronized (MetaContainer.class) {
	    Collection<String> beanNames = getBeanNames(url);
	    if (ObjectUtils.available(beanNames)) {
		for (String beanName : beanNames) {
		    undeployBean(beanName);
		}
	    }
	    removeBeanNames(url);
	}
    }

    /**
     * Removes bean (removes it's {@link MetaData} from cache) by {@link File}
     * of archive file
     * 
     * @param file
     * @throws IOException
     */
    public static void undeploy(File file) throws IOException {

	URL url = file.toURI().toURL();
	undeploy(url);
    }

    /**
     * Removes bean (removes it's {@link MetaData} from cache) by {@link File}
     * path of archive file
     * 
     * @param path
     * @throws IOException
     */
    public static void undeploy(String path) throws IOException {

	File file = new File(path);
	undeploy(file);
    }

    /**
     * Removed {@link MetaData} from cache
     * 
     * @param beanName
     */
    public static void removeMeta(String beanName) {
	EJBS.remove(beanName);
    }

    /**
     * Gets {@link javax.persistence.EntityManagerFactory} from cache for
     * associated unit name
     * 
     * @param unitName
     * @return {@link javax.persistence.EntityManagerFactory}
     * @throws IOException
     */
    public static EntityManagerFactory getConnection(String unitName)
	    throws IOException {

	return JPAManager.getEntityManagerFactory(unitName);
    }

    /**
     * Gets {@link UserTransaction} object from {@link ThreadLocal} per thread
     * 
     * @return {@link UserTransaction}
     */
    public static UserTransaction getTransaction() {

	UserTransaction transaction = TRANSACTION_HOLDER.get();

	return transaction;
    }

    /**
     * Caches {@link UserTransaction} object in {@link ThreadLocal} per thread
     * 
     * @param transaction
     */
    public static void setTransaction(UserTransaction transaction) {

	TRANSACTION_HOLDER.set(transaction);
    }

    /**
     * Removes {@link UserTransaction} object from {@link ThreadLocal} per
     * thread
     */
    public static void removeTransaction() {

	TRANSACTION_HOLDER.remove();
    }

    /**
     * Closes all {@link javax.persistence.EntityManagerFactory} cached
     * instances
     */
    public static void closeConnections() {
	closeEntityManagerFactories();
    }

    /**
     * Gets {@link java.util.Iterator}<MetaData> over all cached
     * {@link org.lightmare.cache.MetaData}
     * 
     * @return {@link java.util.Iterator}<MetaData>
     */
    public static Iterator<MetaData> getBeanClasses() {
	return EJBS.values().iterator();
    }

    public static void putResource(Class<?> resourceClass, Resource resource) {
	REST_RESOURCES.putIfAbsent(resourceClass, resource);
    }

    public static Resource getResource(Class<?> resourceClass) {

	Resource resource = REST_RESOURCES.get(resourceClass);

	return resource;
    }

    public static void removeResource(Class<?> resourceClass) {

	REST_RESOURCES.remove(resourceClass);
    }

    public static boolean hasRest() {

	return ObjectUtils.available(REST_RESOURCES);
    }
}
