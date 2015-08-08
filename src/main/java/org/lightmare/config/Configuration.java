/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2014, Levan Tsinadze, or third-party contributors as
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
package org.lightmare.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lightmare.cache.DeploymentDirectory;
import org.lightmare.jpa.datasource.PoolConfig;
import org.lightmare.jpa.datasource.PoolConfig.PoolProviderType;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.config.ConfigUtils;
import org.lightmare.utils.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

/**
 * Retrieves and caches configuration properties from configuration file or from
 * {@link org.lightmare.deploy.MetaCreator.Builder} instance
 *
 * @author Levan Tsinadze
 * @since 0.0.21
 */
public class Configuration extends AbstractConfiguration implements Cloneable {

    // Instance of pool configuration
    private static final PoolConfig POOL_CONFIG = new PoolConfig();

    // Runtime to get available processors
    private static final Runtime RUNTIME = Runtime.getRuntime();

    // Resource path (META-INF)
    private static final String META_INF_PATH = "META-INF/";

    // Error messages
    private static final String RESOURCE_NOT_EXISTS_ERROR = "Configuration resource doesn't exist";

    private static final Logger LOG = Logger.getLogger(Configuration.class);

    public Configuration() {
    }

    /**
     * Loads configuration from file contained in classpath
     *
     * @param resourceName
     * @param loader
     */
    public void loadFromResource(String resourceName, ClassLoader loader)
	    throws IOException {

	InputStream resourceStream = loader.getResourceAsStream(
		StringUtils.concat(META_INF_PATH, resourceName));
	if (resourceStream == null) {
	    LOG.error(RESOURCE_NOT_EXISTS_ERROR);
	} else {
	    loadFromStream(resourceStream);
	}
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
		ConfigKeys.DEPLOY_CONFIG.key, ConfigKeys.PERSISTENCE_CONFIG.key,
		key);

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

	Map<Object, Object> poolProperties = getPoolConfigValue(
		ConfigKeys.POOL_PROPERTIES.key);
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
	setIfContains(ConfigKeys.IP_ADDRESS.key, ConfigKeys.IP_ADDRESS.value);
	setIfContains(ConfigKeys.PORT.key, ConfigKeys.PORT.value);
	setIfContains(ConfigKeys.BOSS_POOL.key, ConfigKeys.BOSS_POOL.value);

	boolean contains = containsConfigKey(ConfigKeys.WORKER_POOL.key);
	if (Boolean.FALSE.equals(contains)) {
	    int defaultWorkers = ConfigKeys.WORKER_POOL.getValue();
	    int workers = (RUNTIME.availableProcessors() * defaultWorkers);
	    String workerProperty = String.valueOf(workers);
	    setConfigValue(ConfigKeys.WORKER_POOL.key, workerProperty);
	}

