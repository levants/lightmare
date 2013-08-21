package org.lightmare.jpa.datasource.dbcp;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.SharedPoolDataSource;
import org.lightmare.jpa.datasource.DataSourceInitializer;
import org.lightmare.jpa.datasource.InitDataSource;
import org.lightmare.jpa.datasource.InitMessages;
import org.lightmare.jpa.datasource.PoolConfig;

/**
 * Initializes and bind to {@link Context} c3p0 pooled {@link DataSource} object
 * 
 * @author levan
 * 
 */
public class InitDataSourceDbcp extends InitDataSource {

    private static final int DEFAULT_TRANSACTION_ISOLATION = 1;

    public InitDataSourceDbcp(Properties properties) {
	super(properties);
    }

    @Override
    public DataSource initializeDataSource() throws IOException {

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

    @Override
    protected boolean checkForInstance(DataSource dataSource)
	    throws IOException {

	boolean valid = (dataSource instanceof DataSource);

	return valid;
    }

    @Override
    public void cleanUp(javax.sql.DataSource dataSource) {

	if (dataSource instanceof DataSource) {
	    try {
		((SharedPoolDataSource) dataSource).close();
	    } catch (Exception ex) {
		LOG.error(InitMessages.COULD_NOT_CLOSE_ERROR, ex);
	    }
	}
    }
}
