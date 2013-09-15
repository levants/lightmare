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
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.yaml.snakeyaml.Yaml;

/**
 * Easy way to retrieve configuration properties from configuration file
 * 
 * @author levan
 * 
 */
public class Configuration implements Cloneable {

    // Cache for all configuration passed programmatically or read from file
    private final Map<Object, Object> config = new HashMap<Object, Object>();

    // Runtime to get available processors
    private static final Runtime RUNTIME = Runtime.getRuntime();

    public static final String DATA_SOURCE_PATH_DEF = "./ds";

    // Properties which version of server is running remote it requires server
    // client RPC infrastructure or local (embedded mode)

    private static final String CONFIG_FILE = "./config/configuration.yaml";

    // Configuration keys properties for deployment

    private static final String HOT_DEPLOYMENT_KEY = "hotDeployment";

    private static final String WATCH_STATUS_KEY = "watchStatus";

    private static final String LIBRARY_PATH_KEY = "libraryPaths";

    // Persistence provider property keys
    private static final String PERSISTENCE_CONFIG_KEY = "persistenceConfig";

    private static final String SCAN_FOR_ENTITIES_KEY = "scanForEntities";

    private static final String ANNOTATED_UNIT_NAME_KEY = "annotatedUnitName";

    private static final String PERSISTENCE_XML_PATH_KEY = "persistanceXmlPath";

    private static final String PERSISTENCE_XML_FROM_JAR_KEY = "persistenceXmlFromJar";

    private static final String SWAP_DATASOURCE_KEY = "swapDataSource";

    private static final String SCAN_ARCHIVES_KEY = "scanArchives";

    private static final String POOLED_DATA_SOURCE_KEY = "pooledDataSource";

    private static final String PERSISTENCE_PROPERTIES_KEY = "persistenceProperties";

    // Connection pool provider property keys
    private static final String POOL_CONFIG_KEY = "poolConfig";

    private static final String POOL_PROPERTIES_PATH_KEY = "poolPropertiesPath";

    private static final String POOL_PROVIDER_TYPE_KEY = "poolProviderType";

    private static final String POOL_PROPERTIES_KEY = "poolProperties";

    // Configuration properties for deployment
    private static String ADMIN_USERS_PATH;

    // Is configuration server or client (default is server)
    private static boolean server = (Boolean) Config.SERVER.value;

    private static boolean remote;

    // Instance of pool configuration
    private static final PoolConfig POOL_CONFIG = new PoolConfig();

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

	@SuppressWarnings("unchecked")
	Map<K, V> value = (Map<K, V>) ObjectUtils.getAsMap(key, from);

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
	if (ObjectUtils.available(subConfig)) {
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
	boolean valid = ObjectUtils.available(subConfig);
	if (valid) {
	    valid = subConfig.containsKey(subKey);
	}

	return valid;
    }

    private <K> boolean containsConfigKey(K key) {

	return containsSubConfigKey(Config.DEPLOY_CONFIG.key, key);
    }

    private <K, V> V getSubConfigValue(Object key, K subKey) {

	return getSubConfigValue(key, subKey, null);
    }

    private <K, V> void setConfigValue(K subKey, V value) {

	setSubConfigValue(Config.DEPLOY_CONFIG.key, subKey, value);
    }

    private <K, V> V getConfigValue(K subKey, V defaultValue) {

	return getSubConfigValue(Config.DEPLOY_CONFIG.key, subKey, defaultValue);
    }

