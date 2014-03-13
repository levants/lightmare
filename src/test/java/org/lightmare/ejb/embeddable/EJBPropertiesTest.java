package org.lightmare.ejb.embeddable;

import java.util.HashMap;
import java.util.Map;

public enum EJBPropertiesTest {

    INSTANCE;

    public final Map<?, ?> config;

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

	properties.put("swapDataSource", Boolean.TRUE.toString());
	properties.put("persistenceProperties", createHibernateProperties());

	return properties;
    }

    public Map<Object, Object> createPoolProperties() {

	Map<Object, Object> properties = new HashMap<Object, Object>();

	properties.put("statementCacheNumDeferredCloseThreads", "3");
	properties.put("checkoutTimeout", "1000");

	return properties;
    }

    public Map<Object, Object> createPoolConfig() {

	Map<Object, Object> properties = new HashMap<Object, Object>();

	properties.put("poolPropertiesPath", "./pool/pool.properties");
	properties.put("poolProperties", createPoolProperties());

	return properties;
    }

    public Map<Object, Object> createProperties() {

	Map<Object, Object> properties = new HashMap<Object, Object>();

	properties.put("persistenceConfig", createJPAProperties());
	properties.put("poolConfig", createPoolConfig());

	return properties;
    }

    private EJBPropertiesTest() {
	this.config = createProperties();
    }
}
