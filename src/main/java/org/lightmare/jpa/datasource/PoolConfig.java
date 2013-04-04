package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.lightmare.utils.ObjectUtils;

/**
 * Configuration with default parameters for c3p0 connection pooling
 * 
 * @author levan
 * 
 */
public class PoolConfig {

    public static final String MAX_POOL_SIZE = "maxPoolSize";
    public static final String INITIAL_POOL_SIZE = "initialPoolSize";
    public static final String MIN_POOL_SIZE = "minPoolSize";
    public static final String MAX_IDLE_TIMEOUT = "maxIdleTime";
    public static final String MAX_STATEMENTS = "maxStatements";
    public static final String AQUIRE_INCREMENT = "acquireIncrement";
    public static final String MAX_IDLE_TIME_EXCESS_CONN = "maxIdleTimeExcessConnections";
    public static final String STAT_CACHE_NUM_DEFF_THREADS = "statementCacheNumDeferredCloseThreads";

    public static final String MAX_POOL_SIZE_DEF_VALUE = "15";
    public static final String INITIAL_POOL_SIZE_DEF_VALUE = "5";
    public static final String MIN_POOL_SIZE_DEF_VALUE = "5";
    public static final String MAX_IDLE_TIMEOUT_DEF_VALUE = "0";
    public static final String MAX_STATEMENTS_DEF_VALUE = "50";
    public static final String AQUIRE_INCREMENT_DEF_VALUE = "50";
    public static final String MAX_IDLE_TIME_EXCESS_CONN_DEF_VALUE = "0";
    public static final String STAT_CACHE_NUM_DEFF_THREADS_DEF_VALUE = "1";

    public static enum PoolProviderType {

	C3P0, TOMCAT;
    }

    public static PoolProviderType poolProviderType = PoolProviderType.C3P0;

    /**
     * Sets default connection pooling properties
     * 
     * @return
     */
    public static Map<Object, Object> getDefaultPooling() {
	Map<Object, Object> c3p0Properties = new HashMap<Object, Object>();
	c3p0Properties.put(PoolConfig.MAX_POOL_SIZE,
		PoolConfig.MAX_POOL_SIZE_DEF_VALUE);
	c3p0Properties.put(PoolConfig.INITIAL_POOL_SIZE,
		PoolConfig.INITIAL_POOL_SIZE_DEF_VALUE);
	c3p0Properties.put(PoolConfig.MIN_POOL_SIZE,
		PoolConfig.MIN_POOL_SIZE_DEF_VALUE);
	c3p0Properties.put(PoolConfig.MAX_IDLE_TIMEOUT,
		PoolConfig.MAX_IDLE_TIMEOUT_DEF_VALUE);
	c3p0Properties.put(PoolConfig.MAX_STATEMENTS,
		PoolConfig.MAX_STATEMENTS_DEF_VALUE);
	c3p0Properties.put(PoolConfig.AQUIRE_INCREMENT,
		PoolConfig.AQUIRE_INCREMENT_DEF_VALUE);

	return c3p0Properties;
    }

    /**
     * Add single property to defaults
     * 
     * @param defaults
     * @param initial
     * @param key
     */
    private static void setProperty(Map<Object, Object> defaults,
	    Map<Object, Object> initial, Object key) {

	Object property;
	if (initial.containsKey(key)) {
	    property = initial.get(key);
	    defaults.put(key, property);
	}
    }

    /**
     * Add initialized properties to defaults
     * 
     * @param defaults
     * @param initial
     */
    private static void fillDefaults(Map<Object, Object> defaults,
	    Map<Object, Object> initial) {

	Set<Object> keys = defaults.keySet();
	for (Object key : keys) {
	    setProperty(defaults, initial, key);
	}
    }

    /**
     * Generates pooling configuration properties
     * 
     * @param initial
     * @return
     */
    public static Map<Object, Object> configProperties(
	    Map<Object, Object> initial) {

	Map<Object, Object> propertiesMap = getDefaultPooling();
	fillDefaults(propertiesMap, initial);

	return propertiesMap;
    }

    public static int asInt(Map<Object, Object> properties, Object key) {

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
    public static Properties load(String path) throws IOException {

	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	if (!ObjectUtils.available(path)) {
	    path = "META-INF/pool.properties";
	}
	InputStream stream = loader.getResourceAsStream(path);
	try {
	    Properties properties;
	    if (ObjectUtils.notNull(stream)) {
		properties = new Properties();
		properties.load(stream);
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
}
