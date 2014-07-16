package org.lightmare.jpa.spring;

import java.io.IOException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.JpaManager;
import org.lightmare.jpa.datasource.FileParsers;
import org.lightmare.jpa.hibernate.jpa.HibernatePersistenceProviderExt;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.ObjectUtils;

@Ignore
public class SpringORMTest {

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
	properties.putAll(JndiManager.JNDIConfigs.INIT.hinbernateConfig);

	try {
	    FileParsers.parseDataSources(DATA_SOURCE_PATH);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    @Test
    public void getEmfTest() {

	HibernatePersistenceProviderExt.Builder builder = new HibernatePersistenceProviderExt.Builder();
	PersistenceProvider persistenceProvider = builder
		.setSwapDataSource(Boolean.TRUE).setScanArchives(Boolean.TRUE)
		.setOverridenClassLoader(LibraryLoader.getContextClassLoader())
		.build();
	SpringORM springData = new SpringORM.Builder(DATA_SOURCE_NAME,
		persistenceProvider, UNIT_NAME).properties(properties)
		.swapDataSource(Boolean.TRUE).build();

	EntityManagerFactory emf = null;
	EntityManager em = null;
	try {
	    emf = springData.getEmf();
	    em = emf.createEntityManager();
	    System.out.format("EntityManager - %s\n", em);
	    System.out.format("EntityManagerfactory - %s\n", emf);
	} catch (Exception ex) {
	    ex.printStackTrace();
	} finally {
	    JpaManager.closeEntityManager(em);
	    if (ObjectUtils.notNull(emf)) {
		emf.close();
	    }
	}
    }
}
