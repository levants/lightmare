package org.lightmare.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lightmare.cache.DeploymentDirectory;
import org.lightmare.jpa.datasource.PoolConfig;
import org.lightmare.utils.ObjectUtils;

/**
 * Easy way to retrieve configuration properties from configuration file
 * 
 * @author levan
 * 
 */
public class Configuration implements Cloneable {

    // cache for all configuration passed programmatically or read from file
    private final Map<String, Object> config = new HashMap<String, Object>();

    // path where stored adminitrator users
    public static final String ADMIN_USERS_PATH_KEY = "adminUsersPath";

    /**
     * <a href="netty.io">Netty</a> server / client configuration properties for
     * RPC calls
     */
    public static final String IP_ADDRESS_KEY = "listeningIp";

    public static final String PORT_KEY = "listeningPort";

    public static final String BOSS_POOL_KEY = "bossPoolSize";

    public static final String WORKER_POOL_KEY = "workerPoolSize";

    public static final String CONNECTION_TIMEOUT_KEY = "timeout";

    // properties for datasource path and deployment path
    public static final String DEMPLOYMENT_PATH_KEY = "deploymentPath";

    public static final String DATA_SOURCE_PATH_KEY = "dataSourcePath";

    // runtime to get avaliable processors
    private static final Runtime RUNTIME = Runtime.getRuntime();

    /**
     * Default properties
     */
    public static final String ADMIN_USERS_PATH_DEF = "./config/admin/users.properties";

    public static final String IP_ADDRESS_DEF = "0.0.0.0";

    public static final String PORT_DEF = "1199";

    public static final String BOSS_POOL_DEF = "1";

    public static final int WORKER_POOL_DEF = 3;

    public static final String CONNECTION_TIMEOUT_DEF = "1000";

    public static final boolean SERVER_DEF = Boolean.TRUE;

    public static final String DATA_SOURCE_PATH_DEF = "./ds";

    public static final Set<DeploymentDirectory> DEPLOYMENT_PATHS_DEF = new HashSet<DeploymentDirectory>(
	    Arrays.asList(new DeploymentDirectory("./deploy", Boolean.TRUE)));

    public static final Set<String> DATA_SOURCES_PATHS_DEF = new HashSet<String>(
	    Arrays.asList("./ds"));

    /**
     * Properties which version of server is running remote it requires server
     * client RPC infrastructure or local (embeddable mode)
     */
    private static final String REMOTE_KEY = "remote";

    private static final String SERVER_KEY = "server";

    private static final String CLIENT_KEY = "client";

    private static final String CONFIG_FILE = "./config/config.properties";

    // String prefixes for jndi names
    public static final String JPA_NAME = "java:comp/env/";

    public static final String EJB_NAME = "ejb:";

    public static final int EJB_NAME_LENGTH = 4;

    // Configuration keys properties for deployment
    private static final String DEPLOY_CONFIG_KEY = "deployConfiguration";

    private static final String SCAN_FOR_ENTITIES_KEY = "scanForEntities";

    private static final String ANNOTATED_UNIT_NAME_KEY = "annotatedUnitName";

    private static final String PERSISTENCE_XML_PATH_KEY = "persistanceXmlPath";

    private static final String LIBRARY_PATH_KEY = "libraryPaths";

    private static final String PERSISTENCE_XML_FROM_JAR_KEY = "persistenceXmlFromJar";

    private static final String SWAP_DATASOURCE_KEY = "swapDataSource";

    private static final String SCAN_ARCHIVES_KEY = "scanArchives";

    private static final String POOLED_DATA_SOURCE_KEY = "pooledDataSource";

    private static final String PERSISTENCE_PROPERTIES_KEY = "persistenceProperties";

    private static final String POOL_CONFIG_KEY = "poolConfig";

    // Configuration properties for deployment
    private boolean hotDeployment;

    private boolean watchStatus;

    private static String ADMIN_USERS_PATH;

    private static boolean server = SERVER_DEF;

    private static boolean remote;

    private static final Logger LOG = Logger.getLogger(Configuration.class);

    public Configuration() {
    }

