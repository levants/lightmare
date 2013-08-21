package org.lightmare.utils.shutdown;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.RestContainer;
import org.lightmare.cache.TmpResources;
import org.lightmare.deploy.MetaCreator;

/**
 * Runnable class for shut down hook
 * 
 * @author levan
 * 
 */
public class ShutDown implements Runnable {

    private TmpResources tmpResources;

    private static final Logger LOG = Logger.getLogger(ShutDown.class);

    public ShutDown(TmpResources tmpResources) {
	this.tmpResources = tmpResources;
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
    }

    @Override
    public void run() {

	tmpResources.removeTempFiles();
	try {
	    clearAll();
	} catch (IOException ex) {
	    LOG.fatal(ex.getMessage(), ex);
	}
	LOG.info("Lightmare server is going to shut down");
    }

    /**
     * Sets shut down hook for application
     * 
     * @param tmpResources
     */
    public static void setHook(TmpResources tmpResources) {

	ShutDown shutDown = new ShutDown(tmpResources);
	Thread shutDownThread = new Thread(shutDown);
	Runtime runtime = Runtime.getRuntime();
	runtime.addShutdownHook(shutDownThread);
    }

}
