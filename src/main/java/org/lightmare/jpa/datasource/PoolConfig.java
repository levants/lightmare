package org.lightmare.jpa.datasource;

/**
 * Configuration with default parameters for c3p0 connection pooling
 * 
 * @author levan
 * 
 */
public class PoolConfig {

    public static final String MAX_POOL_SIZE = "maxPoolSize";
    public static final String MIN_POOL_SIZE = "minPoolSize";
    public static final String MAX_IDLE_TIMEOUT = "maxIdleTime";
    public static final String MAX_STATEMENTS = "maxStatements";
    public static final String AQUIRE_INCREMENT = "acquireIncrement";

    public static final String MAX_POOL_SIZE_DEF_VALUE = "15";
    public static final String MIN_POOL_SIZE_DEF_VALUE = "5";
    public static final String MAX_IDLE_TIMEOUT_DEF_VALUE = "1800";
    public static final String MAX_STATEMENTS_DEF_VALUE = "50";
    public static final String AQUIRE_INCREMENT_DEF_VALUE = "50";

}
