package org.lightmare.jpa.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.lightmare.config.Configuration;
import org.lightmare.jpa.datasource.PoolConfig.PoolProviderType;
import org.lightmare.jpa.datasource.c3p0.InitC3p0;
import org.lightmare.jpa.datasource.dbcp.InitDBCP;
import org.lightmare.jpa.datasource.tomcat.InitTomcat;

/**
 * Factory class to get {@link InitDataSource} instance
 * 
 * @author Levan
 * 
 */
public abstract class InitDataSourceFactory {

    /**
     * Constructs appropriate {@link InitDataSource} instance
     * 
     * @return {@link InitDataSource}
     */
    public static InitDataSource get(Properties properties) {

	InitDataSource initDataSource;

	PoolConfig poolConfig = Configuration.getPoolConfig();

	if (poolConfig.getPoolProviderType().equals(PoolProviderType.C3P0)) {
	    initDataSource = new InitC3p0(properties);
	} else if (poolConfig.getPoolProviderType().equals(
		PoolProviderType.TOMCAT)) {
	    initDataSource = new InitTomcat(properties);
	} else if (poolConfig.getPoolProviderType().equals(
		PoolProviderType.DBCP)) {
	    initDataSource = new InitDBCP(properties);
	} else {
	    initDataSource = null;
	}

	return initDataSource;
    }

    /**
     * Constructs appropriate {@link InitDataSource} instance to close or
     * destroy appropriate {@link DataSource} instance
     * 
     * @return {@link InitDataSource}
     */
    public static void destroy(DataSource dataSource) {

	InitDataSource initDataSource = get(null);
	initDataSource.cleanUp(dataSource);
    }
}
