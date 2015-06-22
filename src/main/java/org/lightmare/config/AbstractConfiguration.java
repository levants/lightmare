package org.lightmare.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.io.IOUtils;

/**
 * Configuration utilities in sbtract class
 * 
 * @author Levan Tsinadze
 * @since 0.1.4
 */
public abstract class AbstractConfiguration implements Cloneable {

    // Cache for all configuration passed from API or read from file
    protected final Map<Object, Object> config = new HashMap<Object, Object>();

    // Error messages
    private static final String COULD_NOT_LOAD_CONFIG_ERROR = "Could not load configuration";

    private static final String COULD_NOT_OPEN_FILE_ERROR = "Could not open config file";

    private static final Logger LOG = Logger
	    .getLogger(AbstractConfiguration.class);

    /**
     * Gets value on passed generic key K of passed {@link Map} as {@link Map}
     * of generic key values
     * 
     * @param key
     * @param from
     * @return {@link Map}<code><K, V></code>
     */
    private <K, V> Map<K, V> getAsMap(Object key, Map<Object, Object> from) {

	if (from == null) {
	    from = config;
	}
	// Gets value associated with key as map
	Map<K, V> value = ObjectUtils.cast(CollectionUtils.getAsMap(key, from));

	return value;
    }

    /**
     * Gets value on passed generic key K of cached configuration as {@link Map}
     * of generic key values
     * 
     * @param key
     * @return {@link Map}<code><K, V></code>
     */
    private <K, V> Map<K, V> getAsMap(Object key) {
	return getAsMap(key, null);
    }

    /**
     * Sets value of sub {@link Map} on passed sub key contained in cached
     * configuration on passed key
     * 
     * @param key
     * @param subKey
     * @param value
     */
    private <K, V> void setSubConfigValue(Object key, K subKey, V value) {

	Map<K, V> subConfig = getAsMap(key);
	if (subConfig == null) {
	    subConfig = new HashMap<K, V>();
	    config.put(key, subConfig);
	}

	subConfig.put(subKey, value);
    }

    /**
     * Gets value of sub {@link Map} on passed sub key contained in cached
     * configuration on passed key
     * 
     * @param key
     * @param subKey
     * @param defaultValue
     * @return V
     */
    private <K, V> V getSubConfigValue(Object key, K subKey, V defaultValue) {

	V def;

	Map<K, V> subConfig = getAsMap(key);
	if (CollectionUtils.valid(subConfig)) {
	    def = subConfig.get(subKey);
	    if (def == null) {
		def = defaultValue;
	    }
	} else {
	    def = defaultValue;
	}

	return def;
    }

    /**
     * Check if sub {@link Map} contains passed sub key contained in cached
     * configuration on passed key
     * 
     * @param key
     * @param subKey
     * @return <code>boolean</code>
     */
    private <K> boolean containsSubConfigKey(Object key, K subKey) {

	boolean valid;

	Map<K, ?> subConfig = getAsMap(key);
	valid = CollectionUtils.valid(subConfig);
	if (valid) {
	    valid = subConfig.containsKey(subKey);
	}

	return valid;
    }

    /**
     * Checks if configuration contains passed key
     * 
     * @param key
     * @return <code>boolean</code>
     */
    protected <K> boolean containsConfigKey(K key) {
	return containsSubConfigKey(ConfigKeys.DEPLOY_CONFIG.key, key);
    }

    /**
     * Gets value from sub configuration for passed sub key contained in
     * configuration for passed key
     * 
     * @param key
     * @param subKey
     * @return <coder>V</code>
     */
    private <K, V> V getSubConfigValue(Object key, K subKey) {
	return getSubConfigValue(key, subKey, null);
    }

    /**
     * Sets sub configuration configuration value for passed sub key for default
     * configuration key
     * 
     * @param subKey
     * @param value
     */
    protected <K, V> void setConfigValue(K subKey, V value) {
	setSubConfigValue(ConfigKeys.DEPLOY_CONFIG.key, subKey, value);
    }

    /**
     * Gets value from sub configuration for passed sub key contained in
     * configuration for default configuration key and if such not exists
     * returns passed default value
     * 
     * @param subKey
     * @param defaultValue
     * @return <coder>V</code>
     */
    protected <K, V> V getConfigValue(K subKey, V defaultValue) {
	return getSubConfigValue(ConfigKeys.DEPLOY_CONFIG.key, subKey,
		defaultValue);
    }

    /**
     * Gets value from sub configuration for passed sub key contained in
     * configuration for default configuration key
     * 
     * @param subKey
     * @param defaultValue
     * @return <coder>V</code>
     */
    protected <K, V> V getConfigValue(K subKey) {
	return getSubConfigValue(ConfigKeys.DEPLOY_CONFIG.key, subKey);
    }

    /**
     * Gets {@link Map} value from configuration with passed key and if such
     * does not exists creates and puts new instance
     * 
     * @param key
     * @return {@link Map}<code><K, V></code>
     */
    private <K, V> Map<K, V> getWithInitialization(Object key) {

	Map<K, V> result = getConfigValue(key);

	if (result == null) {
	    result = new HashMap<K, V>();
	    setConfigValue(key, result);
	}

	return result;
    }

    /**
     * Sets sub configuration configuration value for passed sub key for passed
     * configuration key (if sub configuration does not exists creates and puts
     * new instance)
     * 
     * @param subKey
     * @param value
     */
    protected <K, V> void setWithInitialization(Object key, K subKey, V value) {
	Map<K, V> result = getWithInitialization(key);
	result.put(subKey, value);
    }

