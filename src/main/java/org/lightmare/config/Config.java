package org.lightmare.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.lightmare.cache.DeploymentDirectory;

/**
 * Keeps keys and default values for configuration
 * 
 * @author Levan
 * 
 */
public enum Config {

    // Path where stored administrative users
    ADMIN_USERS_PATH("adminUsersPath", "./config/admin/users.properties"),

    // Netty server / client configuration properties for RPC calls
    IP_ADDRESS("listeningIp", "0.0.0.0"), // ip

    PORT("listeningPort", "1199"), // port

    BOSS_POOL("bossPoolSize"), // boss pool

    WORKER_POOL_KEY("workerPoolSize"), // Worker pool

    CONNECTION_TIMEOUT("timeout"), // Connection timeout

    // Properties for data source path and deployment path
    DEMPLOYMENT_PATH("deploymentPath"),

    DATA_SOURCE_PATH("dataSourcePath"); // data source path

    // Default properties
    public static final String BOSS_POOL_DEF = "1";

    public static final int WORKER_POOL_DEF = 3;

    public static final String CONNECTION_TIMEOUT_DEF = "1000";

    public static final boolean SERVER_DEF = Boolean.TRUE;

    public static final String DATA_SOURCE_PATH_DEF = "./ds";

    // Properties which version of server is running remote it requires server
    // client RPC infrastructure or local (embedded mode)
    private static final String REMOTE_KEY = "remote";

    private static final String SERVER_KEY = "server";

    private static final String CLIENT_KEY = "client";

    public static final Set<DeploymentDirectory> DEPLOYMENT_PATHS_DEF = new HashSet<DeploymentDirectory>(
	    Arrays.asList(new DeploymentDirectory("./deploy", Boolean.TRUE)));

    public static final Set<String> DATA_SOURCES_PATHS_DEF = new HashSet<String>(
	    Arrays.asList("./ds"));

    private static final String CONFIG_FILE = "./config/configuration.yaml";

    // Configuration keys properties for deployment
    private static final String DEPLOY_CONFIG_KEY = "deployConfiguration";

    private static final String ADMIN_USER_PATH_KEY = "adminPath";

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

    // Is configuration server or client (default is server)
    private static boolean server = SERVER_DEF;

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
