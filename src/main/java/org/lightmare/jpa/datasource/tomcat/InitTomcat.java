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
package org.lightmare.jpa.datasource.tomcat;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.lightmare.jpa.datasource.InitDataSource;
import org.lightmare.jpa.datasource.PoolConfig;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

/**
 * Initializes and bind to {@link Context} tomcat pooled {@link DataSource}
 * object
 * 
 * @author Levan Tsinadze
 * @since 0.0.79-SNAPSHOT
 */
public class InitTomcat extends InitDataSource {

    // Max wait property
    private static final int MAX_WAIT = 10000;

    // Statement to check connection
    private static final String TEST_SQL = "SELECT 1";

    /**
     * Container for Tomcat default configurations
     * 
     * @author Levan Tsinadze
     * @since 0.0.81-SNAPSHOT
     */
    protected static enum TomcatConfig {

	JDBC_INTERCEPTOR(
		"org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;",
		"org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer"); // Intercepts
									       // JDBC
									       // statement

	public String key;

	public String value;

	private TomcatConfig(String key, String value) {
	    this.key = key;
	    this.value = value;
	}
    }

    public InitTomcat(Properties properties) {
	super(properties);
    }

    @Override
    public DataSource initializeDataSource() throws IOException {

	Map<Object, Object> configMap = poolConfig.merge(properties);

	int checkOutTimeout = PoolConfig.asInt(configMap,
		PoolConfig.Defaults.CHECK_OUT_TIMEOUT.key);
	int exeededTimeout = PoolConfig.asInt(configMap,
		PoolConfig.Defaults.MAX_IDLE_TIME_EXCESS_CONN.key);
	int minPoolSize = PoolConfig.asInt(configMap,
		PoolConfig.Defaults.MIN_POOL_SIZE.key);

	DataSource dataSource;
	PoolProperties poolProperties = new PoolProperties();
	poolProperties.setUrl(url);
	poolProperties.setDriverClassName(driver);
	poolProperties.setUsername(user);
	poolProperties.setPassword(password);
	poolProperties.setJmxEnabled(Boolean.TRUE);
	poolProperties.setTestWhileIdle(Boolean.FALSE);
	poolProperties.setTestOnBorrow(Boolean.TRUE);
	poolProperties.setValidationQuery(TEST_SQL);
	poolProperties.setTestOnReturn(Boolean.FALSE);
	poolProperties.setValidationInterval(checkOutTimeout);
	poolProperties.setTimeBetweenEvictionRunsMillis(checkOutTimeout);
	poolProperties.setMaxActive(PoolConfig.asInt(configMap,
		PoolConfig.Defaults.MAX_POOL_SIZE));
	poolProperties.setInitialSize(PoolConfig.asInt(configMap,
		PoolConfig.Defaults.INITIAL_POOL_SIZE));
	poolProperties.setMaxWait(MAX_WAIT);
	poolProperties.setRemoveAbandonedTimeout(exeededTimeout);
	poolProperties.setMinEvictableIdleTimeMillis(checkOutTimeout);
	poolProperties.setMinIdle(minPoolSize);
	poolProperties.setLogAbandoned(Boolean.TRUE);
	poolProperties.setRemoveAbandoned(Boolean.TRUE);
	poolProperties.setJdbcInterceptors(StringUtils.concat(
		TomcatConfig.JDBC_INTERCEPTOR.key,
		TomcatConfig.JDBC_INTERCEPTOR.value));
	dataSource = new DataSource();
	dataSource.setPoolProperties(poolProperties);

	return dataSource;
    }

    @Override
    protected boolean checkInstance(javax.sql.DataSource dataSource)
	    throws IOException {
	return (dataSource instanceof DataSource);
    }

    @Override
    public void cleanUp(javax.sql.DataSource dataSource) {

	DataSource pooledDataSource;
	if (dataSource instanceof DataSource) {
	    pooledDataSource = ObjectUtils.cast(dataSource, DataSource.class);
	    pooledDataSource.close();
	}
    }
}
