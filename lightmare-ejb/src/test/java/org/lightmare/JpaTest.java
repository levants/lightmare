package org.lightmare;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.lightmare.cache.ConnectionContainer;
import org.lightmare.config.ConfigKeys;
import org.lightmare.deploy.MetaCreator;
import org.lightmare.logger.Configure;
import org.lightmare.utils.ObjectUtils;
import org.yaml.snakeyaml.Yaml;

@Ignore
public class JpaTest {

    public static DBCreator getDBCreator() throws ClassNotFoundException,
	    IOException, ParseException, InterruptedException,
	    ExecutionException {
	DBCreator creator = getDBCreator(null, null, null, null);
	return creator;
    }

    public static DBCreator getDBCreator(String unitName)
	    throws ClassNotFoundException, IOException, ParseException,
	    InterruptedException, ExecutionException {
	DBCreator creator = getDBCreator(null, null, unitName, null);
	return creator;
    }

    public static DBCreator getDBCreator(String path, String dataSourcePath,
	    String unitName, String jndi) throws ClassNotFoundException,
	    IOException, ParseException, InterruptedException,
	    ExecutionException {
	Configure.configure();
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
	MetaCreator.Builder builder = new MetaCreator.Builder()
		.setPersistenceProperties(properties);
	builder.setPoolPropertiesPath("./pool/pool.properties");
	builder.addPoolProperty("statementCacheNumDeferredCloseThreads", "3");
	builder.setHotDeployment(Boolean.FALSE);
	builder.addDeploymentPath("./lib");
	if (unitName != null) {
	    builder.setScanForEntities(Boolean.TRUE).setUnitName(unitName);
	}
	builder.setSwapDataSource(Boolean.TRUE);
	if (dataSourcePath != null) {
	    builder.addDataSourcePath(dataSourcePath);
	}

	File configFile = new File("./config/configuration.yml");
	if (configFile.exists()) {
	    Yaml yaml = new Yaml();
	    InputStream in = new FileInputStream(configFile);
	    try {
		Map<?, ?> config = (Map<?, ?>) yaml.load(in);
		String key = ConfigKeys.DEPLOY_CONFIG.key;
		if (ObjectUtils.notNull(config) && config.containsKey(key)) {
		    @SuppressWarnings("unchecked")
		    Map<Object, Object> deployConfig = (Map<Object, Object>) config
			    .get(key);
		    key = ConfigKeys.DATASOURCE.key;
		    if (ObjectUtils.notNull(deployConfig)
			    && deployConfig.containsKey(key)) {
			@SuppressWarnings("unchecked")
			Map<Object, Object> datasource = (Map<Object, Object>) deployConfig
				.get(key);
			builder.setDataSource(datasource);
		    }
		}
	    } finally {
		in.close();
	    }
	}

	MetaCreator metaCreator;
	if (path != null) {
	    builder.setXmlFromJar(Boolean.TRUE);
	    File file = new File(path);
	    File[] files = { file };
	    metaCreator = builder.build();
	    metaCreator.scanForBeans(files);
	} else {
	    metaCreator = builder.build();
	    metaCreator.scanForBeans();
	}
	Map<String, URL> classOwnershipURLs = metaCreator.getAnnotationFinder()
		.getClassOwnersURLs();
	Map<String, String> classOwnershipFiles = metaCreator
		.getAnnotationFinder().getClassOwnersFiles();
	System.out
		.println("============URLs of scanned classes ================");
	for (Map.Entry<String, URL> entry : classOwnershipURLs.entrySet()) {
	    System.out.format("%s ------ %s\n", entry.getKey(),
		    entry.getValue());
	}
	System.out
		.println("============Files of scanned classes ================");
	for (Map.Entry<String, String> entry : classOwnershipFiles.entrySet()) {
	    System.out.format("%s ------ %s\n", entry.getKey(),
		    entry.getValue());
	}
	System.out
		.println("====================================================");
	EntityManagerFactory emf = null;
	EntityManager em = null;
	int tryCount = 0;
	while (emf == null || em == null) {
	    if (jndi == null) {
		emf = ConnectionContainer.getEntityManagerFactory(unitName);
		em = emf.createEntityManager();
	    } else {
		try {
		    em = (EntityManager) new InitialContext().lookup(String
			    .format("java:comp/env/%s", jndi));
		} catch (NamingException ex) {
		    ex.printStackTrace();
		}
	    }
	    tryCount++;
	}
	System.out.format("tryes for get EntityManagerFactory are %s\n",
		tryCount);
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
	    EntityManagerFactory emf = ConnectionContainer
		    .getEntityManagerFactory("testUnit");
	    Assert.assertNotNull("could not create EntityManagerFactory", emf);
	    System.out.println(emf);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @AfterClass
    public static void end() {
	try {
	    ConnectionContainer.closeConnections();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }
}
