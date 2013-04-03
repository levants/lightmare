package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.lightmare.jpa.datasource.PoolConfig.PoolProviderType;
import org.lightmare.jpa.datasource.c3p0.InitDataSourceC3p0;
import org.lightmare.jpa.datasource.tomcat.InitDataSourceTomcat;
import org.lightmare.utils.ObjectUtils;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.DataSources;

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

    public DataSourceInitializer() {
    }

    private static boolean checkForDataSource(String path) {
	return ObjectUtils.available(path);
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

    public static boolean checkDSPath(String datasourcePath) {
	return INITIALIZED_SOURCES.contains(datasourcePath);
    }

    /**
     * Destroys all registered pooled {@link DataSource}s for shut down hook
     */
    public static void cleanUp() {

	@SuppressWarnings("unchecked")
	Set<DataSource> dataSources = C3P0Registry.getPooledDataSources();
	for (DataSource dataSource : dataSources) {
	    try {
		DataSources.destroy(dataSource);
	    } catch (SQLException ex) {
		LOG.error("Could not destroy data source", ex);
	    }
	}
    }

}
