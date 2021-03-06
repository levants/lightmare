/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
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
package org.lightmare.cache;

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

import org.apache.log4j.Logger;
import org.lightmare.config.Configuration;
import org.lightmare.deploy.MetaCreator;
import org.lightmare.ejb.exceptions.BeanInUseException;
import org.lightmare.ejb.exceptions.BeanNotDeployedException;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.rest.providers.RestProvider;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.fs.WatchUtils;
import org.lightmare.utils.logging.LogUtils;

/**
 * Container class to save {@link MetaData} for bean interface {@link Class},
 * connections ({@link EntityManagerFactory}) for unit names and
 * {@link Configuration}s for distinct deployments
 *
 * @author Levan Tsinadze
 * @since 0.0.45
 */
public class MetaContainer {

    // Cached instance of MetaCreator
    private static MetaCreator creator;

    // Configurations cache for server
    public static final Map<String, Configuration> CONFIGS = new ConcurrentHashMap<String, Configuration>();

    // Cached bean meta data
    private static final ConcurrentMap<String, MetaData> EJBS = new ConcurrentHashMap<String, MetaData>();

    // Cached bean class name by its URL for undeploy processing
    private static final ConcurrentMap<URL, Collection<String>> EJB_URLS = new ConcurrentHashMap<URL, Collection<String>>();

    private static final Logger LOG = Logger.getLogger(MetaContainer.class);

    /**
     * Gets cached {@link MetaCreator} object
     *
     * @return {@link MetaCreator}
     */
    public static MetaCreator getCreator() {

        MetaCreator syncCreator;

        synchronized (MetaContainer.class) {
            syncCreator = creator;
        }

        return syncCreator;
    }

    /**
     * Caches {@link MetaCreator} object
     *
     * @param metaCreator
     */
    public static void setCreator(MetaCreator metaCreator) {
        synchronized (MetaContainer.class) {
            creator = metaCreator;
        }
    }

    /**
     * Clones passed {@link Configuration} instance
     *
     * @param configuration
     * @return {@link Configuration} cloned
     * @throws IOException
     */
    public static Configuration clone(Configuration configuration) throws IOException {

        Configuration clone;

        try {
            clone = configuration.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IOException(ex);
        }

        return clone;

    }

    /**
     * Caches {@link Configuration} for specific {@link URL} array
     *
     * @param archives
     * @param config
     */
    public static void putConfig(URL[] archives, Configuration config) {

        if (CollectionUtils.valid(archives)) {
            boolean containsPath;
            for (URL archive : archives) {
                String path = WatchUtils.clearPath(archive.getFile());
                containsPath = CONFIGS.containsKey(path);
                if (Boolean.FALSE.equals(containsPath)) {
                    CONFIGS.put(path, config);
                }
            }
        }
    }

    /**
     * Gets {@link Configuration} from cache for specific {@link URL} array
     *
     * @param archives
     */
    public static Configuration getConfig(URL[] archives) {

        Configuration config;

        URL archive = CollectionUtils.getFirst(archives);
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
     * @return {@link MetaData} added to cache
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
    public static void checkAndAddMetaData(String beanName, MetaData metaData) throws BeanInUseException {

        MetaData tmpMeta = addMetaData(beanName, metaData);
        if (ObjectUtils.notNull(tmpMeta)) {
            throw BeanInUseException.get(beanName);
        }
    }

    /**
     * Checks if bean with associated name deployed and if it is, then checks if
     * deployment is in progress
     *
     * @param beanName
     * @return boolean
     */
    public static boolean checkMetaData(String beanName) {

        boolean check;

        MetaData metaData = EJBS.get(beanName);
        check = metaData == null;
        if (Boolean.FALSE.equals(check)) {
            check = metaData.isInProgress();
        }

        return check;
    }

    /**
     * Checks if bean with associated name is already deployed
     *
     * @param beanName
     * @return boolean
     */
    public boolean checkBean(String beanName) {
        return EJBS.containsKey(beanName);
    }

    /**
     * Waits while passed {@link MetaData} instance is in progress
     *
     * @param inProgress
     * @param metaData
     * @throws IOException
     */
    private static void awaitProgress(boolean inProgress, MetaData metaData) throws IOException {

        while (inProgress) {
            try {
                metaData.wait();
                inProgress = metaData.isInProgress();
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            }
        }
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
                awaitProgress(inProgress, metaData);
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
            throw BeanNotDeployedException.get(beanName);
        }
        // Locks until initialization is complete
        awaitMetaData(metaData);

        return metaData;
    }

    /**
     * Gets bean name by containing archive {@link URL} address
     *
     * @param url
     * @return {@link Collection}<code><String></code>
     */
    public static Collection<String> getBeanNames(URL url) {

        Collection<String> urls;

        synchronized (MetaContainer.class) {
            urls = EJB_URLS.get(url);
        }

        return urls;
    }

    /**
     * Checks containing archive {@link URL} address
     *
     * @param url
     * @return <code>boolean</code>
     */
    public static boolean chackDeployment(URL url) {

        boolean containsKey;

        synchronized (MetaContainer.class) {
            containsKey = EJB_URLS.containsKey(url);
        }

        return containsKey;
    }

