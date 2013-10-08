package org.lightmare.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lightmare.cache.DeploymentDirectory;
import org.lightmare.jpa.datasource.PoolConfig;
import org.lightmare.jpa.datasource.PoolConfig.PoolProviderType;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.IOUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.yaml.snakeyaml.Yaml;

/**
 * Easy way to retrieve configuration properties from configuration file
 * 
 * @author levan
 * @since 0.0.21-SNAPSHOT
 */
public class Configuration implements Cloneable {

    // Cache for all configuration passed from API or read from file
    private final Map<Object, Object> config = new HashMap<Object, Object>();

    // Instance of pool configuration
    private static final PoolConfig POOL_CONFIG = new PoolConfig();

    // Runtime to get available processors
    private static final Runtime RUNTIME = Runtime.getRuntime();

    // Resource path (META-INF)
    private static final String META_INF_PATH = "META-INF/";

    // Error messages
    private static final String COULD_NOT_LOAD_CONFIG_ERROR = "Could not load configuration";

    private static final String COULD_NOT_OPEN_FILE_ERROR = "Could not open config file";

    private static final String RESOURCE_NOT_EXISTS_ERROR = "Configuration resource doesn't exist";

    private static final Logger LOG = Logger.getLogger(Configuration.class);

    public Configuration() {
    }

    private <K, V> Map<K, V> getAsMap(Object key, Map<Object, Object> from) {

	if (from == null) {
	    from = config;
	}

	Map<K, V> value = ObjectUtils.cast(CollectionUtils.getAsMap(key, from));

	return value;
    }

    private <K, V> Map<K, V> getAsMap(Object key) {

	return getAsMap(key, null);
    }

    private <K, V> void setSubConfigValue(Object key, K subKey, V value) {

	Map<K, V> subConfig = getAsMap(key);
	if (subConfig == null) {
	    subConfig = new HashMap<K, V>();
	    config.put(key, subConfig);
	}

	subConfig.put(subKey, value);
    }

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

    private <K> boolean containsSubConfigKey(Object key, K subKey) {

	Map<K, ?> subConfig = getAsMap(key);

	boolean valid = CollectionUtils.valid(subConfig);

	if (valid) {
	    valid = subConfig.containsKey(subKey);
	}

	return valid;
    }

    private <K> boolean containsConfigKey(K key) {

	return containsSubConfigKey(ConfigKeys.DEPLOY_CONFIG.key, key);
    }

    private <K, V> V getSubConfigValue(Object key, K subKey) {

	return getSubConfigValue(key, subKey, null);
    }

    private <K, V> void setConfigValue(K subKey, V value) {

	setSubConfigValue(ConfigKeys.DEPLOY_CONFIG.key, subKey, value);
    }

    private <K, V> V getConfigValue(K subKey, V defaultValue) {

	return getSubConfigValue(ConfigKeys.DEPLOY_CONFIG.key, subKey,
		defaultValue);
    }

    private <K, V> V getConfigValue(K subKey) {

	return getSubConfigValue(ConfigKeys.DEPLOY_CONFIG.key, subKey);
    }

    private <K, V> Map<K, V> getWithInitialization(Object key) {

	Map<K, V> result = getConfigValue(key);

	if (result == null) {
	    result = new HashMap<K, V>();
	    setConfigValue(key, result);
	}

	return result;
    }

    private <K, V> void setWithInitialization(Object key, K subKey, V value) {

	Map<K, V> result = getWithInitialization(key);

	result.put(subKey, value);
    }

    /**
     * Gets value for specific key from connection persistence sub {@link Map}
     * of configuration if value is null then returns passed default value
     * 
     * @param key
     * @return <code>V</code>
     */
    public <V> V getPersistenceConfigValue(Object key, V defaultValue) {

	V value = CollectionUtils.getSubValue(config,
		ConfigKeys.DEPLOY_CONFIG.key,
		ConfigKeys.PERSISTENCE_CONFIG.key, key);

	if (value == null) {
	    value = defaultValue;
	}

	return value;
    }

    /**
     * Gets value for specific key from connection persistence sub {@link Map}
     * of configuration
     * 
     * @param key
     * @return <code>V</code>
     */
    public <V> V getPersistenceConfigValue(Object key) {

	return getPersistenceConfigValue(key, null);
    }

    /**
     * Sets specific value for appropriated key in persistence configuration sub
     * {@link Map} of configuration
     * 
     * @param key
     * @param value
     */
    public void setPersistenceConfigValue(Object key, Object value) {

	setWithInitialization(ConfigKeys.PERSISTENCE_CONFIG.key, key, value);
    }

