/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
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
package org.lightmare.jpa.datasource.dbcp;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.SharedPoolDataSource;
import org.lightmare.jpa.datasource.InitDataSource;
import org.lightmare.jpa.datasource.InitMessages;
import org.lightmare.jpa.datasource.Initializer;
import org.lightmare.jpa.datasource.PoolConfig;
import org.lightmare.utils.ObjectUtils;

/**
 * Initializes and bind to {@link Context} c3p0 pooled {@link DataSource} object
 * 
 * @author Levan Tsinadze
 * @since 0.0.79-SNAPSHOT
 */
public class InitDBCP extends InitDataSource {

    private static final int DEFAULT_TRANSACTION_ISOLATION = 1;

    public InitDBCP(Properties properties) {
	super(properties);
    }

    @Override
    public DataSource initializeDataSource() throws IOException {

	String jndiName = Initializer.getJndiName(properties);

	DriverAdapterCPDS dacp = new DriverAdapterCPDS();

	try {
	    dacp.setDriver(driver);
	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	}
	dacp.setUrl(url);
	dacp.setUser(user);
	dacp.setPassword(password);

	SharedPoolDataSource dataSource = new SharedPoolDataSource();
	dataSource.setDataSourceName(jndiName);
	dataSource.setDefaultAutoCommit(Boolean.FALSE);
	dataSource.setDefaultReadOnly(Boolean.FALSE);
	dataSource
		.setDefaultTransactionIsolation(DEFAULT_TRANSACTION_ISOLATION);
	dataSource.setLoginTimeout(PoolConfig.asInt(properties,
		PoolConfig.Defaults.MAX_IDLE_TIMEOUT));
	dataSource.setMaxActive(PoolConfig.asInt(properties,
		PoolConfig.Defaults.MAX_POOL_SIZE));
	dataSource.setMaxIdle(PoolConfig.asInt(properties,
		PoolConfig.Defaults.MAX_IDLE_TIMEOUT));
	dataSource.setMaxWait(PoolConfig.asInt(properties,
		PoolConfig.Defaults.CHECK_OUT_TIMEOUT));

	return dataSource;
    }

    @Override
    protected boolean checkInstance(DataSource dataSource) throws IOException {

	boolean valid = (dataSource instanceof DataSource);

	return valid;
    }

    @Override
    public void cleanUp(DataSource dataSource) {

	SharedPoolDataSource pooledDataSource;
	if (dataSource instanceof SharedPoolDataSource) {
	    try {
		pooledDataSource = ObjectUtils.cast(dataSource,
			SharedPoolDataSource.class);
		pooledDataSource.close();
	    } catch (Exception ex) {
		LOG.error(InitMessages.COULD_NOT_CLOSE_ERROR.message, ex);
	    }
	}
    }
}