	setIfContains(ConfigKeys.CONNECTION_TIMEOUT.key,
		ConfigKeys.CONNECTION_TIMEOUT.value);
    }

    /**
     * Sets hot deployment and directory watch configuration
     */
    private void mergeHotDeployment() {

	Boolean hotDeployment = getConfigValue(ConfigKeys.HOT_DEPLOYMENT.key);
	if (hotDeployment == null) {
	    setConfigValue(ConfigKeys.HOT_DEPLOYMENT.key, Boolean.FALSE);
	    hotDeployment = getConfigValue(ConfigKeys.HOT_DEPLOYMENT.key);
	}

	// Check if application needs directory watch service
	boolean watchStatus;
	if (Boolean.FALSE.equals(hotDeployment)) {
	    watchStatus = Boolean.TRUE;
	} else {
	    watchStatus = Boolean.FALSE;
	}
	setConfigValue(ConfigKeys.WATCH_STATUS.key, watchStatus);
    }

    /**
     * Sets deployment directories
     */
    private void mergeDeployPath() {

	Set<DeploymentDirectory> deploymentPaths = getConfigValue(
		ConfigKeys.DEMPLOYMENT_PATH.key);
	if (deploymentPaths == null) {
	    deploymentPaths = ConfigKeys.DEMPLOYMENT_PATH.getValue();
	    setConfigValue(ConfigKeys.DEMPLOYMENT_PATH.key, deploymentPaths);
	}
    }

    /**
     * Sets remote control configuration
     */
    private void mergeRemoteControl() {

	Boolean remoteControl = getConfigValue(ConfigKeys.REMOTE_CONTROL.key);
	if (ObjectUtils.notNull(remoteControl)) {
	    setRemoteControl(remoteControl);
	}
    }

    /**
     * Merges configuration with default properties
     */
    public void configureDeployments() {

	// Checks if application run in hot deployment mode
	mergeHotDeployment();
	// Sets deployments directories
	mergeDeployPath();
	// Sets remote control check
	mergeRemoteControl();
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

    public static String getAdminUsersPath() {
	return ConfigKeys.ADMIN_USERS_PATH.getValue();
    }

    public static void setAdminUsersPath(String adminUsersPath) {
	ConfigKeys.ADMIN_USERS_PATH.value = adminUsersPath;
    }

    public static void setRemoteControl(boolean remoteControl) {
	ConfigKeys.REMOTE_CONTROL.value = remoteControl;
    }

    public static boolean getRemoteControl() {

	boolean answer;

	Object value = ConfigKeys.REMOTE_CONTROL.getValue();
	answer = ConfigUtils.getBoolean(value);

	return answer;
    }

    public boolean isRemote() {
	return ConfigKeys.REMOTE.getValue();
    }

    public void setRemote(boolean remote) {
	ConfigKeys.REMOTE.value = remote;
    }

    public static boolean isServer() {

	boolean answer;

	Object value = ConfigKeys.SERVER.getValue();
	answer = ConfigUtils.getBoolean(value);

	return answer;
    }

    public static void setServer(boolean server) {
	ConfigKeys.SERVER.value = server;
    }

    public boolean isClient() {

	boolean answer;

	Object value = getConfigValue(ConfigKeys.CLIENT.key, Boolean.FALSE);
	answer = ConfigUtils.getBoolean(value);

	return answer;
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

	Set<DeploymentDirectory> deploymentPaths = getConfigValue(
		ConfigKeys.DEMPLOYMENT_PATH.key);
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

	Set<String> dataSourcePaths = getConfigValue(
		ConfigKeys.DATA_SOURCE_PATH.key);
	if (dataSourcePaths == null) {
	    dataSourcePaths = new HashSet<String>();
	    setConfigValue(ConfigKeys.DATA_SOURCE_PATH.key, dataSourcePaths);
	}

	dataSourcePaths.add(path);
    }

    /**
     * Sets data sources properties list
     *
     * @param datasources
     */
    public void setDataSources(List<Map<Object, Object>> datasources) {
	setConfigValue(ConfigKeys.DATASOURCES.key, datasources);
    }

    /**
     * Adds data source to existed data sources
     *
     * @param datasource
     */
    public void addDataSource(Map<Object, Object> datasource) {
	List<Map<Object, Object>> datasources = getDataSources();
	datasources.add(datasource);
    }

    /**
     * Sets single data source
     *
     * @param datasources
     */
    public void setDataSource(Map<Object, Object> datasources) {
	setConfigValue(ConfigKeys.DATASOURCE.key, datasources);
    }

    /**
     * Initializes modules by deployment modules parameter from configuration
     *
     * @return {@link List} of deployment file paths
     */
    public List<String[]> getDeploymentModules() {

	List<String[]> modules;

	// Extracts modules from configuration
	Object value = getConfigValue(ConfigKeys.MODULES.key);
	modules = ConfigUtils.getModules(value);

	return modules;
    }

    /**
     * Initializes deployment path parameters
     *
     * @return {@link Set} of {@link DeploymentDirectory} instances
     */
    public Set<DeploymentDirectory> getDeploymentPath() {

	Set<DeploymentDirectory> deployments;

	Object value = getConfigValue(ConfigKeys.DEMPLOYMENT_PATH.key);
	deployments = ConfigUtils.getDeployments(value);

	return deployments;
    }

    public Set<String> getDataSourcePath() {

	Set<String> paths;

	Object value = getConfigValue(ConfigKeys.DATA_SOURCE_PATH.key);
	paths = ConfigUtils.getSet(value);

	return paths;
    }

    /**
     * Gets datasources from configuration
     *
     * @return
     */
    public List<Map<Object, Object>> getDataSources() {

	List<Map<Object, Object>> datasources;

	Object raw = getConfigValue(ConfigKeys.DATASOURCES.key);
	if (raw == null) {
	    datasources = new ArrayList<Map<Object, Object>>();
	    setDataSources(datasources);
	} else if (raw instanceof Map<?, ?>) {
	    Map<Object, Object> config = ObjectUtils.cast(raw);
	    Collection<?> raws = config.values();
	    Collection<Map<Object, Object>> values = ObjectUtils.cast(raws);
	    datasources = new ArrayList<Map<Object, Object>>(values);
	} else if (raw instanceof List<?>) {
	    datasources = ObjectUtils.cast(raw);
	} else {
	    datasources = new ArrayList<Map<Object, Object>>();
	}

	return datasources;
    }

    public Map<Object, Object> getDataSource() {
	return getConfigValue(ConfigKeys.DATASOURCE.key);
    }

    public String[] getLibraryPaths() {

	String[] paths;

	Object value = getConfigValue(ConfigKeys.LIBRARY_PATH.key);
	paths = ConfigUtils.getModule(value);

	return paths;
    }

    public void setLibraryPaths(String[] libraryPaths) {
	setConfigValue(ConfigKeys.LIBRARY_PATH.key, libraryPaths);
    }

    public boolean isHotDeployment() {

	boolean answer;

	Object value = getConfigValue(ConfigKeys.HOT_DEPLOYMENT.key,
		Boolean.FALSE);
	answer = ConfigUtils.getBoolean(value);

	return answer;
    }

    public void setHotDeployment(boolean hotDeployment) {
	setConfigValue(ConfigKeys.HOT_DEPLOYMENT.key, hotDeployment);
    }

    public boolean isWatchStatus() {

	boolean answer;

	Object value = getConfigValue(ConfigKeys.WATCH_STATUS.key,
		Boolean.FALSE);
	answer = ConfigUtils.getBoolean(value);

	return answer;
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

	boolean answer;

	Object value = getPersistenceConfigValue(
		ConfigKeys.SCAN_FOR_ENTITIES.key, Boolean.FALSE);
	answer = ConfigUtils.getBoolean(value);

	return answer;
    }

    public void setSpringPersistence(boolean springPersistence) {
	setPersistenceConfigValue(ConfigKeys.SPRING_PERSISTENCE.key,
		springPersistence);
    }

    public boolean isSpringPersistence() {
	return getPersistenceConfigValue(ConfigKeys.SPRING_PERSISTENCE.key,
		Boolean.FALSE);
    }

    /**
     * Gets data source name for appropriated JPA persistence unit from
     * configuration
     *
     * @param unitName
     * @return String data source name
     */
    public String getDataSourceName(String unitName) {

	String dataSourceName;

	Map<Object, Object> datasourceNames = getPersistenceConfigValue(
		ConfigKeys.UNIT_DATASOURCES.key, Collections.emptyMap());
	Object value = datasourceNames.get(unitName);
	dataSourceName = ObjectUtils.cast(value, String.class);

	return dataSourceName;
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

	boolean answer;

	Object value = getPersistenceConfigValue(
		ConfigKeys.PERSISTENCE_XML_FROM_JAR.key, Boolean.FALSE);
	answer = ConfigUtils.getBoolean(value);

	return answer;
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

	boolean answer;

	Object value = getPersistenceConfigValue(ConfigKeys.SCAN_ARCHIVES.key,
		Boolean.FALSE);
	answer = ConfigUtils.getBoolean(value);

	return answer;
    }

    public void setScanArchives(boolean scanArchives) {
	setPersistenceConfigValue(ConfigKeys.SCAN_ARCHIVES.key, scanArchives);
    }

    public boolean isPooledDataSource() {

	boolean answer;

	Object value = getPersistenceConfigValue(
		ConfigKeys.POOLED_DATA_SOURCE.key, Boolean.FALSE);
	answer = ConfigUtils.getBoolean(value);

	return answer;
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

    public static void setDataSourcePooledType(boolean dsPooledType) {
	PoolConfig poolConfig = getPoolConfig();
	poolConfig.setPooledDataSource(dsPooledType);
    }

    public static void setPoolPropertiesPath(String path) {
	PoolConfig poolConfig = getPoolConfig();
	poolConfig.setPoolPath(path);
    }

    public static void setPoolProperties(
	    Map<? extends Object, ? extends Object> properties) {
	PoolConfig poolConfig = getPoolConfig();
	poolConfig.getPoolProperties().putAll(properties);
    }

    public static void addPoolProperty(Object key, Object value) {
	PoolConfig poolConfig = getPoolConfig();
	poolConfig.getPoolProperties().put(key, value);
    }

    public static void setPoolProviderType(PoolProviderType poolProviderType) {
	PoolConfig poolConfig = getPoolConfig();
	poolConfig.setPoolProviderType(poolProviderType);
    }
}
