package org.lightmare.config;

import java.util.Arrays;
import java.util.HashSet;

import org.lightmare.cache.DeploymentDirectory;

/**
 * Keeps keys and default values for configuration
 * 
 * @author Levan
 * 
 */
public enum Config {

    // Default properties
    // Path where stored administrative users
    ADMIN_USERS_PATH("adminUsersPath", "./config/admin/users.properties"),

    // Netty server / client configuration properties for RPC calls
    IP_ADDRESS("listeningIp", "0.0.0.0"), // ip

    PORT("listeningPort", "1199"), // port

    BOSS_POOL("bossPoolSize", 1), // boss pool

    WORKER_POOL_KEY("workerPoolSize", 3), // Worker pool

    CONNECTION_TIMEOUT("timeout", 1000), // Connection timeout

    // Properties for data source path and deployment path
    DEMPLOYMENT_PATH("deploymentPath", new HashSet<DeploymentDirectory>(
	    Arrays.asList(new DeploymentDirectory("./deploy", Boolean.TRUE)))),

    DATA_SOURCE_PATH("dataSourcePath", new HashSet<String>(
	    Arrays.asList("./ds"))), // data
				     // source
				     // path

    // Properties which version of server is running remote it requires server
    // client RPC infrastructure or local (embedded mode)
    SERVER("server", Boolean.TRUE),

    REMOTE("remote", Boolean.FALSE),

    CLIENT_KEY("client", Boolean.FALSE),

    // Configuration keys properties for deployment
    DEPLOY_CONFIG("deployConfiguration"), // Deploy config

    ADMIN_USER_PATH("adminPath"), // Admin user path

    HOT_DEPLOYMENT("hotDeployment"), // Hot deployment

    WATCH_STATUS("watchStatus"), // Watch status

    LIBRARY_PATH("libraryPaths"), // Library path

    // Persistence provider property keys
    PERSISTENCE_CONFIG("persistenceConfig"), // Persistence config

    SCAN_FOR_ENTITIES("scanForEntities"), // Scan for entities

    ANNOTATED_UNIT_NAME("annotatedUnitName"), // Annotated unit

    PERSISTENCE_XML_PATH("persistanceXmlPath"), // Persistence XML

    PERSISTENCE_XML_FROM_JAR("persistenceXmlFromJar"), // Persistence XML from
						       // jar

    SWAP_DATASOURCE("swapDataSource"), // Swap data source

    SCAN_ARCHIVES("scanArchives"), // Scan archives

    POOLED_DATA_SOURCE("pooledDataSource"); // Pooled data source

    private static final String PERSISTENCE_PROPERTIES_KEY = "persistenceProperties";

    // Connection pool provider property keys
    private static final String POOL_CONFIG_KEY = "poolConfig";

    private static final String POOL_PROPERTIES_PATH_KEY = "poolPropertiesPath";

    private static final String POOL_PROVIDER_TYPE_KEY = "poolProviderType";

    private static final String POOL_PROPERTIES_KEY = "poolProperties";

    public String key;

    public Object value;

    private Config(String key) {
	this.key = key;
    }

    private Config(String key, Object value) {
	this(key);
	this.value = value;
    }
}