    protected <K, V> void setIfContains(K key, V value) {

	boolean contains = containsConfigKey(key);
	if (Boolean.FALSE.equals(contains)) {
	    setConfigValue(key, value);
	}
    }

    /**
     * Merges key and value from passed {@link java.util.Map.Entry} and passed
     * {@link java.util.Map}'s appropriated key and value
     * 
     * @param map
     * @param entry
     */
    private void deepMerge(Map<Object, Object> map,
	    Map.Entry<Object, Object> entry) {

	Object key = entry.getKey();
	Object value2 = entry.getValue();
	Object mergedValue;
	if (value2 instanceof Map) {
	    Map<Object, Object> value1 = CollectionUtils.getAsMap(key, map);
	    Map<Object, Object> mapValue2 = ObjectUtils.cast(value2);
	    mergedValue = deepMerge(value1, mapValue2);
	} else {
	    mergedValue = value2;
	}
	// Caches merged value
	if (ObjectUtils.notNull(mergedValue)) {
	    map.put(key, mergedValue);
	}
    }

    /**
     * Merges two {@link Map}s and if second {@link Map}'s value is instance of
     * {@link Map} merges this value with first {@link Map}'s value recursively
     * 
     * @param map1
     * @param map2
     * @return <code>{@link Map}<Object, Object></code>
     */
    protected Map<Object, Object> deepMerge(Map<Object, Object> map1,
	    Map<Object, Object> map2) {

	if (map1 == null) {
	    map1 = map2;
	} else {
	    Set<Map.Entry<Object, Object>> entries2 = map2.entrySet();
	    for (Map.Entry<Object, Object> entry2 : entries2) {
		deepMerge(map1, entry2);
	    }
	}

	return map1;
    }

    /**
     * Reads configuration from passed properties
     * 
     * @param configuration
     */
    public void configure(Map<Object, Object> configuration) {
	deepMerge(config, configuration);
    }

    /**
     * Gets value associated with particular key as {@link String} instance
     * 
     * @param key
     * @return {@link String}
     */
    public String getStringValue(String key) {

	String textValue;

	Object value = config.get(key);
	if (value == null) {
	    textValue = null;
	} else {
	    textValue = value.toString();
	}

	return textValue;
    }

    /**
     * Gets value associated with particular key as <code>int</code> instance
     * 
     * @param key
     * @return {@link String}
     */
    public int getIntValue(String key) {
	String value = getStringValue(key);
	return Integer.parseInt(value);
    }

    /**
     * Gets value associated with particular key as <code>long</code> instance
     * 
     * @param key
     * @return {@link String}
     */
    public long getLongValue(String key) {
	String value = getStringValue(key);
	return Long.parseLong(value);
    }

    /**
     * Gets value associated with particular key as <code>boolean</code>
     * instance
     * 
     * @param key
     * @return {@link String}
     */
    public boolean getBooleanValue(String key) {

	String value = getStringValue(key);

	return Boolean.parseBoolean(value);
    }

    public void putValue(String key, String value) {
	config.put(key, value);
    }

    /**
     * Load {@link Configuration} in memory as {@link Map} of parameters
     * 
     * @param propertiesStream
     * @throws IOException
     */
    public void loadFromStream(InputStream propertiesStream) throws IOException {

	try {
	    Properties props = new Properties();
	    props.load(propertiesStream);

	    for (String propertyName : props.stringPropertyNames()) {
		config.put(propertyName, props.getProperty(propertyName));
	    }
	} catch (IOException ex) {
	    LOG.error(COULD_NOT_LOAD_CONFIG_ERROR, ex);
	} finally {
	    IOUtils.close(propertiesStream);
	}
    }

    /**
     * Loads configuration form file
     * 
     * @throws IOException
     */
    public void loadFromFile() throws IOException {

	String configFilePath = ConfigKeys.CONFIG_FILE.getValue();

	try {
	    File configFile = new File(configFilePath);
	    if (configFile.exists()) {
		InputStream propertiesStream = new FileInputStream(configFile);
		loadFromStream(propertiesStream);
	    } else {
		configFile.mkdirs();
	    }
	} catch (IOException ex) {
	    LOG.error(COULD_NOT_OPEN_FILE_ERROR, ex);
	}
    }

    /**
     * Loads configuration form file by passed file path
     * 
     * @param configFilename
     * @throws IOException
     */
    public void loadFromFile(String configFilename) throws IOException {

	try {
	    InputStream propertiesStream = new FileInputStream(new File(
		    configFilename));
	    loadFromStream(propertiesStream);
	} catch (IOException ex) {
	    LOG.error(COULD_NOT_OPEN_FILE_ERROR, ex);
	}
    }

    /**
     * Clears existed configuration parameters
     */
    protected void clear() {
	config.clear();
    }

    @Override
    public Configuration clone() throws CloneNotSupportedException {

	// Deep clone for configuration
	Configuration cloneConfig;

	Object cloneObject = super.clone();
	// Casting cloned object to the appropriated type
	cloneConfig = ObjectUtils.cast(cloneObject, Configuration.class);
	// Coping configuration for cloned data
	Map<Object, Object> copyConfig = new HashMap<Object, Object>(config);
	// copyConfig.putAll(this.config);
	cloneConfig.clear();
	cloneConfig.configure(copyConfig);

	return cloneConfig;
    }
}
