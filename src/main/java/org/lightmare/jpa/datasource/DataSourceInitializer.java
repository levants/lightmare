package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.lightmare.config.Configuration;
import org.lightmare.jndi.JndiManager;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Parses XML and property files to initialize and cache {@link DataSource}
 * objects
 * 
 * @author levan
 * 
 */
public class DataSourceInitializer {

    // Caches already initialized data source file paths
    private static final Set<String> INITIALIZED_SOURCES = Collections
	    .synchronizedSet(new HashSet<String>());

    // Caches already initialized data source JNDI names
    private static final Set<String> INITIALIZED_NAMES = Collections
	    .synchronizedSet(new HashSet<String>());

    public static final Logger LOG = Logger
	    .getLogger(DataSourceInitializer.class);

    // Connection properties
    public static final String DRIVER_PROPERTY = "driver";
    public static final String USER_PROPERTY = "user";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String URL_PROPERTY = "url";
    public static final String JNDI_NAME_PROPERTY = "jndiname";
    public static final String NAME_PROPERTY = "name";

    private DataSourceInitializer() {
    }

    private static boolean checkForDataSource(String path) {

	return ObjectUtils.available(path);
    }

    public static String getJndiName(Properties properties) {

	String jndiName = properties
		.getProperty(DataSourceInitializer.JNDI_NAME_PROPERTY);

	return jndiName;
    }

    /**
     * Loads jdbc driver class
     * 
     * @param driver
     */
    public static void initializeDriver(String driver) throws IOException {

	MetaUtils.initClassForName(driver);
    }

    /**
     * Initialized data source
     * 
     * @throws IOException
     */
    public static void initializeDataSource(String path) throws IOException {

	boolean valid = checkForDataSource(path)
		&& ObjectUtils.notTrue(DataSourceInitializer.checkDSPath(path));
	if (valid) {
	    FileParsers parsers = new FileParsers();
	    parsers.parseStandaloneXml(path);
	}
    }

    /**
     * Initialized data sources
     * 
     * @throws IOException
     */
    public static void initializeDataSources(Configuration config)
	    throws IOException {

	Collection<String> paths = config.getDataSourcePath();
	if (ObjectUtils.available(paths)) {
	    for (String path : paths) {
		initializeDataSource(path);
	    }
	}
    }

    /**
     * Initializes and registers {@link DataSource} object in jndi by
     * {@link Properties} {@link Context}
     * 
     * @param poolingProperties
     * @param dataSource
     * @param jndiName
     * @throws IOException
     */
    public static void registerDataSource(Properties properties)
	    throws IOException {

	InitDataSource initDataSource = InitDataSourceFactory.get(properties);
	initDataSource.create();

	// Caches jndiName for data source
	String jndiName = getJndiName(properties);
	INITIALIZED_NAMES.add(jndiName);
    }

    public static void setDsAsInitialized(String datasourcePath) {

	INITIALIZED_SOURCES.add(datasourcePath);
    }

    public static void removeInitialized(String datasourcePath) {

	INITIALIZED_SOURCES.remove(datasourcePath);
    }

    public static boolean checkDSPath(String datasourcePath) {

	return INITIALIZED_SOURCES.contains(datasourcePath);
    }

    /**
     * Closes and unbinds from context data source with specified jndi name
     * 
     * @param jndiName
     * @throws IOException
     */
    public static void close(String jndiName) throws IOException {

	JndiManager jndiManager = new JndiManager();
	DataSource dataSource = jndiManager.lookup(jndiName);
	if (ObjectUtils.notNull(dataSource)) {
	    cleanUp(dataSource);
	}
	dataSource = null;
	jndiManager.unbind(jndiName);
	INITIALIZED_NAMES.remove(jndiName);
    }

    /**
     * Closes and unbinds from context all existing sources
     * 
     * @throws IOException
     */
    public static void closeAll() throws IOException {

	Set<String> dataSources = new HashSet<String>(INITIALIZED_NAMES);
	for (String jndiName : dataSources) {
	    close(jndiName);
	}
    }

    /**
     * Closes and unbinds from {@link Context} all data sources from passed file
     * path
     * 
     * @param dataSourcePath
     * @throws IOException
     */
    public static void undeploy(String dataSourcePath) throws IOException {

	Collection<String> jndiNames = FileParsers
		.dataSourceNames(dataSourcePath);
	if (ObjectUtils.available(dataSourcePath)) {

	    for (String jndiName : jndiNames) {
		close(jndiName);
	    }
	}
	removeInitialized(dataSourcePath);
    }

    /**
     * Clean and destroy data source
     * 
     * @param dataSource
     */
    public static void cleanUp(DataSource dataSource) {

	InitDataSourceFactory.destroy(dataSource);
    }
}
