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
    private static final AtomicBoolean HOOK_SET = new AtomicBoolean(
	    Boolean.FALSE);

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

    private void setTmpResources(TmpResources tmpResources) {
	this.resources = tmpResources;
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
    }

    @Override
    public void run() {

	try {

	    resources.removeTempFiles();
	    clearAll();

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
	if (HOOK_SET.getAndSet(Boolean.TRUE)) {
	    ShutDown shutDown = new ShutDown(tmpResources);
	    Thread shutDownThread = new Thread(shutDown);
	    Runtime runtime = Runtime.getRuntime();
	    runtime.addShutdownHook(shutDownThread);
	}
    }
}
