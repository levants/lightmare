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
package org.lightmare.deploy.deployers;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.lightmare.deploy.BeanLoader.DataSourceParameters;
import org.lightmare.deploy.LoaderPoolManager;
import org.lightmare.jpa.datasource.InitMessages;
import org.lightmare.jpa.datasource.Initializer;
import org.lightmare.libraries.LibraryLoader;

/**
 * {@link Runnable} implementation for initializing and deploying
 * {@link javax.sql.DataSource}
 *
 * @author Levan Tsinadze
 * @since 0.0.45-SNAPSHOT
 */
public class ConnectionDeployer implements Callable<Boolean> {

    private Properties properties;

    private final CountDownLatch blocker;

    private boolean countedDown;

    private static final Logger LOG = Logger.getLogger(ConnectionDeployer.class);

    public ConnectionDeployer(DataSourceParameters parameters) {
	this.properties = parameters.properties;
	this.blocker = parameters.blocker;
    }

    private void releaseBlocker() {

	if (Boolean.FALSE.equals(countedDown)) {
	    blocker.countDown();
	    countedDown = Boolean.TRUE;
	}
    }

    @Override
    public Boolean call() throws Exception {

	boolean result;

	ClassLoader loader = LoaderPoolManager.getCurrent();
	try {
	    Initializer.registerDataSource(properties);
	    result = Boolean.TRUE;
	} catch (IOException ex) {
	    result = Boolean.FALSE;
	    LOG.error(InitMessages.INITIALIZING_ERROR.message, ex);
	} finally {
	    releaseBlocker();
	    LibraryLoader.loadCurrentLibraries(loader);
	}

	return result;
    }
}