    /**
     * Gets value for specific key from connection pool configuration sub
     * {@link Map} of configuration if value is null then returns passed default
     * value
     * 
     * @param key
     * @return <code>V</code>
     */
    public <V> V getPoolConfigValue(Object key, V defaultValue) {

	V value = CollectionUtils.getSubValue(config,
		ConfigKeys.DEPLOY_CONFIG.key, ConfigKeys.POOL_CONFIG.key, key);

	if (value == null) {
	    value = defaultValue;
	}

	return value;
    }

    /**
     * Gets value for specific key from connection pool configuration sub
     * {@link Map} of configuration
     * 
     * @param key
     * @return <code>V</code>
     */
    public <V> V getPoolConfigValue(Object key) {

	V value = getPoolConfigValue(key, null);

	return value;
    }

    /**
     * Sets specific value for appropriated key in connection pool configuration
     * sub {@link Map} of configuration
     * 
     * @param key
     * @param value
     */
    public void setPoolConfigValue(Object key, Object value) {

	setWithInitialization(ConfigKeys.POOL_CONFIG.key, key, value);
    }

    /**
     * Configuration for {@link PoolConfig} instance
     */
    private void configurePool() {

	Map<Object, Object> poolProperties = getPoolConfigValue(ConfigKeys.POOL_PROPERTIES.key);
	if (CollectionUtils.valid(poolProperties)) {

	    setPoolProperties(poolProperties);
	}

	String type = getPoolConfigValue(ConfigKeys.POOL_PROVIDER_TYPE.key);
	if (StringUtils.valid(type)) {
	    getPoolConfig().setPoolProviderType(type);
	}

	String path = getPoolConfigValue(ConfigKeys.POOL_PROPERTIES_PATH.key);
	if (StringUtils.valid(path)) {
	    setPoolPropertiesPath(path);
	}
    }

    /**
     * Configures server from properties and default values
     */
    private void configureServer() {

	// Sets default values to remote server configuration
	boolean contains = containsConfigKey(ConfigKeys.IP_ADDRESS.key);
	if (ObjectUtils.notTrue(contains)) {
	    setConfigValue(ConfigKeys.IP_ADDRESS.key,
		    ConfigKeys.IP_ADDRESS.value);
	}

	contains = containsConfigKey(ConfigKeys.PORT.key);
	if (ObjectUtils.notTrue(contains)) {
	    setConfigValue(ConfigKeys.PORT.key, ConfigKeys.PORT.value);
	}

	contains = containsConfigKey(ConfigKeys.BOSS_POOL.key);
	if (ObjectUtils.notTrue(contains)) {
	    setConfigValue(ConfigKeys.BOSS_POOL.key, ConfigKeys.BOSS_POOL.value);
	}

	contains = containsConfigKey(ConfigKeys.WORKER_POOL.key);
	if (ObjectUtils.notTrue(contains)) {

	    int defaultWorkers = ConfigKeys.WORKER_POOL.getValue();
	    int workers = RUNTIME.availableProcessors() * defaultWorkers;
	    String workerProperty = String.valueOf(workers);
	    setConfigValue(ConfigKeys.WORKER_POOL.key, workerProperty);
	}

	contains = containsConfigKey(ConfigKeys.CONNECTION_TIMEOUT.key);
	if (ObjectUtils.notTrue(contains)) {
	    setConfigValue(ConfigKeys.CONNECTION_TIMEOUT.key,
		    ConfigKeys.CONNECTION_TIMEOUT.value);
	}
    }

    /**
     * Merges configuration with default properties
     */
    public void configureDeployments() {

	// Checks if application run in hot deployment mode
	Boolean hotDeployment = getConfigValue(ConfigKeys.HOT_DEPLOYMENT.key);
	if (hotDeployment == null) {
	    setConfigValue(ConfigKeys.HOT_DEPLOYMENT.key, Boolean.FALSE);
	    hotDeployment = getConfigValue(ConfigKeys.HOT_DEPLOYMENT.key);
	}

	// Check if application needs directory watch service
	boolean watchStatus;

	if (ObjectUtils.notTrue(hotDeployment)) {
	    watchStatus = Boolean.TRUE;
	} else {
	    watchStatus = Boolean.FALSE;
	}

	setConfigValue(ConfigKeys.WATCH_STATUS.key, watchStatus);

	// Sets deployments directories
	Set<DeploymentDirectory> deploymentPaths = getConfigValue(ConfigKeys.DEMPLOYMENT_PATH.key);
	if (deploymentPaths == null) {
	    deploymentPaths = ConfigKeys.DEMPLOYMENT_PATH.getValue();
	    setConfigValue(ConfigKeys.DEMPLOYMENT_PATH.key, deploymentPaths);
	}
    }

