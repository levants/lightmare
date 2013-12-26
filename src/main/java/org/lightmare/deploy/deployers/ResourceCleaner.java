package org.lightmare.deploy.deployers;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.lightmare.deploy.LoaderPoolManager;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.LogUtils;
import org.lightmare.utils.fs.FileUtils;

/**
 * {@link Runnable} implementation for temporal resources removal
 * 
 * @author Levan Tsinadze
 * @since 0.0.45-SNAPSHOT
 */
public class ResourceCleaner implements Callable<Boolean> {

    private List<File> tmpFiles;

    private static final Logger LOG = Logger.getLogger(ResourceCleaner.class);

    public ResourceCleaner(List<File> tmpFiles) {
	this.tmpFiles = tmpFiles;
    }

    /**
     * Removes temporal resources after deploy {@link Thread} notifies
     * 
     * @throws InterruptedException
     */
    private void clearTmpData() throws InterruptedException {

	synchronized (tmpFiles) {
	    tmpFiles.wait();
	}

	for (File tmpFile : tmpFiles) {
	    FileUtils.deleteFile(tmpFile);
	    LogUtils.info(LOG, "Cleaning temporal resource %s done",
		    tmpFile.getName());
	}
    }

    @Override
    public Boolean call() throws Exception {

	boolean result;

	ClassLoader loader = LoaderPoolManager.getCurrent();
	try {
	    clearTmpData();
	    result = Boolean.TRUE;
	} catch (InterruptedException ex) {
	    result = Boolean.FALSE;
	    LOG.error("Coluld not clean temporary resources", ex);
	} finally {
	    LibraryLoader.loadCurrentLibraries(loader);
	}

	return result;
    }
}
