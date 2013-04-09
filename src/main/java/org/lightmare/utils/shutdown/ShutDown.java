package org.lightmare.utils.shutdown;

import org.apache.log4j.Logger;
import org.lightmare.deploy.MetaCreator;
import org.lightmare.ejb.meta.TmpResources;

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

    @Override
    public void run() {

	tmpResources.removeTempFiles();
	MetaCreator.closeAllConnections();
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
