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
package org.lightmare.jpa.datasource;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.lightmare.deploy.BeanLoader;

/**
 * Parses XML files to initialize {@link javax.sql.DataSource}s and bind them to
 * <a href="http://www.oracle.com/technetwork/java/jndi/index.html">jndi</a>
 * {@link javax.naming.Context} by name
 * 
 * @author Levan Tsinadze
 * @since 0.0.15-SNAPSHOT
 */
public class FileParsers {

    private static final Logger LOG = Logger.getLogger(FileParsers.class);

    /**
     * Initializes and connection pool
     * 
     * @param properties
     * @param blocker
     */
    private void initDatasource(Properties properties, CountDownLatch blocker) {
	try {
	    // Initializes and fills BeanLoader.DataSourceParameters class
	    // to deploy data source
	    BeanLoader.DataSourceParameters parameters = new BeanLoader.DataSourceParameters();
	    parameters.properties = properties;
	    parameters.blocker = blocker;

	    BeanLoader.initializeDatasource(parameters);
	} catch (IOException ex) {
	    LOG.error(InitMessages.INITIALIZING_ERROR.message, ex);
	}
    }

    /**
     * Parses standalone.xml file and initializes {@link javax.sql.DataSource}s
     * and binds them to JNDI context
     * 
     * @param dataSourcePath
     * @throws IOException
     */
    public void parseStandaloneXml(String dataSourcePath) throws IOException {

	List<Properties> datasources = XMLFileParsers
		.getPropertiesFromJBoss(dataSourcePath);
	// Blocking semaphore before all data source initialization finished
	CountDownLatch blocker = new CountDownLatch(datasources.size());
	for (Properties properties : datasources) {
	    initDatasource(properties, blocker);
	}
	// Tries to lock until operation is complete
	try {
	    blocker.await();
	} catch (InterruptedException ex) {
	    throw new IOException(ex);
	}

	Initializer.setDsAsInitialized(dataSourcePath);
    }
}
