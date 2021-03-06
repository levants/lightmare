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
package org.lightmare.jpa.datasource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.lightmare.jpa.datasource.Initializer.ConnectionConfig;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.io.IOUtils;

/**
 * Configuration with default and set parameters for c3p0 connection pooling
 *
 * @author Levan Tsinadze
 * @since 0.0.26-SNAPSHOT
 */
public class PoolConfig {

    /**
     * Container for default configuration keys and values
     *
     * @author Levan Tsinadze
     * @since 0.0.81-SNAPSHOT
     */
    public static enum Defaults {

        // Data source name property
        DATA_SOURCE_NAME("dataSourceName"),

        // ===========================================//
        // ====== Data Source properties =============//
        // ===========================================//

        // Class loader properties
        CONTEXT_CLASS_LOADER_SOURCE("contextClassLoaderSource", "library"), // loader
        PRIVILEGED_SPAWNED_THREADS("privilegeSpawnedThreads", Boolean.TRUE), // threads

        // Pool properties
        MAX_POOL_SIZE("maxPoolSize", 15), // max pool size
        INITIAL_POOL_SIZE("initialPoolSize", 5), // initial
        MIN_POOL_SIZE("minPoolSize", 5), // minimal pool size
        MAX_STATEMENTS("maxStatements", 50), // statements
        AQUIRE_INCREMENT("acquireIncrement", 5), // increment

        // Pool timeout properties
        MAX_IDLE_TIMEOUT("maxIdleTime", 1200), // idle
        MAX_IDLE_TIME_EXCESS_CONN("maxIdleTimeExcessConnections", 60), // excess
        CHECK_OUT_TIMEOUT("checkoutTimeout", 5000), // checkout

        // Controller properties
        STAT_CACHE_NUM_DEFF_THREADS("statementCacheNumDeferredCloseThreads", 1),

        // Transaction properties
        AUTOCOMMIT("autoCommit", Boolean.FALSE), // auto commit
        AUTOCOMMIT_ON_CLOSE("autoCommitOnClose", Boolean.FALSE), // on close
        URESOLVED_TRANSACTIONS("forceIgnoreUnresolvedTransactions", Boolean.TRUE), // ignore
                                                                                   // unresolved
                                                                                   // transactions

        // Connection recovery properties
        ACQUIRE_RETRY_ATTEMPTS("acquireRetryAttempts", 0), // retry
        ACQUIRE_RETRY_DELAY("acquireRetryDelay", 1000), // delay
        BREACK_AFTER_ACQUIRE_FAILURE("breakAfterAcquireFailure", Boolean.FALSE); // break

        public String key;

        public String value;

        private Defaults(String key) {
            this.key = key;
        }

        private Defaults(String key, Object value) {
            this(key);
            if (value instanceof String) {
                this.value = (String) value;
            } else {
                this.value = String.valueOf(value);
            }
        }
    }

    // Data source name property
    public static final String DATA_SOURCE_NAME = "dataSourceName";

    // Default value for data source properties file
    private static final String POOL_PATH_DEF_VALUE = "META-INF/pool.properties";

    private String poolPath;

    private Map<Object, Object> poolProperties = new HashMap<Object, Object>();

    // Check is data source pooled or not
    private boolean pooledDataSource;

    /**
     * Enumeration to choose which type connection pool should be in use
     *
     * @author levan
     * @since 0.0.80-SNAPSHOT
     */
    public static enum PoolProviderType {

        DBCP, C3P0, TOMCAT;
    }

    // Default pool provider type
    private PoolProviderType poolProviderType = PoolProviderType.C3P0;

    /**
     * Sets default connection pooling properties
     *
     * @return {@link Map}<code><Object, Object></code>
     */
    public Map<Object, Object> getDefaultPooling() {

        Map<Object, Object> c3p0Properties = new HashMap<Object, Object>();

        Defaults[] defaults = Defaults.values();
        String key;
        String value;
        for (Defaults config : defaults) {
            key = config.key;
            value = config.value;
            if (StringUtils.validAll(key, value)) {
                c3p0Properties.put(key, value);
            }
        }

        return c3p0Properties;
    }

    /**
     * Caches keys which are not supported directly for pool configuration to
     * remove them
     *
     * @return {@link Set}<code><Object></code>
     * @throws IOException
     */
    private Set<Object> unsopportedKeys() throws IOException {

        Set<Object> keys = new HashSet<Object>();

        ConnectionConfig[] usdKeys = ConnectionConfig.values();
        String key;
        for (ConnectionConfig usdKey : usdKeys) {
            key = usdKey.name;
            if (StringUtils.valid(key)) {
                keys.add(key);
            }
        }

        return keys;
    }

    /**
     * Add initialized properties to defaults
     *
     * @param defaults
     * @param initial
     */
    private void fillDefaults(Map<Object, Object> defaults, Map<Object, Object> initial) {
        defaults.putAll(initial);
    }

    /**
     * Generates pooling configuration properties
     *
     * @param initial
     * @return {@link Map}<Object, Object>
     * @throws IOException
     */
    private Map<Object, Object> configProperties(Map<Object, Object> initial) throws IOException {

        Map<Object, Object> propertiesMap = getDefaultPooling();

        fillDefaults(propertiesMap, initial);
        Set<Object> keys = unsopportedKeys();
        String dataSourceName = null;
        String property;
        for (Object key : keys) {
            property = ConnectionConfig.NAME_PROPERTY.name;
            if (key.equals(property)) {
                dataSourceName = (String) propertiesMap.get(property);
            }
            propertiesMap.remove(key);
        }

        if (StringUtils.valid(dataSourceName)) {
            propertiesMap.put(DATA_SOURCE_NAME, dataSourceName);
        }

        return propertiesMap;
    }

