package org.lightmare.ejb.embeddable;

import java.util.HashMap;
import java.util.Map;

import org.lightmare.config.ConfigKeys;
import org.lightmare.jpa.datasource.PoolConfig;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

public enum EJBPropertiesTest {

    INSTANCE;

    private final Map<?, ?> config;

    private EJBPropertiesTest() {
	this.config = createProperties();
    }

    public Map<Object, Object> createHibernateProperties() {

	Map<Object, Object> properties = new HashMap<Object, Object>();

	properties.put("hibernate.default_schema", "PERSONS");
	properties.put("hibernate.show_sql", "true");
	properties.put("hibernate.sql_trace", "true");

	// properties.put("hibernate.connection.url",
	// "jdbc:derby:target/database/jpa-test-database;create=true");

	// properties.put("hibernate.connection.username", "user");
	// properties.put("hibernate.connection.password", "password");

	properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
	properties.put("hibernate.cache.provider_class",
		"org.hibernate.cache.NoCacheProvider");
	properties.put("hibernate.connection.driver_class", "org.h2.Driver");
	properties.put("hibernate.hbm2ddl.auto", "create-drop");
	// <!-- hiberanate key generation properties -->
	properties.put("hibernate.jdbc.use_get_generated_keys", "true");
	properties.put("hibernate.max_fetch_depth", "3");

	return properties;
    }

    public Map<Object, Object> createJPAProperties() {

	Map<Object, Object> properties = new HashMap<Object, Object>();

	properties.put(ConfigKeys.SWAP_DATASOURCE.key, Boolean.TRUE.toString());
	properties.put(ConfigKeys.PERSISTENCE_PROPERTIES.key,
		createHibernateProperties());

	return properties;
    }

    public Map<Object, Object> createPoolProperties() {

	Map<Object, Object> properties = new HashMap<Object, Object>();

	properties
		.put(PoolConfig.Defaults.STAT_CACHE_NUM_DEFF_THREADS.key, "3");
	properties.put(PoolConfig.Defaults.CHECK_OUT_TIMEOUT.key, "1000");

	return properties;
    }

    public Map<Object, Object> createPoolConfig() {

	Map<Object, Object> properties = new HashMap<Object, Object>();

	properties.put(ConfigKeys.POOL_PROPERTIES_PATH.key,
		"./pool/pool.properties");
	properties.put(ConfigKeys.POOL_PROPERTIES.key, createPoolProperties());

	return properties;
    }

    public Map<Object, Object> createDeployeProperties() {

	Map<Object, Object> properties = new HashMap<Object, Object>();

	properties.put(ConfigKeys.HOT_DEPLOYMENT.key, Boolean.FALSE.toString());
	properties.put(ConfigKeys.DEMPLOYMENT_PATH.key, "./lib");
	properties.put(ConfigKeys.DATA_SOURCE_PATH.key, "./ds/standalone.xml");

	return properties;
    }

    public Map<Object, Object> createProperties() {

	Map<Object, Object> properties = new HashMap<Object, Object>();

	properties.put(ConfigKeys.DEPLOY_CONFIG.key, createDeployeProperties());
	properties
		.put(ConfigKeys.PERSISTENCE_CONFIG.key, createJPAProperties());
	properties.put(ConfigKeys.POOL_CONFIG.key, createPoolConfig());

	return properties;
    }

    public Map<?, ?> getProperties(String unitName, String path) {

	Map<Object, Object> properties;

	if (StringUtils.valid(unitName)) {
	    properties = ObjectUtils.cast(config
		    .get(ConfigKeys.PERSISTENCE_CONFIG.key));
	    properties.put(ConfigKeys.SCAN_FOR_ENTITIES.key,
		    Boolean.TRUE.toString());
	    properties.put(ConfigKeys.ANNOTATED_UNIT_NAME.key, unitName);
	}

	if (StringUtils.valid(path)) {
	    properties = ObjectUtils.cast(config
		    .get(ConfigKeys.PERSISTENCE_CONFIG.key));
	    properties.put(ConfigKeys.PERSISTENCE_XML_FROM_JAR.key,
		    Boolean.TRUE.toString());
	}

	return config;
    }
}
