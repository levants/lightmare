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
package org.lightmare.utils.finalizers;

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
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

/**
 * Runnable class for shut down hook
 * 
 * @author Levan Tsinadze
 * @since o.o.26-SNAPSHOT
 */
public class ShutDown implements Runnable {

    // Collection of TmpResources instances to clear on shutdown hook
    private List<TmpResources> resources;

    private static final String SHUTDOWN_THREAD_NAME = "shutdown-hook-thread-";

    // Message logged at shut down time when hook starts
    private static final String SHUTDOWN_MESSAGE = "Lightmare server is going to shut down";

    // Boolean check if shutdown hook is set
    private static final AtomicBoolean HOOK_NOT_SET = new AtomicBoolean(
	    Boolean.TRUE);

    // Keeps instance of ShutDown to add temporal resources
    private static ShutDown shutDown;

    private static final Logger LOG = Logger.getLogger(ShutDown.class);

    /**
     * Constructor with param for removal
     * 
     * @param tmpResources
     *            to cache
     */
    public ShutDown(TmpResources tmpResources) {
	getResources().add(tmpResources);
    }

    /**
     * Initializes (if null) and gets {@link TmpResources} collection
     * 
     * @return
     */
    private List<TmpResources> getResources() {

	if (resources == null) {
	    resources = new ArrayList<TmpResources>();
	}

	return resources;
    }

    /**
     * Adds {@link TmpResources} to cache for cleaning when shutdown hook
     * activates
     * 
     * @param tmpResources
     */
    private void addTmpResources(TmpResources tmpResources) {
	getResources().add(tmpResources);
    }

    /**
     * Clears and / or closes all cached resources
     * 
     * @throws IOException
     */
    public static void clearAll() throws IOException {

	ConnectionContainer.clear();
	MetaContainer.clear();
	RestContainer.clear();
	LoaderPoolManager.reload();
    }

    /**
     * Removes all temporal resources
     * 
     * @throws IOException
     */
    private void clearTmpResources() throws IOException {

	if (CollectionUtils.valid(resources)) {
	    for (TmpResources tmpResources : resources) {

		// Clears all temporal resources held in this
		// TmpResources
		// instance
		tmpResources.removeTempFiles();
	    }
	    // Clears resources
	    resources.clear();
	}
	// Clears all cached data
	clearAll();
	clearHook();
    }

    @Override
    public void run() {

	try {
	    synchronized (this) {
		clearTmpResources();
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
	    String name = StringUtils.concat(SHUTDOWN_THREAD_NAME,
		    shutDownThread.getId());
	    shutDownThread.setName(name);
	    shutDownThread.setDaemon(Boolean.TRUE);
	    Runtime runtime = Runtime.getRuntime();
	    runtime.addShutdownHook(shutDownThread);
	} else {
	    // Adds passed TmpResources instances to existing ShutDown instance
	    shutDown.addTmpResources(tmpResources);
	}
    }

    /**
     * Clears cached resources from {@link ShutDown} class
     */
    private static void clearHook() {

	synchronized (ShutDown.class) {
	    // Prevents null pointer exception for SutDown instance
	    boolean hookIsSet = Boolean.FALSE;
	    while (ObjectUtils.notTrue(hookIsSet)) {
		hookIsSet = HOOK_NOT_SET.getAndSet(Boolean.TRUE);
	    }
	    // Nulls cached ShutDown instance for PermGen sake
	    shutDown = null;
	}
    }
}
