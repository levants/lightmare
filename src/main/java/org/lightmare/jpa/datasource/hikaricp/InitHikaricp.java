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

    // Connection timeout
    private static final long CONNECTION_TIMEOUT = 30000;

    /**
     * Default properties of HikariDB connection pool
     * 
     * @author Levan Tsinadze
     *
     */
    private static enum HikariDefault {

	MAX_LIFETIME("maxLifetime", 1800000), // Lifetime
	LEAK_DETECTION("leakDetectionThreshold", 0), // Leak detection
	INIT_FAIL_FAST("initializationFailFast", Boolean.TRUE); // Initialization
								// fail

	private final String key;

	private final Object value;

	private HikariDefault(final String key, final Object value) {
	    this.key = key;
	    this.value = value;
	}

	public Integer asInt() {
	    return ObjectUtils.cast(value, Integer.class);
	}

	public Boolean asBoolean() {
	    return ObjectUtils.cast(value, Boolean.class);
	}

	public Integer asInt(Map<Object, Object> properties) {

	    Integer property = PoolConfig.asInt(properties, key);

	    if (property == null) {
		property = asInt();
	    }

	    return property;
	}

	public Boolean asBoolean(Map<Object, Object> properties) {

	    Boolean property = PoolConfig.asBoolean(properties, key);

	    if (property == null) {
		property = asBoolean();
	    }

	    return property;
	}
    }

    public InitHikaricp(Properties properties) {
	super(properties);
    }

    private void setAutoCommit(Map<Object, Object> configMap,
	    HikariConfig config) {
	config.setAutoCommit(Boolean.FALSE);
	config.setIsolateInternalQueries(Boolean.TRUE);
    }

    private void setConnection(Map<Object, Object> configMap,
	    HikariConfig config) {

	config.setUsername(user);
	config.setPassword(password);
	config.setJdbcUrl(url);
	String name = PoolConfig.asText(configMap,
		Defaults.DATA_SOURCE_NAME.key);
	config.setPoolName(name);
	config.setDriverClassName(driver);
    }

    private void setTimeouts(Map<Object, Object> configMap, HikariConfig config) {

	config.setConnectionTimeout(CONNECTION_TIMEOUT);
	config.setIdleTimeout(PoolConfig.asInt(configMap,
		Defaults.MAX_IDLE_TIMEOUT.key));
	Integer maxLifetime = HikariDefault.MAX_LIFETIME.asInt(configMap);
	config.setMaxLifetime(maxLifetime);
	Integer leakDetection = HikariDefault.LEAK_DETECTION.asInt(configMap);
	config.setLeakDetectionThreshold(leakDetection);
	Boolean initFaulFast = HikariDefault.INIT_FAIL_FAST
		.asBoolean(configMap);
	config.setInitializationFailFast(initFaulFast);
    }

    private void setPoolSize(Map<Object, Object> configMap, HikariConfig config) {

	config.setMinimumIdle(PoolConfig.asInt(configMap,
		Defaults.MIN_POOL_SIZE.key));
	config.setMaximumPoolSize(PoolConfig.asInt(configMap,
		Defaults.MAX_POOL_SIZE.key));
    }

    @Override
    protected DataSource initializeDataSource() throws IOException {

	HikariDataSource hikariDataSource;

	Map<Object, Object> configMap = poolConfig.merge(properties);
	HikariConfig config = new HikariConfig();

	// Auto commit
	setAutoCommit(configMap, config);
	// Connection properties
	setConnection(configMap, config);
	// timeouts
	setTimeouts(configMap, config);
	// Pool size
	setPoolSize(configMap, config);

	// initializes data source with configuration
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
