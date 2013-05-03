package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.lightmare.cache.MetaContainer;
import org.lightmare.jndi.NamingUtils;
import org.lightmare.jpa.datasource.PoolConfig.PoolProviderType;
import org.lightmare.jpa.datasource.c3p0.InitDataSourceC3p0;
import org.lightmare.jpa.datasource.tomcat.InitDataSourceTomcat;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Parses xml and property files to initialize and cache {@link DataSource}
 * objects
 * 
 * @author levan
 * 
 */
public class DataSourceInitializer {

    private static final Set<String> INITIALIZED_SOURCES = Collections
	    .synchronizedSet(new HashSet<String>());

    public static final Logger LOG = Logger
	    .getLogger(DataSourceInitializer.class);

    public static final String DRIVER_PROPERTY = "driver";
    public static final String USER_PROPERTY = "user";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String URL_PROPERTY = "url";
    public static final String JNDI_NAME_PROPERTY = "name";

    public DataSourceInitializer() {
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
     * Initialized datasource
     * 
     * @throws IOException
     */
    public static void initializeDataSource(String path) throws IOException {

	if (checkForDataSource(path)
		&& !DataSourceInitializer.checkDSPath(path)) {
	    FileParsers parsers = new FileParsers();
	    parsers.parseStandaloneXml(path);
	}
    }

    /**
     * Initialized datasources
     * 
     * @throws IOException
     */
    public static void initializeDataSources() throws IOException {

	Collection<String> paths = MetaContainer.CONFIG.getDataSourcePath();
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
    public void registerDataSource(Properties properties) throws IOException {

	if (PoolConfig.poolProviderType.equals(PoolProviderType.C3P0)) {
	    InitDataSourceC3p0.registerDataSource(properties);
	} else if (PoolConfig.poolProviderType.equals(PoolProviderType.TOMCAT)) {
	    InitDataSourceTomcat.registerDataSource(properties);
	}
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

	NamingUtils utils = new NamingUtils();
	Context context = utils.getContext();
	try {
	    DataSource dataSource = (DataSource) context.lookup(jndiName);
	    cleanUp(dataSource);
	    dataSource = null;
	    context.unbind(jndiName);
	} catch (NamingException ex) {
	    throw new IOException(ex);
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

	if (PoolConfig.poolProviderType.equals(PoolProviderType.C3P0)) {
	    InitDataSourceC3p0.cleanUp(dataSource);
	} else if (PoolConfig.poolProviderType.equals(PoolProviderType.TOMCAT)) {
	    InitDataSourceTomcat.cleanUp(dataSource);
	}
    }

}
