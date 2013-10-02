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
 * @author levan
 * @since 0.0.79-SNAPSHOT
 */
public class InitTomcat extends InitDataSource {

    /**
     * Container for Tomcat default configurations
     * 
     * @author levan
     * 
     */
    protected static enum TomcatConfig {

	JDBC_INTERCEPTOR(
		"org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;",
		"org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

	public String key;

	public String value;

	private TomcatConfig(String key, String value) {
	    this.key = key;
	    this.value = value;
	}
    }

    private static final String TEST_SQL = "SELECT 1";

    public InitTomcat(Properties properties) {
	super(properties);
    }

    @Override
    public DataSource initializeDataSource() throws IOException {

	Map<Object, Object> configMap = poolConfig.merge(properties);

	int checkOutTimeout = PoolConfig.asInt(configMap,
		PoolConfig.Defaults.CHECK_OUT_TIMEOUT);
	int exeededTimeout = PoolConfig.asInt(configMap,
		PoolConfig.Defaults.MAX_IDLE_TIME_EXCESS_CONN);

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
	poolProperties.setMaxWait(10000);
	poolProperties.setRemoveAbandonedTimeout(exeededTimeout);
	poolProperties.setMinEvictableIdleTimeMillis(checkOutTimeout);
	poolProperties.setMinIdle(10);
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

	boolean valid = (dataSource instanceof DataSource);

	return valid;
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
