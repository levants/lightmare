package org.lightmare.config;

import java.util.Arrays;
import java.util.HashSet;

import org.lightmare.cache.DeploymentDirectory;
import org.lightmare.utils.ObjectUtils;

/**
 * Keeps keys and default values for configuration
 * 
 * @author Levan
 * 
 */
public enum ConfigKeys {

    // Default properties
    // Path where stored administrative users
    ADMIN_USERS_PATH("adminUsersPath", "./config/admin/users.properties"),

    REMOTE_CONTROL("remoteControl", Boolean.FALSE),

    // Netty server / client configuration properties for RPC calls
    IP_ADDRESS("listeningIp", "0.0.0.0"), // IP

    PORT("listeningPort", 1199), // Port

    BOSS_POOL("bossPoolSize", 1), // Boss pool

    WORKER_POOL("workerPoolSize", 3), // Worker pool

    CONNECTION_TIMEOUT("timeout", 1000), // Connection timeout

    // Properties for data source path and deployment path
    DEMPLOYMENT_PATH("deploymentPath", new HashSet<DeploymentDirectory>(
	    Arrays.asList(new DeploymentDirectory("./deploy", Boolean.TRUE)))),

    DATA_SOURCE_PATH("dataSourcePath", new HashSet<String>(
	    Arrays.asList("./ds"))), // Data
				     // source
				     // path

    // Properties which version of server is running remote it requires server
    // client RPC infrastructure or local (embedded mode)
    SERVER("server", Boolean.TRUE),

    REMOTE("remote", Boolean.FALSE),

    CLIENT("client", Boolean.FALSE),

    // Configuration keys properties for deployment
    DEPLOY_CONFIG("deployConfiguration"), // Deploy CONFIG

    HOT_DEPLOYMENT("hotDeployment"), // Hot deployment

    WATCH_STATUS("watchStatus"), // Watch status

    LIBRARY_PATH("libraryPaths"), // Library path

    // Persistence provider property keys
    PERSISTENCE_CONFIG("persistenceConfig"), // Persistence CONFIG

    SCAN_FOR_ENTITIES("scanForEntities"), // Scan for entities

    ANNOTATED_UNIT_NAME("annotatedUnitName"), // Annotated unit

    PERSISTENCE_XML_PATH("persistanceXmlPath"), // Persistence XML

    PERSISTENCE_XML_FROM_JAR("persistenceXmlFromJar"), // Persistence XML from
						       // jar

    SWAP_DATASOURCE("swapDataSource"), // Swap data source

    SCAN_ARCHIVES("scanArchives"), // Scan archives

    POOLED_DATA_SOURCE("pooledDataSource", Boolean.TRUE), // Pooled data source

    PERSISTENCE_PROPERTIES("persistenceProperties"), // Persistence properties

    // Connection pool provider property keys
    POOL_CONFIG("poolConfig"), // Pool CONFIG

    POOL_PROPERTIES_PATH("poolPropertiesPath"), // Pool properties path

    POOL_PROVIDER_TYPE("poolProviderType"), // Pool provider type

    POOL_PROPERTIES("poolProperties"), // Pool properties

    // Default configuration file location
    CONFIG_FILE("configFile", "./config/configuration.yaml");

    public String key;

    public Object value;

    private ConfigKeys(String key) {
	this.key = key;
    }

    private ConfigKeys(String key, Object value) {
	this(key);
	this.value = value;
    }

    public String getKey() {

	return key;
    }

    public <T> T getValue() {

	T typedValue = ObjectUtils.cast(value);

	return typedValue;
    }
}