    /**
     * Removes cached EJB bean names {@link Collection} by containing file
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
     * Caches EJB bean name by {@link URL} of jar ear or any file
     *
     * @param beanName
     */
    public static void addBeanName(URL url, String beanName) {

        synchronized (MetaContainer.class) {
            Collection<String> beanNames = EJB_URLS.get(url);
            if (CollectionUtils.invalid(beanNames)) {
                beanNames = new HashSet<String>();
                EJB_URLS.put(url, beanNames);
            }
            // Caches EJB bean name
            beanNames.add(beanName);
        }
    }

    /**
     * Lists {@link Set} for deployed application {@link URL}'s
     *
     * @return {@link Set}<URL>
     */
    public static Set<URL> listApplications() {
        Set<URL> apps = EJB_URLS.keySet();
        return apps;
    }

    /**
     * Clears connection from cache for passed {@link MetaData} instance
     *
     * @param metaData
     * @throws IOException
     */
    private static void clearConnection(MetaData metaData) throws IOException {

        Collection<ConnectionData> connections = metaData.getConnections();
        if (CollectionUtils.valid(connections)) {
            for (ConnectionData connection : connections) {
                // Gets connection to clear
                String unitName = connection.getUnitName();
                ConnectionSemaphore semaphore = connection.getConnection();
                if (semaphore == null) {
                    semaphore = ConnectionContainer.getConnection(unitName);
                }
                // Clears connection from cache
                if (ObjectUtils.notNull(semaphore) && semaphore.decrementUser() <= ConnectionSemaphore.MINIMAL_USERS) {
                    ConnectionContainer.removeConnection(unitName);
                }
            }
        }
    }

    /**
     * Removes EJB bean (removes it's {@link MetaData} from cache) by bean class
     * name
     *
     * @param beanName
     * @throws IOException
     */
    public static void undeployBean(String beanName) throws IOException {

        MetaData metaData;

        try {
            metaData = getSyncMetaData(beanName);
        } catch (IOException ex) {
            LogUtils.error(LOG, ex, "Could not get bean resources %s cause %s", beanName, ex.getMessage());
            metaData = null;
        }
        // Removes MetaData from cache
        removeMeta(beanName);
        // Clears REST services and connections
        if (ObjectUtils.notNull(metaData)) {
            // Removes appropriated resource class from REST service
            if (RestContainer.hasRest()) {
                RestProvider.remove(metaData.getBeanClass());
            }
            // Clears connection and unloads classes
            clearConnection(metaData);
            ClassLoader loader = metaData.getLoader();
            LibraryLoader.closeClassLoader(loader);
            metaData = null;
        }
    }

    /**
     * Removes EJB bean (removes it's {@link MetaData} from cache) by
     * {@link URL} of archive file
     *
     * @param url
     * @throws IOException
     */
    public static boolean undeploy(URL url) throws IOException {

        boolean valid;

        synchronized (MetaContainer.class) {
            Collection<String> beanNames = getBeanNames(url);
            valid = CollectionUtils.valid(beanNames);
            if (valid) {
                for (String beanName : beanNames) {
                    undeployBean(beanName);
                }
            }
            // Clears EJB bean names
            removeBeanNames(url);
        }

        return valid;
    }

    /**
     * Removes EJB bean (removes it's {@link MetaData} from cache) by
     * {@link File} of archive file
     *
     * @param file
     * @throws IOException
     */
    public static boolean undeploy(File file) throws IOException {

        boolean valid;

        URL url = file.toURI().toURL();
        valid = undeploy(url);

        return valid;
    }

    /**
     * Removes EJB bean (removes it's {@link MetaData} from cache) by
     * {@link File} path of archive file
     *
     * @param path
     * @throws IOException
     */
    public static boolean undeploy(String path) throws IOException {

        boolean valid;

        File file = new File(path);
        valid = undeploy(file);

        return valid;
    }

    /**
     * Removed {@link MetaData} from cache by EJB bean name
     *
     * @param beanName
     */
    public static void removeMeta(String beanName) {
        EJBS.remove(beanName);
    }

    /**
     * Gets {@link java.util.Iterator}<MetaData> over all cached
     * {@link org.lightmare.cache.MetaData} objects
     *
     * @return {@link java.util.Iterator}<MetaData>
     */
    public static Iterator<MetaData> getBeanClasses() {
        return EJBS.values().iterator();
    }

    /**
     * Clears {@link MetaCreator} instance
     */
    private static void clearMetaCreator() {

        if (ObjectUtils.notNull(creator)) {
            creator.clear();
            creator = null;
        }
    }

    /**
     * Removes all cached resources
     */
    public static void clear() {

        if (ObjectUtils.notNull(creator)) {
            synchronized (MetaContainer.class) {
                clearMetaCreator();
            }
        }
        // Clears caches
        CONFIGS.clear();
        EJBS.clear();
        EJB_URLS.clear();
    }
}