    public void setDefaults() {

	boolean contains = config.containsKey(IP_ADDRESS_KEY);
	if (ObjectUtils.notTrue(contains)) {
	    config.put(IP_ADDRESS_KEY, IP_ADDRESS_DEF);
	}

	contains = config.containsKey(PORT_KEY);
	if (ObjectUtils.notTrue(contains)) {
	    config.put(PORT_KEY, PORT_DEF);
	}

	contains = config.containsKey(BOSS_POOL_KEY);
	if (ObjectUtils.notTrue(contains)) {
	    config.put(BOSS_POOL_KEY, BOSS_POOL_DEF);
	}

	contains = config.containsKey(WORKER_POOL_KEY);
	if (ObjectUtils.notTrue(contains)) {

	    int workers = RUNTIME.availableProcessors() * WORKER_POOL_DEF;
	    String workerProperty = String.valueOf(workers);
	    config.put(WORKER_POOL_KEY, workerProperty);
	}

	contains = config.containsKey(CONNECTION_TIMEOUT_KEY);
	if (ObjectUtils.notTrue(contains)) {
	    config.put(CONNECTION_TIMEOUT_KEY, CONNECTION_TIMEOUT_DEF);
	}

	if (ObjectUtils.notTrue(hotDeployment)) {
	    watchStatus = Boolean.TRUE;
	} else {
	    watchStatus = Boolean.FALSE;
	}

	Set<DeploymentDirectory> deploymentPaths = getSubConfigValue(
		DEPLOY_CONFIG_KEY, DEMPLOYMENT_PATH_KEY);
	if (deploymentPaths == null) {
	    deploymentPaths = DEPLOYMENT_PATHS_DEF;
	    setSubConfigValue(DEPLOY_CONFIG_KEY, DEMPLOYMENT_PATH_KEY,
		    deploymentPaths);
	}
    }

    public void configure() {
	setDefaults();
    }

    public void configure(Map<String, Object> configuration) {

	configure();
	if (ObjectUtils.available(configuration)) {
	    config.putAll(configuration);
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
	    LOG.error("Could not open config file", ex);
	} finally {
	    if (ObjectUtils.notNull(propertiesStream)) {
		propertiesStream.close();
	    }
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
	    LOG.error("Could not open config file", ex);
	} finally {
	    if (ObjectUtils.notNull(propertiesStream)) {
		propertiesStream.close();
	    }
	}

    }

    /**
     * Loads configuration from file contained in classpath
     * 
     * @param resourceName
     * @param loader
     */
    public void loadFromResource(String resourceName, ClassLoader loader) {

	InputStream resourceStream = loader
		.getResourceAsStream(new StringBuilder("META-INF/").append(
			resourceName).toString());
	if (resourceStream == null) {
	    LOG.error("Configuration resource doesn't exist");
	    return;
	}
	loadFromStream(resourceStream);
	try {
	    resourceStream.close();
	} catch (IOException ex) {
	    LOG.error("Could not load resource", ex);
	}
    }

    /**
     * Load {@link Configuration} in memory as {@link Map} of parameters
     * 
     * @throws IOException
     */
    public void loadFromStream(InputStream propertiesStream) {

	try {
	    Properties props = new Properties();
	    props.load(propertiesStream);
	    for (String propertyName : props.stringPropertyNames()) {
		config.put(propertyName, props.getProperty(propertyName));
	    }
	    propertiesStream.close();
	} catch (IOException ex) {
	    LOG.error("Could not load configuration", ex);
	}
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

	return getSubConfigValue(DEPLOY_CONFIG_KEY, CLIENT_KEY, Boolean.FALSE);
    }

    public void setClient(boolean client) {
	setSubConfigValue(DEPLOY_CONFIG_KEY, CLIENT_KEY, client);
    }

    /**
     * Adds path for deployments file or directory
     * 
     * @param path
     * @param scan
     */
    public void addDeploymentPath(String path, boolean scan) {

	Set<DeploymentDirectory> deploymentPaths = getSubConfigValue(
		DEPLOY_CONFIG_KEY, DEMPLOYMENT_PATH_KEY);
	if (deploymentPaths == null) {
	    deploymentPaths = new HashSet<DeploymentDirectory>();
	    setSubConfigValue(DEPLOY_CONFIG_KEY, DEMPLOYMENT_PATH_KEY,
		    deploymentPaths);
	}

	deploymentPaths.add(new DeploymentDirectory(path, scan));
    }

    /**
     * Adds path for data source file
     * 
     * @param path
     */
    public void addDataSourcePath(String path) {

	Set<String> dataSourcePaths = getSubConfigValue(DEPLOY_CONFIG_KEY,
		DATA_SOURCE_PATH_KEY);
	if (dataSourcePaths == null) {
	    dataSourcePaths = new HashSet<String>();
	    setSubConfigValue(DEPLOY_CONFIG_KEY, DATA_SOURCE_PATH_KEY,
		    dataSourcePaths);
	}

	dataSourcePaths.add(path);
    }

    public Set<DeploymentDirectory> getDeploymentPath() {

	return getSubConfigValue(DEPLOY_CONFIG_KEY, DEMPLOYMENT_PATH_KEY);
    }

    public Set<String> getDataSourcePath() {

	return getSubConfigValue(DEPLOY_CONFIG_KEY, DATA_SOURCE_PATH_KEY);
    }

    @SuppressWarnings("unchecked")
    private <K, V> Map<K, V> getAsMap(String key) {

	Map<K, V> value = (Map<K, V>) config.get(key);

	return value;
    }

    private <K, V> void setSubConfigValue(String key, K subKey, V value) {

	Map<K, V> subConfig = getAsMap(key);
	if (subConfig == null) {
	    subConfig = new HashMap<K, V>();
	    config.put(key, subConfig);
	}

	subConfig.put(subKey, value);
    }

