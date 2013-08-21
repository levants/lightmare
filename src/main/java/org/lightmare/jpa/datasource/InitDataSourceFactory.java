package org.lightmare.jpa.datasource;

import java.util.Properties;

import org.lightmare.config.Configuration;
import org.lightmare.jpa.datasource.PoolConfig.PoolProviderType;
import org.lightmare.jpa.datasource.c3p0.InitDataSourceC3p0;
import org.lightmare.jpa.datasource.dbcp.InitDataSourceDbcp;
import org.lightmare.jpa.datasource.tomcat.InitDataSourceTomcat;

/**
 * Factory class to get {@link InitDataSource} instance
 * 
 * @author Levan
 * 
 */
public class InitDataSourceFactory {

    /**
     * Constructs appropriate {@link InitDataSource} instance
     * 
     * @return
     */
    public static InitDataSource get(Properties properties) {

	InitDataSource initDataSource;

	PoolConfig poolConfig = Configuration.getPoolConfig();

	if (poolConfig.getPoolProviderType().equals(PoolProviderType.C3P0)) {
	    initDataSource = new InitDataSourceC3p0(properties);
	} else if (poolConfig.getPoolProviderType().equals(
		PoolProviderType.TOMCAT)) {
	    initDataSource = new InitDataSourceTomcat(properties);
	} else if (poolConfig.getPoolProviderType().equals(
		PoolProviderType.DBCP)) {
	    initDataSource = new InitDataSourceDbcp(properties);
	} else {
	    initDataSource = null;
	}

	return initDataSource;
    }
}