    /**
     * Configures server and connection pooling
     */
    public void configure() {

	configureServer();
	configureDeployments();
	configurePool();
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
	    Object key;
	    Map<Object, Object> value1;
	    Object value2;
	    Map<Object, Object> mapValue2;
	    Object mergedValue;
	    for (Map.Entry<Object, Object> entry2 : entries2) {
		key = entry2.getKey();
		value2 = entry2.getValue();
		if (value2 instanceof Map) {
		    value1 = CollectionUtils.getAsMap(key, map1);
		    mapValue2 = ObjectUtils.cast(value2);
		    mergedValue = deepMerge(value1, mapValue2);
		} else {
		    mergedValue = value2;
		}

		if (ObjectUtils.notNull(mergedValue)) {
		    map1.put(key, mergedValue);
		}
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
     * Reads configuration from passed file path
     * 
     * @param configuration
     */
    public void configure(String path) throws IOException {

	File yamlFile = new File(path);
	if (yamlFile.exists()) {

	    InputStream stream = new FileInputStream(yamlFile);
	    try {
		Yaml yaml = new Yaml();
		Object configuration = yaml.load(stream);

		if (configuration instanceof Map) {
		    Map<Object, Object> innerConfig = ObjectUtils
			    .cast(configuration);
		    configure(innerConfig);
		}
	    } finally {
		IOUtils.close(stream);
	    }
	}
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
     * Loads configuration from file contained in classpath
     * 
     * @param resourceName
     * @param loader
     */
    public void loadFromResource(String resourceName, ClassLoader loader)
	    throws IOException {

	InputStream resourceStream = loader.getResourceAsStream(StringUtils
		.concat(META_INF_PATH, resourceName));
	if (resourceStream == null) {
	    LOG.error(RESOURCE_NOT_EXISTS_ERROR);
	} else {
	    loadFromStream(resourceStream);
	}
    }

    public static String getAdminUsersPath() {
	return ConfigKeys.ADMIN_USERS_PATH.getValue();
    }

    public static void setAdminUsersPath(String adminUsersPath) {
	ConfigKeys.ADMIN_USERS_PATH.value = adminUsersPath;
    }

    public static void setRemoteContron(boolean remoteControl) {
	ConfigKeys.REMOTE_CONTROL.value = remoteControl;
    }

    public static boolean getRemoteControl() {
	return ConfigKeys.REMOTE_CONTROL.getValue();
    }

    public boolean isRemote() {

	return ConfigKeys.REMOTE.getValue();
    }

    public void setRemote(boolean remote) {
	ConfigKeys.REMOTE.value = remote;
    }

    public static boolean isServer() {

	return ConfigKeys.SERVER.getValue();
    }

    public static void setServer(boolean server) {

	ConfigKeys.SERVER.value = server;
    }

    public boolean isClient() {

	return getConfigValue(ConfigKeys.CLIENT.key, Boolean.FALSE);
    }

    public void setClient(boolean client) {
	setConfigValue(ConfigKeys.CLIENT.key, client);
    }

    /**
     * Adds path for deployments file or directory
     * 
     * @param path
     * @param scan
     */
    public void addDeploymentPath(String path, boolean scan) {

	Set<DeploymentDirectory> deploymentPaths = getConfigValue(ConfigKeys.DEMPLOYMENT_PATH.key);
	if (deploymentPaths == null) {
	    deploymentPaths = new HashSet<DeploymentDirectory>();
	    setConfigValue(ConfigKeys.DEMPLOYMENT_PATH.key, deploymentPaths);
	}

	deploymentPaths.add(new DeploymentDirectory(path, scan));
    }

    /**
     * Adds path for data source file
     * 
     * @param path
     */
    public void addDataSourcePath(String path) {

	Set<String> dataSourcePaths = getConfigValue(ConfigKeys.DATA_SOURCE_PATH.key);
	if (dataSourcePaths == null) {
	    dataSourcePaths = new HashSet<String>();
	    setConfigValue(ConfigKeys.DATA_SOURCE_PATH.key, dataSourcePaths);
	}

	dataSourcePaths.add(path);
    }

    public Set<DeploymentDirectory> getDeploymentPath() {

	return getConfigValue(ConfigKeys.DEMPLOYMENT_PATH.key);
    }

    public Set<String> getDataSourcePath() {

	return getConfigValue(ConfigKeys.DATA_SOURCE_PATH.key);
    }

    public String[] getLibraryPaths() {

	return getConfigValue(ConfigKeys.LIBRARY_PATH.key);
    }

    public void setLibraryPaths(String[] libraryPaths) {

	setConfigValue(ConfigKeys.LIBRARY_PATH.key, libraryPaths);
    }

    public boolean isHotDeployment() {

	return getConfigValue(ConfigKeys.HOT_DEPLOYMENT.key, Boolean.FALSE);
    }

    public void setHotDeployment(boolean hotDeployment) {

	setConfigValue(ConfigKeys.HOT_DEPLOYMENT.key, hotDeployment);
    }

    public boolean isWatchStatus() {

	return getConfigValue(ConfigKeys.WATCH_STATUS.key, Boolean.FALSE);
    }

    public void setWatchStatus(boolean watchStatus) {
	setConfigValue(ConfigKeys.WATCH_STATUS.key, watchStatus);
    }

    /**
     * Property for persistence configuration
     * 
     * @return <code>boolean</code>
     */
    public boolean isScanForEntities() {

	return getPersistenceConfigValue(ConfigKeys.SCAN_FOR_ENTITIES.key,
		Boolean.FALSE);
    }

    public void setScanForEntities(boolean scanForEntities) {

	setPersistenceConfigValue(ConfigKeys.SCAN_FOR_ENTITIES.key,
		scanForEntities);
    }

    public String getAnnotatedUnitName() {

	return getPersistenceConfigValue(ConfigKeys.ANNOTATED_UNIT_NAME.key);
    }

    public void setAnnotatedUnitName(String annotatedUnitName) {

	setPersistenceConfigValue(ConfigKeys.ANNOTATED_UNIT_NAME.key,
		annotatedUnitName);
    }

    public String getPersXmlPath() {

	return getPersistenceConfigValue(ConfigKeys.PERSISTENCE_XML_PATH.key);
    }

    public void setPersXmlPath(String persXmlPath) {

	setPersistenceConfigValue(ConfigKeys.PERSISTENCE_XML_PATH.key,
		persXmlPath);
    }

    public boolean isPersXmlFromJar() {

	return getPersistenceConfigValue(
		ConfigKeys.PERSISTENCE_XML_FROM_JAR.key, Boolean.FALSE);
    }

    public void setPersXmlFromJar(boolean persXmlFromJar) {

	setPersistenceConfigValue(ConfigKeys.PERSISTENCE_XML_FROM_JAR.key,
		persXmlFromJar);
    }

    public boolean isSwapDataSource() {

	return getPersistenceConfigValue(ConfigKeys.SWAP_DATASOURCE.key,
		Boolean.FALSE);
    }

    public void setSwapDataSource(boolean swapDataSource) {

	setPersistenceConfigValue(ConfigKeys.SWAP_DATASOURCE.key,
		swapDataSource);
    }

    public boolean isScanArchives() {

	return getPersistenceConfigValue(ConfigKeys.SCAN_ARCHIVES.key,
		Boolean.FALSE);
    }

    public void setScanArchives(boolean scanArchives) {

	setPersistenceConfigValue(ConfigKeys.SCAN_ARCHIVES.key, scanArchives);
    }

    public boolean isPooledDataSource() {

	return getPersistenceConfigValue(ConfigKeys.POOLED_DATA_SOURCE.key,
		Boolean.FALSE);
    }

    public void setPooledDataSource(boolean pooledDataSource) {

	setPersistenceConfigValue(ConfigKeys.POOLED_DATA_SOURCE.key,
		pooledDataSource);
    }

    public Map<Object, Object> getPersistenceProperties() {

	return getPersistenceConfigValue(ConfigKeys.PERSISTENCE_PROPERTIES.key);
    }

    public void setPersistenceProperties(
	    Map<Object, Object> persistenceProperties) {

	setPersistenceConfigValue(ConfigKeys.PERSISTENCE_PROPERTIES.key,
		persistenceProperties);
    }

    /**
     * Gets cached {@link PoolConfig} instance a connection pool configuration
     * 
     * @return {@link PoolConfig}
     */
    public static PoolConfig getPoolConfig() {

	return POOL_CONFIG;
    }

    public void setDataSourcePooledType(boolean dsPooledType) {

	PoolConfig poolConfig = getPoolConfig();
	poolConfig.setPooledDataSource(dsPooledType);
    }

    public void setPoolPropertiesPath(String path) {

	PoolConfig poolConfig = getPoolConfig();
	poolConfig.setPoolPath(path);
    }

    public void setPoolProperties(
	    Map<? extends Object, ? extends Object> properties) {

	PoolConfig poolConfig = getPoolConfig();
	poolConfig.getPoolProperties().putAll(properties);
    }

    public void addPoolProperty(Object key, Object value) {

	PoolConfig poolConfig = getPoolConfig();
	poolConfig.getPoolProperties().put(key, value);
    }

    public void setPoolProviderType(PoolProviderType poolProviderType) {

	PoolConfig poolConfig = getPoolConfig();
	poolConfig.setPoolProviderType(poolProviderType);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

	// Deep clone for configuration
	Configuration cloneConfig = (Configuration) super.clone();
	cloneConfig.config.clear();
	cloneConfig.configure(this.config);

	return cloneConfig;
    }
}