    private <K, V> V getConfigValue(K subKey) {

	return getSubConfigValue(Config.DEPLOY_CONFIG.key, subKey);
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

	V value = ObjectUtils.getSubValue(config, Config.DEPLOY_CONFIG.key,
		Config.PERSISTENCE_CONFIG.key, key);
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

	setWithInitialization(Config.PERSISTENCE_CONFIG.key, key, value);
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

	V value = ObjectUtils.getSubValue(config, Config.DEPLOY_CONFIG.key,
		Config.POOL_CONFIG.key, key);
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
     * sub {@link Map} of configuraion
     * 
     * @param key
     * @param value
     */
    public void setPoolConfigValue(Object key, Object value) {

	setWithInitialization(Config.POOL_CONFIG.key, key, value);
    }

    /**
     * Configuration for {@link PoolConfig} instance
     */
    private void configurePool() {

	Map<Object, Object> poolProperties = getPoolConfigValue(Config.POOL_PROPERTIES.key);
	if (ObjectUtils.available(poolProperties)) {

	    setPoolProperties(poolProperties);
	}

	String type = getPoolConfigValue(Config.POOL_PROVIDER_TYPE.key);
	if (ObjectUtils.available(type)) {
	    getPoolConfig().setPoolProviderType(type);
	}

	String path = getPoolConfigValue(Config.POOL_PROPERTIES_PATH.key);
	if (ObjectUtils.available(path)) {
	    setPoolPropertiesPath(path);
	}
    }

    /**
     * Configures server from properties
     */
    private void configureServer() {

	// Sets default values to remote server configuration
	boolean contains = containsConfigKey(Config.IP_ADDRESS.key);
	if (ObjectUtils.notTrue(contains)) {
	    setConfigValue(Config.IP_ADDRESS.key, Config.IP_ADDRESS.value);
	}

	contains = containsConfigKey(Config.PORT.key);
	if (ObjectUtils.notTrue(contains)) {
	    setConfigValue(Config.PORT.key, Config.PORT.value);
	}

	contains = containsConfigKey(Config.BOSS_POOL.key);
	if (ObjectUtils.notTrue(contains)) {
	    setConfigValue(Config.BOSS_POOL.key, Config.BOSS_POOL.value);
	}

	contains = containsConfigKey(Config.WORKER_POOL.key);
	if (ObjectUtils.notTrue(contains)) {

	    int workers = RUNTIME.availableProcessors()
		    * (Integer) Config.WORKER_POOL.value;
	    String workerProperty = String.valueOf(workers);
	    setConfigValue(Config.WORKER_POOL.key, workerProperty);
	}

	contains = containsConfigKey(Config.CONNECTION_TIMEOUT.key);
	if (ObjectUtils.notTrue(contains)) {
	    setConfigValue(Config.CONNECTION_TIMEOUT.key,
		    Config.CONNECTION_TIMEOUT.value);
	}

	// Sets default values is application on server or client mode
	Object serverValue = getConfigValue(Config.SERVER.key);
	if (ObjectUtils.notNull(serverValue)) {
	    if (serverValue instanceof Boolean) {
		server = (Boolean) serverValue;
	    } else {
		server = Boolean.valueOf(serverValue.toString());
	    }
	}

	Object remoteValue = getConfigValue(Config.REMOTE.key);
	if (ObjectUtils.notNull(remoteValue)) {
	    if (remoteValue instanceof Boolean) {
		remote = (Boolean) remoteValue;
	    } else {
		remote = Boolean.valueOf(remoteValue.toString());
	    }
	}
    }

    /**
     * Merges configuration with default properties
     */
    @SuppressWarnings("unchecked")
    public void configureDeployments() {

	// Checks if application run in hot deployment mode
	Boolean hotDeployment = getConfigValue(Config.HOT_DEPLOYMENT.key);
	if (hotDeployment == null) {
	    setConfigValue(Config.HOT_DEPLOYMENT.key, Boolean.FALSE);
	    hotDeployment = getConfigValue(Config.HOT_DEPLOYMENT.key);
	}

	// Check if application needs directory watch service
	boolean watchStatus;
	if (ObjectUtils.notTrue(hotDeployment)) {
	    watchStatus = Boolean.TRUE;
	} else {
	    watchStatus = Boolean.FALSE;
	}

	setConfigValue(Config.WATCH_STATUS.key, watchStatus);

	// Sets deployments directories
	Set<DeploymentDirectory> deploymentPaths = getConfigValue(Config.DEMPLOYMENT_PATH.key);
	if (deploymentPaths == null) {
	    deploymentPaths = (Set<DeploymentDirectory>) Config.DEMPLOYMENT_PATH.value;
	    setConfigValue(Config.DEMPLOYMENT_PATH.key, deploymentPaths);
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
    @SuppressWarnings("unchecked")
    protected Map<Object, Object> deepMerge(Map<Object, Object> map1,
	    Map<Object, Object> map2) {

	if (map1 == null) {
	    map1 = map2;
	} else {
	    Set<Map.Entry<Object, Object>> entries2 = map2.entrySet();
	    Object key;
	    Map<Object, Object> value1;
	    Object value2;
	    Object mergedValue;
	    for (Map.Entry<Object, Object> entry2 : entries2) {
		key = entry2.getKey();
		value2 = entry2.getValue();
		if (value2 instanceof Map) {
		    value1 = ObjectUtils.getAsMap(key, map1);
		    mergedValue = deepMerge(value1,
			    (Map<Object, Object>) value2);
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
		    @SuppressWarnings("unchecked")
		    Map<Object, Object> innerConfig = (Map<Object, Object>) configuration;
		    configure(innerConfig);
		}
	    } finally {
		ObjectUtils.close(stream);
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

	Object value = config.get(key);
	String textValue;
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
	    ObjectUtils.close(propertiesStream);
	}
    }

    /**
     * Loads configuration form file
     * 
     * @throws IOException
     */
    public void loadFromFile() throws IOException {

	InputStream propertiesStream = null;
	try {
	    File configFile = new File(CONFIG_FILE);
	    if (configFile.exists()) {
		propertiesStream = new FileInputStream(configFile);
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

	InputStream propertiesStream = null;
	try {
	    propertiesStream = new FileInputStream(new File(configFilename));
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
	return ADMIN_USERS_PATH;
    }

    public static void setAdminUsersPath(String aDMIN_USERS_PATH) {
	ADMIN_USERS_PATH = aDMIN_USERS_PATH;
    }

    public boolean isRemote() {

	return remote;
    }

    public void setRemote(boolean remoteValue) {
	remote = remoteValue;
    }

    public static boolean isServer() {

	return server;
    }

    public static void setServer(boolean serverValue) {

	server = serverValue;
    }

    public boolean isClient() {

	return getConfigValue(Config.CLIENT.key, Boolean.FALSE);
    }

    public void setClient(boolean client) {
	setConfigValue(Config.CLIENT.key, client);
    }

    /**
     * Adds path for deployments file or directory
     * 
     * @param path
     * @param scan
     */
    public void addDeploymentPath(String path, boolean scan) {

	Set<DeploymentDirectory> deploymentPaths = getConfigValue(Config.DEMPLOYMENT_PATH.key);
	if (deploymentPaths == null) {
	    deploymentPaths = new HashSet<DeploymentDirectory>();
	    setConfigValue(Config.DEMPLOYMENT_PATH.key, deploymentPaths);
	}

	deploymentPaths.add(new DeploymentDirectory(path, scan));
    }

    /**
     * Adds path for data source file
     * 
     * @param path
     */
    public void addDataSourcePath(String path) {

	Set<String> dataSourcePaths = getConfigValue(Config.DATA_SOURCE_PATH.key);
	if (dataSourcePaths == null) {
	    dataSourcePaths = new HashSet<String>();
	    setConfigValue(Config.DATA_SOURCE_PATH.key, dataSourcePaths);
	}

	dataSourcePaths.add(path);
    }

    public Set<DeploymentDirectory> getDeploymentPath() {

	return getConfigValue(Config.DEMPLOYMENT_PATH.key);
    }

    public Set<String> getDataSourcePath() {

	return getConfigValue(Config.DATA_SOURCE_PATH.key);
    }

    public String[] getLibraryPaths() {
	return getConfigValue(Config.LIBRARY_PATH.key);
    }

    public void setLibraryPaths(String[] libraryPaths) {
	setConfigValue(Config.LIBRARY_PATH.key, libraryPaths);
    }

    public boolean isHotDeployment() {

	return getConfigValue(Config.HOT_DEPLOYMENT.key, Boolean.FALSE);
    }

    public void setHotDeployment(boolean hotDeployment) {
	setConfigValue(Config.HOT_DEPLOYMENT.key, hotDeployment);
    }

    public boolean isWatchStatus() {

	return getConfigValue(Config.WATCH_STATUS.key, Boolean.FALSE);
    }

    public void setWatchStatus(boolean watchStatus) {
	setConfigValue(Config.WATCH_STATUS.key, watchStatus);
    }

    /**
     * Property for persistence configuration
     * 
     * @return <code>boolean</code>
     */
    public boolean isScanForEntities() {

	return getPersistenceConfigValue(Config.SCAN_FOR_ENTITIES.key,
		Boolean.FALSE);
    }

    public void setScanForEntities(boolean scanForEntities) {

	setPersistenceConfigValue(Config.SCAN_FOR_ENTITIES.key, scanForEntities);
    }

    public String getAnnotatedUnitName() {

	return getPersistenceConfigValue(Config.ANNOTATED_UNIT_NAME.key);
    }

    public void setAnnotatedUnitName(String annotatedUnitName) {
	setPersistenceConfigValue(Config.ANNOTATED_UNIT_NAME.key,
		annotatedUnitName);
    }

    public String getPersXmlPath() {

	return getPersistenceConfigValue(Config.PERSISTENCE_XML_PATH.key);
    }

    public void setPersXmlPath(String persXmlPath) {
	setPersistenceConfigValue(Config.PERSISTENCE_XML_PATH.key, persXmlPath);
    }

    public boolean isPersXmlFromJar() {

	return getPersistenceConfigValue(Config.PERSISTENCE_XML_FROM_JAR.key,
		Boolean.FALSE);
    }

    public void setPersXmlFromJar(boolean persXmlFromJar) {
	setPersistenceConfigValue(PERSISTENCE_XML_FROM_JAR_KEY, persXmlFromJar);
    }

    public boolean isSwapDataSource() {
	return getPersistenceConfigValue(SWAP_DATASOURCE_KEY, Boolean.FALSE);
    }

    public void setSwapDataSource(boolean swapDataSource) {
	setPersistenceConfigValue(SWAP_DATASOURCE_KEY, swapDataSource);
    }

    public boolean isScanArchives() {
	return getPersistenceConfigValue(SCAN_ARCHIVES_KEY, Boolean.FALSE);
    }

    public void setScanArchives(boolean scanArchives) {
	setPersistenceConfigValue(SCAN_ARCHIVES_KEY, scanArchives);
    }

    public boolean isPooledDataSource() {
	return getPersistenceConfigValue(POOLED_DATA_SOURCE_KEY, Boolean.FALSE);
    }

    public void setPooledDataSource(boolean pooledDataSource) {
	setPersistenceConfigValue(POOLED_DATA_SOURCE_KEY, pooledDataSource);
    }

    public Map<Object, Object> getPersistenceProperties() {
	return getPersistenceConfigValue(PERSISTENCE_PROPERTIES_KEY);
    }

    public void setPersistenceProperties(
	    Map<Object, Object> persistenceProperties) {
	setPersistenceConfigValue(PERSISTENCE_PROPERTIES_KEY,
		persistenceProperties);
    }

    /**
     * Property for connection pool configuration
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

	Configuration cloneConfig = (Configuration) super.clone();
	cloneConfig.config.clear();
	cloneConfig.configure(this.config);

	return cloneConfig;
    }
}
