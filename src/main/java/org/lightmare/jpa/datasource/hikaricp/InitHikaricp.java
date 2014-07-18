package org.lightmare.jpa.datasource.hikaricp;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.sql.DataSource;

import org.lightmare.jpa.datasource.InitDataSource;
import org.lightmare.jpa.datasource.PoolConfig;
import org.lightmare.jpa.datasource.PoolConfig.Defaults;
import org.lightmare.utils.ObjectUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Initializes and bind to {@link Context} HikariCP pooled {@link DataSource}
 * object
 * 
 * @author Levan Tsinadze
 * @since 0.1.3
 *
 */
public class InitHikaricp extends InitDataSource {

    // Configuration keys
    private static final String MAX_LIFETIME_KEY = "maxLifetime";

    private static final String LEAK_DETECTION_KEY = "leakDetectionThreshold";

    private static final String INIT_FAIL_FAST_KEY = "initializationFailFast";

    // Connection timeout
    private static final long CONNECTION_TIMEOUT = 30000;

    private static final int MAX_LIFETIME = 1800000;

    private static final int LEAK_DETECTION = 0;

    private static final int INIT_FAIL_FAST = 120000;

    public InitHikaricp(Properties properties) {
	super(properties);
    }

    @Override
    protected DataSource initializeDataSource() throws IOException {

	HikariDataSource hikariDataSource;

	Map<Object, Object> configMap = poolConfig.merge(properties);
	HikariConfig config = new HikariConfig();

	// Auto commit
	config.setAutoCommit(Boolean.FALSE);
	config.setIsolateInternalQueries(Boolean.TRUE);

	// Connection properties
	config.setUsername(user);
	config.setPassword(password);
	config.setJdbcUrl(url);
	String name = PoolConfig.asText(configMap,
		Defaults.DATA_SOURCE_NAME.key);
	config.setPoolName(name);
	config.setDriverClassName(driver);

	// timeouts
	config.setConnectionTimeout(CONNECTION_TIMEOUT);
	config.setIdleTimeout(PoolConfig.asInt(configMap,
		Defaults.MAX_IDLE_TIMEOUT.key));
	Integer maxLifetime = PoolConfig.asInt(configMap, MAX_LIFETIME_KEY);
	if (maxLifetime == null) {
	    maxLifetime = MAX_LIFETIME;
	}
	config.setMaxLifetime(maxLifetime);
	Integer leakDetection = PoolConfig.asInt(configMap, LEAK_DETECTION_KEY);
	if (leakDetection == null) {
	    leakDetection = LEAK_DETECTION;
	}
	config.setLeakDetectionThreshold(LEAK_DETECTION);

//	Boolean initFaulFast = PoolConfig.asBoolean(configMap, INIT_FAIL_FAST_KEY);
//	if (initFaulFast == null) {
//	    initFaulFast = INIT_FAIL_FAST;
//	}
//	config.setInitializationFailFast(Boolean.TRUE);

	// Pool size
	config.setMinimumIdle(PoolConfig.asInt(configMap,
		Defaults.MIN_POOL_SIZE.key));
	config.setMaximumPoolSize(PoolConfig.asInt(configMap,
		Defaults.MAX_POOL_SIZE.key));

	hikariDataSource = new HikariDataSource(config);

	return hikariDataSource;
    }

    @Override
    protected boolean checkInstance(DataSource dataSource) {
	return (dataSource instanceof HikariDataSource);
    }

    /**
     * Destroys passed {@link DataSource} for shut down
     * 
     * @param dataSource
     */
    @Override
    public void cleanUp(DataSource dataSource) {

	if (checkInstance(dataSource)) {
	    HikariDataSource hikariDataSource = ObjectUtils.cast(dataSource,
		    HikariDataSource.class);
	    hikariDataSource.close();
	}
    }
}
