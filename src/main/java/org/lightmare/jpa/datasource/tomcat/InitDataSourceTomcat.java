package org.lightmare.jpa.datasource.tomcat;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.lightmare.jpa.datasource.InitDataSource;
import org.lightmare.jpa.datasource.PoolConfig;
import org.lightmare.utils.StringUtils;

/**
 * Initializes and bind to {@link Context} tomcat pooled {@link DataSource}
 * object
 * 
 * @author levan
 * 
 */
public class InitDataSourceTomcat extends InitDataSource {

    private static final String JDBC_INTERCEPTOR_KEY = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;";

    private static final String JDBC_INTERCEPTOR_VALUE = "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";

    private static final String TEST_SQL = "SELECT 1";

    public InitDataSourceTomcat(Properties properties) {
	super(properties);
    }

    @Override
    public DataSource initializeDataSource() throws IOException {

	Map<Object, Object> configMap = poolConfig.merge(properties);

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
	poolProperties.setValidationInterval(30000);
	poolProperties.setTimeBetweenEvictionRunsMillis(30000);
	poolProperties.setMaxActive(PoolConfig.asInt(configMap,
		PoolConfig.DefaultConfig.MAX_POOL_SIZE));
	poolProperties.setInitialSize(PoolConfig.asInt(configMap,
		PoolConfig.DefaultConfig.INITIAL_POOL_SIZE));
	poolProperties.setMaxWait(10000);
	poolProperties.setRemoveAbandonedTimeout(60);
	poolProperties.setMinEvictableIdleTimeMillis(30000);
	poolProperties.setMinIdle(10);
	poolProperties.setLogAbandoned(Boolean.TRUE);
	poolProperties.setRemoveAbandoned(Boolean.TRUE);
	poolProperties.setJdbcInterceptors(StringUtils.concat(
		JDBC_INTERCEPTOR_KEY, JDBC_INTERCEPTOR_VALUE));
	dataSource = new DataSource();
	dataSource.setPoolProperties(poolProperties);

	return dataSource;
    }

    @Override
    protected boolean checkInstance(javax.sql.DataSource dataSource)
	    throws IOException {

	boolean valid = (dataSource instanceof DataSource);

	return valid;
    }

    @Override
    public void cleanUp(javax.sql.DataSource dataSource) {

	if (dataSource instanceof DataSource) {
	    ((DataSource) dataSource).close();
	}
    }
}
