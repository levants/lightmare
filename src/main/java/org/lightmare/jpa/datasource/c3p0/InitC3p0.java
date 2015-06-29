/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
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
 * @author Levan Tsinadze
 * @since 0.0.79-SNAPSHOT
 */
public class InitC3p0 extends InitDataSource {

    public InitC3p0(Properties properties) {
	super(properties);
    }

    /**
     * Initializes and configures {@link DataSource} by properties
     *
     * @return {@link DataSource} initialized and configured
     * @throws PropertyVetoException
     * @throws SQLException
     * @throws IOException
     */
    private DataSource configure() throws PropertyVetoException, SQLException, IOException {

	DataSource dataSource;

	if (poolConfig.isPooledDataSource()) {
	    ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
	    comboPooledDataSource.setDriverClass(driver);
	    comboPooledDataSource.setJdbcUrl(url);
	    comboPooledDataSource.setUser(user);
	    comboPooledDataSource.setPassword(password);
	    dataSource = comboPooledDataSource;
	} else {
	    // Initializes and loads data base driver class by name
	    Initializer.initializeDriver(driver);
	    dataSource = DataSources.unpooledDataSource(url, user, password);
	}

	return dataSource;
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

	try {
	    DataSource dataSource = configure();
	    // Merges configuration with immutable values
	    Map<Object, Object> configMap = poolConfig.merge(properties);
	    namedDataSource = DataSources.pooledDataSource(dataSource, configMap);
	} catch (SQLException ex) {
	    throw new IOException(ex);
	} catch (PropertyVetoException ex) {
	    throw new IOException(ex);
	}

	return namedDataSource;
    }

    @Override
    protected boolean checkInstance(DataSource dataSource) throws IOException {
	return (dataSource instanceof PooledDataSource);
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
	    LOG.error(InitMessages.COULD_NOT_CLOSE_ERROR.message, ex);
	}
    }
}