    /**
     * Gets property as {@link String} value
     *
     * @param properties
     * @param key
     * @return {@link String} property
     */
    public static String asText(Map<Object, Object> properties, Object key) {

        String value;

        Object property = properties.get(key);

        if (property == null) {
            value = null;
        } else if (property instanceof String) {
            value = ObjectUtils.cast(property, String.class);
        } else {
            value = property.toString();
        }

        return value;
    }

    /**
     * Gets property as <code>int</code> value
     *
     * @param properties
     * @param key
     * @return <code>int</code>
     */
    public static Integer asInt(Map<Object, Object> properties, Object key) {

        Integer value;

        Object property = properties.get(key);
        if (property == null) {
            value = null;
        } else if (property instanceof Integer) {
            value = ObjectUtils.cast(property, Integer.class);
        } else if (property instanceof String) {
            String text = ObjectUtils.cast(property, String.class);
            value = Integer.valueOf(text);
        } else {
            value = null;
        }

        return value;
    }

    /**
     * Gets property as <code>int</code> value
     *
     * @param properties
     * @param config
     * @return <code>int</code>
     */
    public static Integer asInt(Map<Object, Object> properties, Defaults config) {

        Integer propertyInt;

        String key = config.key;
        propertyInt = asInt(properties, key);

        return propertyInt;
    }

    /**
     * Gets property as {@link Boolean} value
     *
     * @param properties
     * @param key
     * @return {@link Boolean} value of property
     */
    public static Boolean asBoolean(Map<Object, Object> properties, Object key) {

        Boolean value;

        Object property = properties.get(key);
        if (property == null) {
            value = null;
        } else if (property instanceof Boolean) {
            value = ObjectUtils.cast(property, Boolean.class);
        } else if (property instanceof String) {
            String text = ObjectUtils.cast(property, String.class);
            value = Boolean.valueOf(text);
        } else {
            value = null;
        }

        return value;
    }

    /**
     * Loads {@link Properties} from specific path
     *
     * @return {@link Properties}
     * @throws IOException
     */
    public Map<Object, Object> load() throws IOException {

        Map<Object, Object> properties;

        InputStream stream;
        if (StringUtils.invalid(poolPath)) {
            ClassLoader loader = LibraryLoader.getContextClassLoader();
            stream = loader.getResourceAsStream(POOL_PATH_DEF_VALUE);
        } else {
            File file = new File(poolPath);
            stream = new FileInputStream(file);
        }

        try {
            Properties propertiesToLoad;
            if (ObjectUtils.notNull(stream)) {
                propertiesToLoad = new Properties();
                propertiesToLoad.load(stream);
                properties = new HashMap<Object, Object>();
                properties.putAll(propertiesToLoad);
            } else {
                properties = null;
            }
        } finally {
            IOUtils.close(stream);
        }

        return properties;
    }

    /**
     * Merges passed properties, startup time passed properties and properties
     * loaded from file
     *
     * @param properties
     * @return {@link Map}<Object, Object> merged properties map
     * @throws IOException
     */
    public Map<Object, Object> merge(Map<Object, Object> properties) throws IOException {

        Map<Object, Object> configMap = configProperties(properties);

        Map<Object, Object> loaded = load();
        if (ObjectUtils.notNull(loaded)) {
            fillDefaults(configMap, loaded);
        }

        if (ObjectUtils.notNull(poolProperties)) {
            fillDefaults(configMap, poolProperties);
        }

        return configMap;
    }

    public String getPoolPath() {
        return poolPath;
    }

    public void setPoolPath(String poolPath) {
        this.poolPath = poolPath;
    }

    public Map<Object, Object> getPoolProperties() {
        return poolProperties;
    }

    public void setPoolProperties(Map<Object, Object> poolProperties) {
        this.poolProperties = poolProperties;
    }

    public boolean isPooledDataSource() {
        return pooledDataSource;
    }

    public void setPooledDataSource(boolean pooledDataSource) {
        this.pooledDataSource = pooledDataSource;
    }

    public PoolProviderType getPoolProviderType() {
        return poolProviderType;
    }

    /**
     * Sets appropriate connection pool provider
     *
     * @param poolProviderType
     */
    public void setPoolProviderType(PoolProviderType poolProviderType) {

        if (ObjectUtils.notNull(poolProviderType)) {
            this.poolProviderType = poolProviderType;
        }
    }

    /**
     * Sets appropriate connection pool provider
     *
     * @param poolProviderTypeName
     */
    public void setPoolProviderType(String poolProviderTypeName) {

        PoolProviderType[] types = PoolProviderType.values();
        int length = types.length;
        boolean typeNotSet = Boolean.TRUE;
        PoolProviderType type;
        for (int i = 0; i < length && typeNotSet; i++) {
            type = types[i];
            if (type.toString().equalsIgnoreCase(poolProviderTypeName)) {
                this.poolProviderType = type;
                typeNotSet = Boolean.FALSE;
            }
        }
    }
}
