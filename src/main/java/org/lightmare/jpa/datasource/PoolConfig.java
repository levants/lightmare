package org.lightmare.jpa.datasource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Configuration with default parameters for c3p0 connection pooling
 * 
 * @author levan
 * 
 */
public class PoolConfig {

    // Data source name property
    public static final String DATA_SOURCE_NAME = "dataSourceName";

    // ===========================================//
    // ====== Data Source properties keys ========//
    // ===========================================//

    // Class loader properties
    public static final String CONTEXT_CLASS_LOADER_SOURCE = "contextClassLoaderSource";
    public static final String PRIVILEGED_SPAWNED_THREADS = "privilegeSpawnedThreads";

    // Pool properties
    public static final String MAX_POOL_SIZE = "maxPoolSize";
    public static final String INITIAL_POOL_SIZE = "initialPoolSize";
    public static final String MIN_POOL_SIZE = "minPoolSize";
    public static final String MAX_STATEMENTS = "maxStatements";
    public static final String AQUIRE_INCREMENT = "acquireIncrement";

    // Pool timeout properties
    public static final String MAX_IDLE_TIMEOUT = "maxIdleTime";
    public static final String MAX_IDLE_TIME_EXCESS_CONN = "maxIdleTimeExcessConnections";
    public static final String CHECK_OUT_TIMEOUT_NAME = "checkoutTimeout";

    // Controller properties
    public static final String STAT_CACHE_NUM_DEFF_THREADS = "statementCacheNumDeferredCloseThreads";

    // Transaction properties
    public static final String AUTOCOMMIT_NAME = "autoCommit";
    public static final String AUTOCOMMIT_ON_CLOSE_NAME = "autoCommitOnClose";
    public static final String URESOLVED_TRANSACTIONS_NAME = "forceIgnoreUnresolvedTransactions";

    // Connection recovery properties
    public static final String ACQUIRE_RETRY_ATTEMPTS = "acquireRetryAttempts";
    public static final String ACQUIRE_RETRY_DELAY = "acquireRetryDelay";
    public static final String BREACK_AFTER_ACQUIRE_FAILURE = "breakAfterAcquireFailure";

    // ===========================================//
    // ================ Default Values ===========//
    // ===========================================//

    // Class loader properties
    public static final String CONTEXT_CLASS_LOADER_SOURCE_DEF = "library";
    public static final String PRIVILEGED_SPAWNED_THREADS_DEF = "true";

    // Pool properties default values
    public static final String MAX_POOL_SIZE_DEF_VALUE = "15";
    public static final String INITIAL_POOL_SIZE_DEF_VALUE = "5";
    public static final String MIN_POOL_SIZE_DEF_VALUE = "5";
    public static final String MAX_STATEMENTS_DEF_VALUE = "50";
    public static final String AQUIRE_INCREMENT_DEF_VALUE = "5";

    // Pool timeout properties default values
    public static final String MAX_IDLE_TIMEOUT_DEF_VALUE = "10000";
    public static final String MAX_IDLE_TIME_EXCESS_CONN_DEF_VALUE = "0";
    public static final String CHECK_OUT_TIMEOUT_DEF_VALUE = "1800";

    // Controller properties default values
    public static final String STAT_CACHE_NUM_DEFF_THREADS_DEF_VALUE = "1";

    // Transaction properties default values
    public static final String AUTOCOMMIT_DEF_VALUE = "false";
    public static final String AUTOCOMMIT_ON_CLOSE_DEF_VALUE = "false";
    public static final String URESOLVED_TRANSACTIONS_DEF_VALUE = "true";

    // Connection recovery properties default values
    public static final String ACQUIRE_RETRY_ATTEMPTS_DEF_VALUE = "0";
    public static final String ACQUIRE_RETRY_DELAY_DEF_VALUE = "1000";
    public static final String BREACK_AFTER_ACQUIRE_FAILURE_DEF_VALUE = "false";

    // ===========================================//
    // ===========End of default Values ===========//
    // ===========================================//

    // Default value for data source properties file
    private static final String POOL_PATH_DEF_VALUE = "META-INF/pool.properties";

    private String poolPath;

    private Map<Object, Object> poolProperties = new HashMap<Object, Object>();

    private boolean pooledDataSource;

    /**
     * Enumeration to choose which type connection pool should be in use
     * 
     * @author levan
     * 
     */
    public static enum PoolProviderType {

	DBCP, C3P0, TOMCAT;
    }

    // Default pool provider type
    private PoolProviderType poolProviderType = PoolProviderType.C3P0;

