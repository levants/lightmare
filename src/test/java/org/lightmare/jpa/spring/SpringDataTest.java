package org.lightmare.jpa.spring;

import java.io.IOException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.lightmare.jpa.datasource.Initializer;
import org.lightmare.jpa.hibernate.jpa.HibernatePersistenceProviderExt;
import org.lightmare.utils.ObjectUtils;

@Ignore
public class SpringDataTest {

    private static final String UNIT_NAME = "testUnit";

    private static final String DATA_SOURCE_PATH = "./ds/standalone.xml";

    private static final String DATA_SOURCE_NAME = "java:/personsDerby";

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
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void getEmfTest() {

	HibernatePersistenceProviderExt.Builder builder = new HibernatePersistenceProviderExt.Builder();
	PersistenceProvider persistenceProvider = builder.build();
	SpringData springData = new SpringData.Builder(DATA_SOURCE_NAME,
		persistenceProvider, UNIT_NAME).properties(properties).build();

	EntityManagerFactory emf = null;
	try {
	    emf = springData.getEmf();
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
