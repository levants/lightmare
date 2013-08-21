package org.lightmare.jpa.datasource;

import java.util.Properties;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * Initializes and bind to {@link Context} pooled {@link DataSource} object
 * 
 * @author levan
 * 
 */
public abstract class InitDataSource {

    protected static final Logger LOG = Logger
	    .getLogger(DataSourceInitializer.class);

    private Properties properties;

    private String driver;
    private String url;
    private String user;
    private String password;

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
}
