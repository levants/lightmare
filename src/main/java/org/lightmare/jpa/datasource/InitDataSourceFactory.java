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
 * @author Levan Tsinadze
 * @since 0.0.80-SNAPSHOT
 */
public class InitDataSourceFactory {

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
