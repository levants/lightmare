package org.lightmare.listeners;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.lightmare.deploy.MetaCreator;

/**
 * Application Lifecycle Listener implementation class LoaderListener
 * 
 */
@WebListener
public class LoaderListener implements ServletContextListener {

    public static final String DEPLOY = "./deploy";

    public static final String DEPLOY_PATH = "./deploy/loader-tester";

    public static final String DS_FILE = "./ds/standalone.xml";

    /**
     * Default constructor.
     */
    public LoaderListener() {
    }

    private Map<String, String> getPersistenceProperties() {

	Map<String, String> properties = new HashMap<String, String>();

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

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event) {

	try {

	    MetaCreator.Builder builder = new MetaCreator.Builder();
	    builder.addDeploymentPath(DEPLOY).addDataSourcePath(DS_FILE)
		    .setSwapDataSource(true).setScanForEntities(true);

	    Map<String, String> properties = getPersistenceProperties();
	    builder.setPersistenceProperties(properties);

	    MetaCreator creator = builder.build();
	    creator.scanForBeans(DEPLOY_PATH);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event) {
    }

}
