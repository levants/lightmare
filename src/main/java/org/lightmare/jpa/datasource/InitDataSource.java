package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.lightmare.config.Configuration;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.datasource.Initializer.ConnectionConfig;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.LogUtils;

/**
 * Initializes and bind to {@link Context} pooled {@link DataSource} object
 * 
 * @author levan
 * 
 */
public abstract class InitDataSource {

    // Additional data source properties
    protected Properties properties;

    protected PoolConfig poolConfig;

    // Initial properties for data source initialization
    protected String driver;
    protected String url;
    protected String user;
    protected String password;

    protected static final Logger LOG = Logger.getLogger(Initializer.class);

    public InitDataSource(Properties properties) {

	if (CollectionUtils.valid(properties)) {

	    this.properties = properties;
	    this.poolConfig = Configuration.getPoolConfig();

	    driver = properties.getProperty(
		    ConnectionConfig.DRIVER_PROPERTY.name).trim();
	    url = properties.getProperty(ConnectionConfig.URL_PROPERTY.name)
		    .trim();
	    user = properties.getProperty(ConnectionConfig.USER_PROPERTY.name)
		    .trim();
	    password = properties.getProperty(
		    ConnectionConfig.PASSWORD_PROPERTY.name).trim();
	}
    }

    /**
     * Initializes appropriated driver and {@link DataSource} objects
     * 
     * @return {@link DataSource}
     * @throws IOException
     */
    protected abstract DataSource initializeDataSource() throws IOException;

    /**
     * Checks if passed {@link DataSource} is instance of appropriated
     * 
     * @param dataSource
     * @throws IOException
     */
    protected abstract boolean checkInstance(DataSource dataSource)
	    throws IOException;

    /**
     * Initializes and registers {@link DataSource} object in JNDI
     * {@link javax.naming.Context}
     * 
     * @param poolingProperties
     * @param dataSource
     * @param jndiName
     * @throws IOException
     */
    public void create() throws IOException {

	String jndiName = Initializer.getJndiName(properties);
	LogUtils.info(LOG, InitMessages.INITIALIZING_MESSAGE, jndiName);

	try {

	    DataSource dataSource = initializeDataSource();
	    boolean valid = checkInstance(dataSource);
	    if (valid) {
		JndiManager namingUtils = new JndiManager();
		namingUtils.rebind(jndiName, dataSource);
	    } else {
		throw new IOException(String.format(
			InitMessages.NOT_APPR_INSTANCE_ERROR, jndiName));
	    }

	    LogUtils.info(LOG, InitMessages.INITIALIZED_MESSAGE, jndiName);
	} catch (IOException ex) {
	    LogUtils.error(LOG, ex, InitMessages.COULD_NOT_INIT_ERROR, jndiName);
	} catch (Exception ex) {
	    LogUtils.error(LOG, ex, InitMessages.COULD_NOT_INIT_ERROR, jndiName);
	}
    }

    /**
     * Destroys passed {@link DataSource} instance
     * 
     * @param dataSource
     */

    public abstract void cleanUp(DataSource dataSource);
}
