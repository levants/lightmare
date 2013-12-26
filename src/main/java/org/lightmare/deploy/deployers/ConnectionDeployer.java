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
import org.lightmare.utils.ObjectUtils;

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

    private static final Logger LOG = Logger
	    .getLogger(ConnectionDeployer.class);

    public ConnectionDeployer(DataSourceParameters parameters) {

	this.properties = parameters.properties;
	this.blocker = parameters.blocker;
    }

    private void releaseBlocker() {

	if (ObjectUtils.notTrue(countedDown)) {
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
	    LOG.error(InitMessages.INITIALIZING_ERROR, ex);
	} finally {
	    releaseBlocker();
	    LibraryLoader.loadCurrentLibraries(loader);
	}

	return result;
    }
}
