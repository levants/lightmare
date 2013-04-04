package org.lightmare.jpa.datasource.c3p0;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.lightmare.jndi.NamingUtils;
import org.lightmare.jpa.JPAManager;
import org.lightmare.jpa.datasource.DataSourceInitializer;
import org.lightmare.jpa.datasource.PoolConfig;

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
    public static DataSource initilizeDataSource(Properties properties)
	    throws IOException {

	String driver = properties.getProperty("driver").trim();
	String url = properties.getProperty("url").trim();
	String user = properties.getProperty("user").trim();
	String password = properties.getProperty("password").trim();

	DataSource dataSource;
	try {
	    if (JPAManager.pooledDataSource) {
		dataSource = new ComboPooledDataSource();
		((ComboPooledDataSource) dataSource).setDriverClass(driver);
		((ComboPooledDataSource) dataSource).setJdbcUrl(url);
		((ComboPooledDataSource) dataSource).setUser(user);
		((ComboPooledDataSource) dataSource).setPassword(password);
	    } else {
		dataSource = DataSources
			.unpooledDataSource(url, user, password);
		// ((DriverManagerDataSource)
		// dataSource).setDriverClass(driver);
		DataSourceInitializer.initializeDriver(driver);
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
    public static void registerDataSource(Properties properties)
	    throws IOException {
	String jndiName = DataSourceInitializer.getJndiName(properties);
	LOG.info(String.format("Initializing data source %s", jndiName));
	Map<Object, Object> configMap = PoolConfig.configProperties(properties);
	try {
	    DataSource dataSource = initilizeDataSource(properties);
	    DataSource namedDataSource = DataSources.pooledDataSource(
		    dataSource, configMap);
	    if (namedDataSource instanceof PooledDataSource) {
		NamingUtils namingUtils = new NamingUtils();
		Context context = namingUtils.getContext();
		context.rebind(jndiName, namedDataSource);
	    } else {
		throw new IOException(
			String.format(
				"Could not initialize data source %s (it is not PooledDataSource instance)",
				jndiName));
	    }
	    LOG.info(String.format("Data source %s initialized", jndiName));
	} catch (SQLException ex) {
	    LOG.error(String.format("Could not initialize data source %s",
		    jndiName), ex);
	} catch (NamingException ex) {
	    LOG.error(String.format("Could not initialize data source %s",
		    jndiName), ex);
	} catch (Exception ex) {
	    LOG.error(String.format("Could not initialize data source %s",
		    jndiName), ex);
	}
    }
}
