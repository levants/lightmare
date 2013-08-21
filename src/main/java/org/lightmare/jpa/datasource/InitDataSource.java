package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.DataSources;

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

    protected String driver;
    protected String url;
    protected String user;
    protected String password;

    public InitDataSource(Properties properties) {

	this.properties = properties;
	driver = properties.getProperty(DataSourceInitializer.DRIVER_PROPERTY)
		.trim();
	url = properties.getProperty(DataSourceInitializer.URL_PROPERTY).trim();
	user = properties.getProperty(DataSourceInitializer.USER_PROPERTY)
		.trim();
	password = properties.getProperty(
		DataSourceInitializer.PASSWORD_PROPERTY).trim();
    }

    protected abstract DataSource initializeDataSource(Properties properties)
	    throws IOException;

    /**
     * Destroys passed {@link DataSource} for shut down
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
}
