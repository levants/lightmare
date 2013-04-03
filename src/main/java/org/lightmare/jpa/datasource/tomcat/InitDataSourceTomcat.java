package org.lightmare.jpa.datasource.tomcat;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.lightmare.jndi.NamingUtils;
import org.lightmare.jpa.datasource.DataSourceInitializer;
import org.lightmare.jpa.datasource.PoolConfig;

/**
 * Initializes and bind to {@link Context} tomcat pooled {@link DataSource}
 * object
 * 
 * @author levan
 * 
 */
public class InitDataSourceTomcat {

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

	Map<Object, Object> configMap = PoolConfig.configProperties(properties);

	DataSource dataSource;
	PoolProperties poolProperties = new PoolProperties();
	poolProperties.setUrl(url);
	poolProperties.setDriverClassName(driver);
	poolProperties.setUsername(user);
	poolProperties.setPassword(password);
	poolProperties.setJmxEnabled(true);
	poolProperties.setTestWhileIdle(false);
	poolProperties.setTestOnBorrow(true);
	poolProperties.setValidationQuery("SELECT 1");
	poolProperties.setTestOnReturn(false);
	poolProperties.setValidationInterval(30000);
	poolProperties.setTimeBetweenEvictionRunsMillis(30000);
	poolProperties.setMaxActive(PoolConfig.asInt(configMap,
		PoolConfig.MAX_POOL_SIZE));
	poolProperties.setInitialSize(PoolConfig.asInt(configMap,
		PoolConfig.INITIAL_POOL_SIZE));
	poolProperties.setMaxWait(10000);
	poolProperties.setRemoveAbandonedTimeout(60);
	poolProperties.setMinEvictableIdleTimeMillis(30000);
	poolProperties.setMinIdle(10);
	poolProperties.setLogAbandoned(true);
	poolProperties.setRemoveAbandoned(true);
	poolProperties
		.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
			+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
	dataSource = new DataSource();
	dataSource.setPoolProperties(poolProperties);

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
	try {
	    DataSource dataSource = initilizeDataSource(properties);
	    if (dataSource instanceof DataSource) {
		NamingUtils namingUtils = new NamingUtils();
		Context context = namingUtils.getContext();
		context.rebind(jndiName, dataSource);
	    } else {
		throw new IOException(
			String.format(
				"Could not initialize data source %s (it is not PooledDataSource instance)",
				jndiName));
	    }
	    LOG.info(String.format("Data source %s initialized", jndiName));
	} catch (NamingException ex) {
	    LOG.error(String.format("Could not initialize data source %s",
		    jndiName), ex);
	} catch (Exception ex) {
	    LOG.error(String.format("Could not initialize data source %s",
		    jndiName), ex);
	}
    }
}
