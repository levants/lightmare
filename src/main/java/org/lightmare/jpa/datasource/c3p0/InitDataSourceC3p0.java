package org.lightmare.jpa.datasource.c3p0;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.datasource.DataSourceInitializer;
import org.lightmare.jpa.datasource.InitMessages;
import org.lightmare.jpa.datasource.PoolConfig;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PooledDataSource;

/**
 * Initializes and bind to {@link Context} c3p0 pooled {@link DataSource} object
 * 
 * @author levan
 * 
 */
public class InitDataSourceC3p0 {

    public static final Logger LOG = Logger
	    .getLogger(DataSourceInitializer.class);

    /**
     * Initializes appropriated driver and {@link DataSource} objects
     * 
     * @param properties
     * @return {@link DataSource}
     * @throws IOException
     */
    public static DataSource initilizeDataSource(Properties properties,
	    PoolConfig poolConfig) throws IOException {

	String driver = properties.getProperty("driver").trim();
	String url = properties.getProperty("url").trim();
	String user = properties.getProperty("user").trim();
	String password = properties.getProperty("password").trim();

	DataSource dataSource;
	try {
	    if (poolConfig.isPooledDataSource()) {
		ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
		comboPooledDataSource.setDriverClass(driver);
		comboPooledDataSource.setJdbcUrl(url);
		comboPooledDataSource.setUser(user);
		comboPooledDataSource.setPassword(password);
		dataSource = comboPooledDataSource;
	    } else {
		DataSourceInitializer.initializeDriver(driver);
		dataSource = DataSources
			.unpooledDataSource(url, user, password);
	    }
	} catch (SQLException ex) {
	    throw new IOException(ex);
	} catch (PropertyVetoException ex) {
	    throw new IOException(ex);
	}

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
    public static void registerDataSource(Properties properties,
	    PoolConfig poolConfig) throws IOException {
	String jndiName = DataSourceInitializer.getJndiName(properties);
	LOG.info(String.format(InitMessages.INITIALIZING_MESSAGE, jndiName));
	Map<Object, Object> configMap = poolConfig.merge(properties);
	try {
	    DataSource dataSource = initilizeDataSource(properties, poolConfig);
	    DataSource namedDataSource = DataSources.pooledDataSource(
		    dataSource, configMap);
	    if (namedDataSource instanceof PooledDataSource) {
		JndiManager namingUtils = new JndiManager();
		namingUtils.rebind(jndiName, namedDataSource);
	    } else {
		throw new IOException(String.format(
			InitMessages.NOT_APPR_INSTANCE_ERROR, jndiName));
	    }
	    LOG.info(String.format(InitMessages.INITIALIZED_MESSAGE, jndiName));
	} catch (SQLException ex) {
	    LOG.error(
		    String.format(InitMessages.COULD_NOT_INIT_ERROR, jndiName),
		    ex);
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
     * Descroys passed {@link DataSource} for shut down
     * 
     * @param dataSource
     */
    public static void cleanUp(DataSource dataSource) {

	try {
	    DataSources.destroy(dataSource);
	} catch (SQLException ex) {
	    LOG.error(InitMessages.COULD_NOT_CLOSE_ERROR, ex);
	}
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
		LOG.error(InitMessages.COULD_NOT_CLOSE_ERROR, ex);
	    }
	}
    }
}