    /**
     * Sets default connection pooling properties
     * 
     * @return
     */
    public Map<Object, Object> getDefaultPooling() {
	Map<Object, Object> c3p0Properties = new HashMap<Object, Object>();

	// Add class loader properties

	// Added pool properties
	c3p0Properties.put(PoolConfig.MAX_POOL_SIZE,
		PoolConfig.MAX_POOL_SIZE_DEF_VALUE);
	c3p0Properties.put(PoolConfig.INITIAL_POOL_SIZE,
		PoolConfig.INITIAL_POOL_SIZE_DEF_VALUE);
	c3p0Properties.put(PoolConfig.MIN_POOL_SIZE,
		PoolConfig.MIN_POOL_SIZE_DEF_VALUE);
	c3p0Properties.put(PoolConfig.MAX_STATEMENTS,
		PoolConfig.MAX_STATEMENTS_DEF_VALUE);
	c3p0Properties.put(PoolConfig.AQUIRE_INCREMENT,
		PoolConfig.AQUIRE_INCREMENT_DEF_VALUE);

	// Added pool timeout properties
	c3p0Properties.put(PoolConfig.MAX_IDLE_TIMEOUT,
		PoolConfig.MAX_IDLE_TIMEOUT_DEF_VALUE);
	c3p0Properties.put(MAX_IDLE_TIME_EXCESS_CONN,
		MAX_IDLE_TIME_EXCESS_CONN_DEF_VALUE);
	c3p0Properties.put(CHECK_OUT_TIMEOUT_NAME, CHECK_OUT_TIMEOUT_DEF_VALUE);

	// Added controller properties
	c3p0Properties.put(STAT_CACHE_NUM_DEFF_THREADS,
		STAT_CACHE_NUM_DEFF_THREADS_DEF_VALUE);

	// Added transaction properties
	c3p0Properties.put(AUTOCOMMIT_NAME, AUTOCOMMIT_DEF_VALUE);
	c3p0Properties.put(URESOLVED_TRANSACTIONS_NAME,
		URESOLVED_TRANSACTIONS_DEF_VALUE);
	c3p0Properties.put(AUTOCOMMIT_ON_CLOSE_NAME,
		AUTOCOMMIT_ON_CLOSE_DEF_VALUE);

	// Added Connection recovery properties
	c3p0Properties.put(ACQUIRE_RETRY_ATTEMPTS,
		ACQUIRE_RETRY_ATTEMPTS_DEF_VALUE);
	c3p0Properties.put(ACQUIRE_RETRY_DELAY, ACQUIRE_RETRY_DELAY_DEF_VALUE);
	c3p0Properties.put(BREACK_AFTER_ACQUIRE_FAILURE,
		BREACK_AFTER_ACQUIRE_FAILURE_DEF_VALUE);

	return c3p0Properties;
    }

    private boolean checkModifiers(Field field) {

	int modifiers = MetaUtils.getModifiers(field);
	Class<?> fieldType = MetaUtils.getType(field);

	return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
		&& String.class.equals(fieldType);
    }

    private Set<Object> unsopportedKeys() throws IOException {

	Set<Object> keys = new HashSet<Object>();
	Field[] fields = DataSourceInitializer.class.getDeclaredFields();
	Object key;
	String apprEnd = "_PROPERTY";
	String name;
	for (Field field : fields) {
	    name = field.getName();
	    if (checkModifiers(field) && name.endsWith(apprEnd)) {
		key = MetaUtils.getFieldValue(field);
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
    private void fillDefaults(Map<Object, Object> defaults,
	    Map<Object, Object> initial) {

	defaults.putAll(initial);
    }

    /**
     * Generates pooling configuration properties
     * 
     * @param initial
     * @return {@link Map}<Object, Object>
     * @throws IOException
     */
    private Map<Object, Object> configProperties(Map<Object, Object> initial)
	    throws IOException {

	Map<Object, Object> propertiesMap = getDefaultPooling();
	fillDefaults(propertiesMap, initial);
	Set<Object> keys = unsopportedKeys();
	Object dataSourceName = null;
	for (Object key : keys) {
	    if (key.equals(DataSourceInitializer.NAME_PROPERTY)) {
		dataSourceName = propertiesMap
			.get(DataSourceInitializer.NAME_PROPERTY);
	    }
	    propertiesMap.remove(key);
	}
	if (ObjectUtils.notNull(dataSourceName)) {
	    propertiesMap.put(DATA_SOURCE_NAME, dataSourceName);
	}

	return propertiesMap;
    }

    /**
     * Gets property as <code>int</int> value
     * 
     * @param properties
     * @param key
     * @return <code>int</code>
     */
    public static Integer asInt(Map<Object, Object> properties, Object key) {

	Object property = properties.get(key);
	Integer propertyInt;
	if (property == null) {
	    propertyInt = null;
	} else if (property instanceof Integer) {
	    propertyInt = (Integer) property;
	} else if (property instanceof String) {
	    propertyInt = Integer.valueOf((String) property);
	} else {
	    propertyInt = null;
	}

	return propertyInt;
    }

    /**
     * Loads {@link Properties} from specific path
     * 
     * @param path
     * @return {@link Properties}
     * @throws IOException
     */
    public Map<Object, Object> load() throws IOException {

	InputStream stream;
	if (ObjectUtils.notAvailable(poolPath)) {
	    ClassLoader loader = LibraryLoader.getContextClassLoader();
	    stream = loader.getResourceAsStream(POOL_PATH_DEF_VALUE);
	} else {
	    File file = new File(poolPath);
	    stream = new FileInputStream(file);
	}
	try {
	    Map<Object, Object> properties;
	    Properties propertiesToLoad;
	    if (ObjectUtils.notNull(stream)) {
		propertiesToLoad = new Properties();
		propertiesToLoad.load(stream);
		properties = new HashMap<Object, Object>();
		properties.putAll(propertiesToLoad);
	    } else {
		properties = null;
	    }

	    return properties;
	} finally {
	    if (ObjectUtils.notNull(stream)) {
		stream.close();
	    }
	}
    }

    /**
     * Merges passed properties, startup time passed properties and properties
     * loaded from file
     * 
     * @param properties
     * @return {@link Map}<Object, Object> merged properties map
     * @throws IOException
     */
    public Map<Object, Object> merge(Map<Object, Object> properties)
	    throws IOException {

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

    public void setPoolProviderType(PoolProviderType poolProviderType) {
	this.poolProviderType = poolProviderType;
    }

    public void setPoolProviderType(String poolProviderTypeName) {
	this.poolProviderType = PoolProviderType.valueOf(poolProviderTypeName);
    }
}
