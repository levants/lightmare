package org.lightmare.utils.shutdown;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.RestContainer;
import org.lightmare.cache.TmpResources;
import org.lightmare.deploy.LoaderPoolManager;
import org.lightmare.utils.CollectionUtils;

/**
 * Runnable class for shut down hook
 * 
 * @author levan
 * 
 */
public class ShutDown implements Runnable {

    private List<TmpResources> resources;

    private static final String SHUTDOWN_MESSAGE = "Lightmare server is going to shut down";

    // Boolean check if shutdown hook is set
    private static final AtomicBoolean HOOK_NOT_SET = new AtomicBoolean(
	    Boolean.TRUE);

    // Keeps instance of ShutDown to add temporal resources
    private static ShutDown shutDown;

    private static final Logger LOG = Logger.getLogger(ShutDown.class);

    public ShutDown(TmpResources tmpResources) {

	getResources().add(tmpResources);
    }

    private List<TmpResources> getResources() {

	if (resources == null) {
	    resources = new ArrayList<TmpResources>();
	}

	return resources;
    }

    /**
     * Adds {@link TmpResources} to cache
     * 
     * @param tmpResources
     */
    private void addTmpResources(TmpResources tmpResources) {
	getResources().add(tmpResources);
    }

    /**
     * Clears cache and closes all resources
     * 
     * @throws IOException
     */
    public static void clearAll() throws IOException {

	ConnectionContainer.clear();
	MetaContainer.clear();
	RestContainer.clear();
	LoaderPoolManager.reload();
	ShutDown.clear();
    }

    @Override
    public void run() {

	try {

	    synchronized (this) {

		if (CollectionUtils.valid(resources)) {
		    for (TmpResources tmpResources : resources) {
			tmpResources.removeTempFiles();
		    }
		}

		clearAll();
	    }

	} catch (IOException ex) {
	    LOG.fatal(ex.getMessage(), ex);
	}

	LOG.info(SHUTDOWN_MESSAGE);
    }

    /**
     * Sets shut down hook for application
     * 
     * @param tmpResources
     */
    public static void setHook(TmpResources tmpResources) {

	// Checks if shutdown hook is set
	if (HOOK_NOT_SET.getAndSet(Boolean.FALSE)) {
	    shutDown = new ShutDown(tmpResources);
	    Thread shutDownThread = new Thread(shutDown);
	    Runtime runtime = Runtime.getRuntime();
	    runtime.addShutdownHook(shutDownThread);
	} else {
	    shutDown.addTmpResources(tmpResources);
	}
    }

    /**
     * Clears cached resources from {@link ShutDown} class
     */
    private static void clear() {

	synchronized (ShutDown.class) {
	    shutDown = null;
	}
    }
}
