package org.lightmare.jpa.spring;

import java.io.IOException;
import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.datasource.Initializer;
import org.lightmare.jpa.hibernate.jpa.HibernatePersistenceProviderExt;
import org.lightmare.utils.ObjectUtils;

public class SpringDataTest {

    private static final String UNIT_NAME = "testUnit";

    private static final String DATA_SOURCE_PATH = "./ds/standalone.xml";

    private static DataSource dataSource;

    private static Properties properties;

    @BeforeClass
    public static void configure() {

	properties = new Properties();

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

	try {
	    Initializer.initializeDataSource(DATA_SOURCE_PATH);
	    dataSource = (DataSource) JndiManager.getContext().lookup(
		    "java:/personsDerby");
	} catch (IOException ex) {
	    ex.printStackTrace();
	} catch (NamingException ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void getEmfTest() {

	HibernatePersistenceProviderExt.Builder builder = new HibernatePersistenceProviderExt.Builder();
	PersistenceProvider persistenceProvider = builder.build();
	SpringData springData = new SpringData(dataSource, persistenceProvider,
		properties);

	EntityManagerFactory emf = null;
	try {
	    emf = springData.getEmf(UNIT_NAME);
	    System.out.println(emf);
	} catch (Exception ex) {
	    ex.printStackTrace();
	} finally {
	    if (ObjectUtils.notNull(emf)) {
		emf.close();
	    }
	}
    }
}
