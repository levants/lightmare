package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.lightmare.config.Configuration;
import org.lightmare.jndi.JndiManager;

/**
 * Initializes and bind to {@link Context} pooled {@link DataSource} object
 * 
 * @author levan
 * 
 */
public abstract class InitDataSource {

    protected static final Logger LOG = Logger
	    .getLogger(DataSourceInitializer.class);

    protected Properties properties;

    protected PoolConfig poolConfig;

    protected String driver;
    protected String url;
    protected String user;
    protected String password;

    public InitDataSource(Properties properties) {

	this.properties = properties;
	this.poolConfig = Configuration.getPoolConfig();
	driver = properties.getProperty(DataSourceInitializer.DRIVER_PROPERTY)
		.trim();
	url = properties.getProperty(DataSourceInitializer.URL_PROPERTY).trim();
	user = properties.getProperty(DataSourceInitializer.USER_PROPERTY)
		.trim();
	password = properties.getProperty(
		DataSourceInitializer.PASSWORD_PROPERTY).trim();
    }

    /**
     * Initializes appropriated driver and {@link DataSource} objects
     * 
     * @return {@link DataSource}
     * @throws IOException
     */
    protected abstract DataSource initializeDataSource() throws IOException;

    /**
     * Checks if passed {@link DataSource} is appropriated
     * 
     * @param dataSource
     * @throws IOException
     */
    protected abstract boolean checkForInstance(DataSource dataSource)
	    throws IOException;

    /**
     * Initializes and registers {@link DataSource} object in jndi by
     * {@link Properties} {@link Context}
     * 
     * @param poolingProperties
     * @param dataSource
     * @param jndiName
     * @throws IOException
     */
    public void registerDataSource() throws IOException {
	String jndiName = DataSourceInitializer.getJndiName(properties);
	LOG.info(String.format(InitMessages.INITIALIZING_MESSAGE, jndiName));
	try {
	    DataSource dataSource = initializeDataSource();
	    boolean valid = checkForInstance(dataSource);
	    if (valid) {
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
     * Destroys passed {@link DataSource} for shut down
     * 
     * @param dataSource
     */

    public abstract void cleanUp(DataSource dataSource);
}
