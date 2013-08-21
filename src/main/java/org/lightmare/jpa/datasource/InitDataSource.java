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

    public InitDataSource(Properties properties) {

	this.properties = properties;
	String driver = properties.getProperty(
		DataSourceInitializer.DRIVER_PROPERTY).trim();
	String url = properties.getProperty(DataSourceInitializer.URL_PROPERTY)
		.trim();
	String user = properties.getProperty(
		DataSourceInitializer.USER_PROPERTY).trim();
	String password = properties.getProperty(
		DataSourceInitializer.PASSWORD_PROPERTY).trim();
    }
}
