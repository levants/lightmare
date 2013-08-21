package org.lightmare.jpa.datasource.dbcp;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.SharedPoolDataSource;
import org.apache.log4j.Logger;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.datasource.DataSourceInitializer;
import org.lightmare.jpa.datasource.InitMessages;
import org.lightmare.jpa.datasource.PoolConfig;

/**
 * Initializes and bind to {@link Context} c3p0 pooled {@link DataSource} object
 * 
 * @author levan
 * 
 */
public class InitDataSourceDbcp {

    private static final int DEFAULT_TRANSACTION_ISOLATION = 1;

    public static final Logger LOG = Logger
	    .getLogger(DataSourceInitializer.class);

    /**
     * Initializes appropriated driver and {@link DataSource} objects
     * 
     * @param properties
     * @return {@link DataSource}
     * @throws IOException
     */
    public static DataSource initilizeDataSource(Properties properties)
	    throws IOException {

	String driver = properties.getProperty(
		DataSourceInitializer.DRIVER_PROPERTY).trim();
	String url = properties.getProperty(DataSourceInitializer.URL_PROPERTY)
		.trim();
	String user = properties.getProperty(
		DataSourceInitializer.USER_PROPERTY).trim();
	String password = properties.getProperty(
		DataSourceInitializer.PASSWORD_PROPERTY).trim();

	String jndiName = DataSourceInitializer.getJndiName(properties);

	DriverAdapterCPDS dacp = new DriverAdapterCPDS();

	try {
	    dacp.setDriver(driver);
	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	}
	dacp.setUrl(url);
	dacp.setUser(user);
	dacp.setPassword(password);

	SharedPoolDataSource dataSource = new SharedPoolDataSource();
	dataSource.setDataSourceName(jndiName);
	dataSource.setDefaultAutoCommit(Boolean.FALSE);
	dataSource.setDefaultReadOnly(Boolean.FALSE);
	dataSource
		.setDefaultTransactionIsolation(DEFAULT_TRANSACTION_ISOLATION);
	dataSource.setLoginTimeout(PoolConfig.asInt(properties,
		PoolConfig.MAX_IDLE_TIMEOUT));
	dataSource.setMaxActive(PoolConfig.asInt(properties,
		PoolConfig.MAX_POOL_SIZE));
	dataSource.setMaxIdle(PoolConfig.asInt(properties,
		PoolConfig.MAX_IDLE_TIMEOUT));
	dataSource.setMaxWait(PoolConfig.asInt(properties,
		PoolConfig.MAX_IDLE_TIMEOUT));

	return dataSource;
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
	String jndiName = DataSourceInitializer.getJndiName(properties);
	LOG.info(String.format(InitMessages.INITIALIZING_MESSAGE, jndiName));
	try {
	    DataSource dataSource = initilizeDataSource(properties);
	    if (dataSource instanceof DataSource) {
		JndiManager namingUtils = new JndiManager();
		namingUtils.rebind(jndiName, dataSource);
	    } else {
		throw new IOException(String.format(
			InitMessages.NOT_APPR_INSTANCE_ERROR, jndiName));
	    }
	    LOG.info(String.format(InitMessages.INITIALIZED_MESSAGE, jndiName));
	} catch (IOException ex) {
	    LOG.error(
		    String.format(InitMessages.COULD_NOT_INIT_ERROR, jndiName),
		    ex);
	} catch (Exception ex) {
	    LOG.error(
		    String.format(InitMessages.COULD_NOT_INIT_ERROR, jndiName),
		    ex);
	}
    }

    /**
     * Closes passed {@link javax.sql.DataSource} for shut down
     * 
     * @param dataSource
     */
    public static void cleanUp(javax.sql.DataSource dataSource) {

	if (dataSource instanceof DataSource) {
	    try {
		((SharedPoolDataSource) dataSource).close();
	    } catch (Exception ex) {
		LOG.error(InitMessages.COULD_NOT_CLOSE_ERROR, ex);
	    }
	}
    }
}
