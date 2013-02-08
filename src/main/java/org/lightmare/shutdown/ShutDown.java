package org.lightmare.shutdown;

import org.apache.log4j.Logger;
import org.lightmare.ejb.meta.TmpResources;
import org.lightmare.ejb.startup.MetaCreator;

/**
 * Runnable class for shut down hoock
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

		MetaCreator.closeAllConnections();
		tmpResources.removeTempFiles();
		LOG.info("Lightmare servfer is going to shut down");
	}

}
