package org.lightmare.jpa.datasource.c3p0;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.sql.DataSource;

import org.lightmare.jpa.datasource.InitDataSource;
import org.lightmare.jpa.datasource.InitMessages;
import org.lightmare.jpa.datasource.Initializer;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PooledDataSource;

/**
 * Initializes and bind to {@link Context} c3p0 pooled {@link DataSource} object
 * 
 * @author levan
 * 
 */
public class InitC3p0 extends InitDataSource {

    public InitC3p0(Properties properties) {
	super(properties);
    }

    /**
     * Initializes appropriated driver and {@link DataSource} objects
     * 
     * @param properties
     * @return {@link DataSource}
     * @throws IOException
     */
    @Override
    public DataSource initializeDataSource() throws IOException {

	DataSource namedDataSource;

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
		Initializer.initializeDriver(driver);
		dataSource = DataSources
			.unpooledDataSource(url, user, password);
	    }

	    Map<Object, Object> configMap = poolConfig.merge(properties);
	    namedDataSource = DataSources.pooledDataSource(dataSource,
		    configMap);

	} catch (SQLException ex) {
	    throw new IOException(ex);
	} catch (PropertyVetoException ex) {
	    throw new IOException(ex);
	}

	return namedDataSource;
    }

    @Override
    protected boolean checkInstance(DataSource dataSource) throws IOException {

	boolean valid = (dataSource instanceof PooledDataSource);

	return valid;
    }

    /**
     * Destroys passed {@link DataSource} for shut down
     * 
     * @param dataSource
     */
    @Override
    public void cleanUp(DataSource dataSource) {

	try {
	    DataSources.destroy(dataSource);
	} catch (SQLException ex) {
	    LOG.error(InitMessages.COULD_NOT_CLOSE_ERROR, ex);
	}
    }
}
