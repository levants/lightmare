package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.Context;
import javax.sql.DataSource;

import org.lightmare.config.Configuration;
import org.lightmare.jndi.JndiManager;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Parses XML and property files to initialize and cache {@link DataSource}
 * objects
 * 
 * @author Levan Tsinadze
 * @since 0.0.80-SNAPSHOT
 */
public abstract class Initializer {

    // Caches already initialized data source file paths
    private static final Set<String> INITIALIZED_SOURCES = Collections
	    .synchronizedSet(new HashSet<String>());

    // Caches already initialized data source JNDI names
    private static final Set<String> INITIALIZED_NAMES = Collections
	    .synchronizedSet(new HashSet<String>());

    // Locks data base driver initialization to avoid thread blocking
    private static final Lock DRIVER_LOCK = new ReentrantLock();

    /**
     * Container for connection configuration properties
     * 
     * @author Levan Tsinadze
     * 
     */
    public static enum ConnectionConfig {

	DRIVER_PROPERTY("driver"), // driver
	USER_PROPERTY("user"), // user
	PASSWORD_PROPERTY("password"), // password
	URL_PROPERTY("url"), // URL
	JNDI_NAME_PROPERTY("jndiname"), // JNDI name
	NAME_PROPERTY("name");// data source name

	public String name;

	private ConnectionConfig(String name) {
	    this.name = name;
	}
    }

    /**
     * Checks if passed file path is valid data source file path
     * 
     * @param path
     * @return <code>boolean</code>
     */
    private static boolean checkForDataSource(String path) {
	return StringUtils.valid(path);
    }

    /**
     * Gets JNDI name from passed data source configuration {@link Properties}
     * instance
     * 
     * @param properties
     * @return {@link String}
     */
    public static String getJndiName(Properties properties) {

	String jndiName = properties
		.getProperty(ConnectionConfig.JNDI_NAME_PROPERTY.name);

	return jndiName;
    }

    /**
     * Loads JDBC driver class
     * 
     * @param driver
     */
    public static void initializeDriver(String driver) throws IOException {

	boolean locked = Boolean.FALSE;
	while (ObjectUtils.notTrue(locked)) {

	    locked = ObjectUtils.tryLock(DRIVER_LOCK);
	    if (locked) {
		try {
		    MetaUtils.initClassForName(driver);
		} finally {
		    ObjectUtils.unlock(DRIVER_LOCK);
		}
	    }
	}
    }

    /**
     * Initialized data source from passed file path
     * 
     * @throws IOException
     */
    public static void initializeDataSource(String path) throws IOException {

	boolean valid = checkForDataSource(path)
		&& ObjectUtils.notTrue(Initializer.checkDSPath(path));
	if (valid) {
	    FileParsers parsers = new FileParsers();
	    parsers.parseStandaloneXml(path);
	}
    }

    /**
     * Initializes data sources from passed {@link Configuration} instance
     * 
     * @throws IOException
     */
    public static void initializeDataSources(Configuration config)
	    throws IOException {

	Collection<String> paths = config.getDataSourcePath();
	if (CollectionUtils.valid(paths)) {
	    for (String path : paths) {
		initializeDataSource(path);
	    }
	}
    }

    /**
     * Initializes and registers {@link DataSource} object in JNDI by
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
     * Closes and removes from {@link Context} data source with specified JNDI
     * name
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
     * Closes and removes from {@link Context} all initialized and cached data
     * sources
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
     * Closes and removes from {@link Context} all data sources from passed file
     * path
     * 
     * @param dataSourcePath
     * @throws IOException
     */
    public static void undeploy(String dataSourcePath) throws IOException {

	Collection<String> jndiNames = FileParsers
		.dataSourceNames(dataSourcePath);
	if (StringUtils.valid(dataSourcePath)) {
	    for (String jndiName : jndiNames) {
		close(jndiName);
	    }
	}

	removeInitialized(dataSourcePath);
    }

    /**
     * Cleans and destroys passed {@link DataSource} instance
     * 
     * @param dataSource
     */
    public static void cleanUp(DataSource dataSource) {
	InitDataSourceFactory.destroy(dataSource);
    }
}
