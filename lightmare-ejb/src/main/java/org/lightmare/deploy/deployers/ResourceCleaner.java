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

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.lightmare.deploy.LoaderPoolManager;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.fs.FileUtils;
import org.lightmare.utils.logging.LogUtils;

/**
 * {@link Runnable} implementation for temporal resources removal
 *
 * @author Levan Tsinadze
 * @since 0.0.45
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
