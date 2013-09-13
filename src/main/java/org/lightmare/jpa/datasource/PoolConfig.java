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

    public static enum DefaultConfig {

	// Data source name property
	DATA_SOURCE_NAME("dataSourceName"),

	// ===========================================//
	// ====== Data Source properties =============//
	// ===========================================//

	// Class loader properties
	CONTEXT_CLASS_LOADER_SOURCE("contextClassLoaderSource", "library"), // loader
	PRIVILEGED_SPAWNED_THREADS("privilegeSpawnedThreads", "true"), // threads

	// Pool properties
	MAX_POOL_SIZE("maxPoolSize", "15"), // max pool size
	INITIAL_POOL_SIZE("initialPoolSize", "5"), // initial
	MIN_POOL_SIZE("minPoolSize", "5"), // min pool size
	MAX_STATEMENTS("maxStatements", "50"), // statements
	AQUIRE_INCREMENT("acquireIncrement", "5"), // increment

	// Pool timeout properties
	MAX_IDLE_TIMEOUT("maxIdleTime", "10000"), // idle
	MAX_IDLE_TIME_EXCESS_CONN("maxIdleTimeExcessConnections", "0"), // excess
	CHECK_OUT_TIMEOUT("checkoutTimeout", "1800"), // checkout

	// Controller properties
	STAT_CACHE_NUM_DEFF_THREADS("statementCacheNumDeferredCloseThreads",
		"1"),

	// Transaction properties
	AUTOCOMMIT("autoCommit", "false"), // auto commit
	AUTOCOMMIT_ON_CLOSE("autoCommitOnClose", "false"), // on close
	URESOLVED_TRANSACTIONS("forceIgnoreUnresolvedTransactions", "true"), // ignore

	// Connection recovery properties
	ACQUIRE_RETRY_ATTEMPTS("acquireRetryAttempts", "0"), // retry
	ACQUIRE_RETRY_DELAY("acquireRetryDelay", "1000"), // delay
	BREACK_AFTER_ACQUIRE_FAILURE("breakAfterAcquireFailure", "false");// break

	public String key;

	public String value;

	private DefaultConfig(String key) {
	    this.key = key;
	}

	private DefaultConfig(String key, Object value) {
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

	DefaultConfig[] defaults = DefaultConfig.values();

	String key;
	String value;
	for (DefaultConfig config : defaults) {
	    key = config.key;
	    value = config.value;
	    if (ObjectUtils.available(key) && ObjectUtils.available(value)) {
		c3p0Properties.put(key, value);
	    }
	}

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
	    if (key.equals(DataSourceInitializer.ConnectionProperties.NAME_PROPERTY.property)) {
		dataSourceName = propertiesMap
			.get(DataSourceInitializer.ConnectionProperties.NAME_PROPERTY.property);
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
     * Gets property as <code>int</int> value
     * 
     * @param properties
     * @param key
     * @return <code>int</code>
     */
    public static Integer asInt(Map<Object, Object> properties,
	    DefaultConfig config) {

	String key = config.key;
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