    private <K, V> V getSubConfigValue(String key, K subKey, V defaultValue) {

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

    private <K, V> V getSubConfigValue(String key, String subKey) {

	return getSubConfigValue(key, subKey, null);
    }

    public boolean isScanForEntities() {
	return getSubConfigValue(DEPLOY_CONFIG_KEY, SCAN_FOR_ENTITIES_KEY,
		Boolean.FALSE);
    }

    public void setScanForEntities(boolean scanForEntities) {

	setSubConfigValue(DEPLOY_CONFIG_KEY, SCAN_FOR_ENTITIES_KEY,
		scanForEntities);
    }

    public String getAnnotatedUnitName() {
	return getSubConfigValue(DEPLOY_CONFIG_KEY, ANNOTATED_UNIT_NAME_KEY);
    }

    public void setAnnotatedUnitName(String annotatedUnitName) {
	setSubConfigValue(DEPLOY_CONFIG_KEY, ANNOTATED_UNIT_NAME_KEY,
		annotatedUnitName);
    }

    public String getPersXmlPath() {
	return getSubConfigValue(DEPLOY_CONFIG_KEY, PERSISTENCE_XML_PATH_KEY);
    }

    public void setPersXmlPath(String persXmlPath) {
	setSubConfigValue(DEPLOY_CONFIG_KEY, PERSISTENCE_XML_PATH_KEY,
		persXmlPath);
    }

    public String[] getLibraryPaths() {
	return getSubConfigValue(DEPLOY_CONFIG_KEY, LIBRARY_PATH_KEY);
    }

    public void setLibraryPaths(String[] libraryPaths) {
	setSubConfigValue(DEPLOY_CONFIG_KEY, LIBRARY_PATH_KEY, libraryPaths);
    }

    public boolean isPersXmlFromJar() {
	return getSubConfigValue(DEPLOY_CONFIG_KEY,
		PERSISTENCE_XML_FROM_JAR_KEY, Boolean.FALSE);
    }

    public void setPersXmlFromJar(boolean persXmlFromJar) {
	setSubConfigValue(DEPLOY_CONFIG_KEY, PERSISTENCE_XML_FROM_JAR_KEY,
		persXmlFromJar);
    }

    public boolean isSwapDataSource() {
	return getSubConfigValue(DEPLOY_CONFIG_KEY, SWAP_DATASOURCE_KEY,
		Boolean.FALSE);
    }

    public void setSwapDataSource(boolean swapDataSource) {
	setSubConfigValue(DEPLOY_CONFIG_KEY, SWAP_DATASOURCE_KEY,
		swapDataSource);
    }

    public boolean isScanArchives() {
	return getSubConfigValue(DEPLOY_CONFIG_KEY, SCAN_ARCHIVES_KEY,
		Boolean.FALSE);
    }

    public void setScanArchives(boolean scanArchives) {
	setSubConfigValue(DEPLOY_CONFIG_KEY, SCAN_ARCHIVES_KEY, scanArchives);
    }

    public boolean isPooledDataSource() {
	return getSubConfigValue(DEPLOY_CONFIG_KEY, POOLED_DATA_SOURCE_KEY,
		Boolean.FALSE);
    }

    public void setPooledDataSource(boolean pooledDataSource) {
	setSubConfigValue(DEPLOY_CONFIG_KEY, POOLED_DATA_SOURCE_KEY,
		pooledDataSource);
    }

    public PoolConfig getPoolConfig() {
	return getSubConfigValue(DEPLOY_CONFIG_KEY, POOL_CONFIG_KEY);
    }

    public void setPoolConfig(PoolConfig poolConfig) {
	setSubConfigValue(DEPLOY_CONFIG_KEY, POOL_CONFIG_KEY, poolConfig);
    }

    public static String getAdminUsersPath() {
	return ADMIN_USERS_PATH;
    }

    public static void setAdminUsersPath(String aDMIN_USERS_PATH) {
	ADMIN_USERS_PATH = aDMIN_USERS_PATH;
    }

    public Map<Object, Object> getPersistenceProperties() {
	return getSubConfigValue(DEPLOY_CONFIG_KEY, PERSISTENCE_PROPERTIES_KEY);
    }

    public void setPersistenceProperties(
	    Map<Object, Object> persistenceProperties) {
	setSubConfigValue(DEPLOY_CONFIG_KEY, PERSISTENCE_PROPERTIES_KEY,
		persistenceProperties);
    }

    public boolean isHotDeployment() {
	return hotDeployment;
    }

    public void setHotDeployment(boolean hotDeployment) {
	this.hotDeployment = hotDeployment;
    }

    public boolean isWatchStatus() {
	return watchStatus;
    }

    public void setWatchStatus(boolean watchStatus) {
	this.watchStatus = watchStatus;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

	return super.clone();
    }
}
