package org.lightmare.jpa.datasource;

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
}
