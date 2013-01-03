package org.lightmare;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.lightmare.ejb.startup.MetaCreator;
import org.lightmare.jpa.JPAManager;

@Ignore
public class JpaTest {

	public static DBCreator getDBCreator() throws ClassNotFoundException,
			IOException, ParseException {
		DBCreator creator = getDBCreator(null, null);
		return creator;
	}

	public static DBCreator getDBCreator(String unitName)
			throws ClassNotFoundException, IOException, ParseException {
		DBCreator creator = getDBCreator(null, unitName);
		return creator;
	}

	public static DBCreator getDBCreator(String path, String unitName)
			throws ClassNotFoundException, IOException, ParseException {
		Map<String, String> properties = new HashMap<String, String>();

		properties.put("hibernate.default_schema", "PERSONS");
		properties.put("hibernate.show_sql", "true");
		properties.put("hibernate.sql_trace", "true");

		// properties.put("hibernate.connection.url",
		// "jdbc:derby:target/database/jpa-test-database;create=true");

		// properties.put("hibernate.connection.username", "user");
		// properties.put("hibernate.connection.password", "password");

		properties.put("hibernate.dialect",
				"org.hibernate.dialect.DerbyDialect");
		properties.put("hibernate.cache.provider_class",
				"org.hibernate.cache.NoCacheProvider");
		properties.put("hibernate.connection.driver_class",
				"org.apache.derby.jdbc.EmbeddedDriver");
		properties.put("hibernate.hbm2ddl.auto", "create-drop");
		// <!-- hiberanate key generation properties -->
		properties.put("hibernate.jdbc.use_get_generated_keys", "true");
		properties.put("hibernate.max_fetch_depth", "3");
		MetaCreator.Builder builder = new MetaCreator.Builder()
				.setPersistenceProperties(properties);
		if (unitName != null) {
			builder.setScanForEntities(true).setUnitName(unitName);
		}
		if (path != null) {
			builder.setPersXmlPath(path);
		}
		builder.setSwapDataSource(true).setXmlFromJar(true)
				.setDataSourcePath("./ds/standalone.xml");
		MetaCreator metaCreator = builder.build();
		File file = new File(JarFileReaderTest.JAR_PATH);
		File[] files = { file };
		metaCreator.scanForBeans(files);
		EntityManagerFactory emf = JPAManager.getConnection("testUnit");
		EntityManager em = emf.createEntityManager();
		DBCreator creator = new DBCreator(em);
		creator.createDB();

		return creator;
	}

	@BeforeClass
	public static void start() {

		try {
			getDBCreator();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void addEntityTest() {
		try {
			EntityManagerFactory emf = JPAManager.getConnection("testUnit");
			Assert.assertNotNull("could not create EntityManagerFactory", emf);
			System.out.println(emf);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@AfterClass
	public static void end() {
		MetaCreator.closeAllConnections();
	}
}
