/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2014, Levan Tsinadze, or third-party contributors as
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
package org.lightmare.deploy;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lightmare.cache.MetaContainer;
import org.lightmare.deploy.deployers.LoaderThreadFactory;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.ObjectUtils;

/**
 * Manager class for application deployment in parallel mode
 *
 * @author Levan Tsinadze
 * @since 0.0.77
 */
public abstract class LoaderPoolManager {

    // Amount of deployment thread pool
    private static final int LOADER_POOL_SIZE = 5;

    // Lock for pool reopening
    private static final Lock LOCK = new ReentrantLock();

    // Time amount (in milliseconds) for trying lock
    private static final long LOCK_TIME = 10;

    // Thread pool for deploying and removal of beans and temporal resources
    private static ExecutorService LOADER_POOL;

    /**
     * Gets current {@link MetaCreator}'s cached {@link ClassLoader} for threads
     *
     * @param creator
     * @return {@link ClassLoader} from {@link MetaCreator}'s cache
     */
    private static ClassLoader getCurrent(MetaCreator creator) {

        ClassLoader current;

        // Gets class loader for this deployment
        ClassLoader existing = creator.getCurrent();
        if (existing == null) {
            current = LibraryLoader.getContextClassLoader();
        } else {
            current = existing;
        }

        return current;
    }

    /**
     * Gets class loader for existing {@link org.lightmare.deploy.MetaCreator}
     * instance
     *
     * @return {@link ClassLoader} instance
     */
    public static ClassLoader getCurrent() {

        ClassLoader current;

        MetaCreator creator = MetaContainer.getCreator();
        if (creator == null) {
            current = LibraryLoader.getContextClassLoader();
        } else {
            // Gets default, context class loader for current thread
            current = getCurrent(creator);
        }

        return current;
    }

    /**
     * Checks if loader {@link ExecutorService} is null or shut down or
     * terminated
     *
     * @return <code>boolean</code> validation result
     */
    private static boolean invalid() {
        return (LOADER_POOL == null || LOADER_POOL.isShutdown() || LOADER_POOL.isTerminated());
    }

    /**
     * Tries to lock thread for {@link ReentrantLock} and fixed time and time
     * unit
     *
     * @return <code>boolean</code> lock result
     * @throws IOException
     */
    private static boolean tryLock() throws IOException {
        return ObjectUtils.tryLock(LOCK, LOCK_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates loader {@link ExecutorService} for deployment
     *
     * @return {@link ExecutorService} instance
     */
    private static ExecutorService createLoaderPool() {

        ExecutorService pool;

        ThreadFactory factory = new LoaderThreadFactory();
        pool = Executors.newFixedThreadPool(LOADER_POOL_SIZE, factory);

        return pool;
    }

    /**
     * Initializes loader pool ({@link ExecutorService}) if it is not available
     */
    private static void checkAndInitLoaderPool() {

        if (invalid()) {
            LOADER_POOL = createLoaderPool();
        }
    }

    /**
     * Initializes loader pool
     */
    private static void initLoaderPool() {

        try {
            checkAndInitLoaderPool();
        } finally {
            ObjectUtils.unlock(LOCK);
        }
    }

    /**
     * Initializes and locks thread pool
     *
     * @return <code>boolean</code> validation result for lock
     * @throws IOException
     */
    private static boolean initAndUnlock() throws IOException {

        boolean locked = tryLock();
        if (locked) {
            initLoaderPool();
        }

        return locked;
    }

    /**
     * Checks and if not valid reopens deploy {@link ExecutorService} instance
     *
     * @return {@link ExecutorService} instance
     * @throws IOException
     */
    protected static ExecutorService getLoaderPool() throws IOException {

        if (invalid()) {
            boolean locked = Boolean.FALSE;
            while (Boolean.FALSE.equals(locked)) {
                // Locks the object to avoid shut down and submit in parallel
                locked = initAndUnlock();
            }
        }

        return LOADER_POOL;
    }

    /**
     * Submit passed {@link Runnable} implementation in loader pool instance
     *
     * @param runnable
     * @throws IOException
     */
    public static void submit(Runnable runnable) throws IOException {
        ExecutorService pool = getLoaderPool();
        pool.submit(runnable);
    }

    /**
     * Submits passed {@link Callable} implementation in loader pool instance
     *
     * @param callable
     * @return {@link Future}<code><T></code> for passed {@link Callable}
     *         instance
     * @throws IOException
     */
    public static <T> Future<T> submit(Callable<T> callable) throws IOException {

        Future<T> future;

        ExecutorService pool = getLoaderPool();
        future = pool.submit(callable);

        return future;
    }

    /**
     * Checks if pool is not null and shuts it down
     */
    private static void shutDownPool() {

        if (ObjectUtils.notNull(LOADER_POOL)) {
            LOADER_POOL.shutdown();
            LOADER_POOL = null;
        }
    }

    /**
     * Reloads pool and releases locks
     *
     * @throws IOException
     */
    private static void reloadAndUnlock() throws IOException {

        try {
            shutDownPool();
        } finally {
            ObjectUtils.unlock(LOCK);
        }
    }

    /**
     * Clears existing {@link ExecutorService}s from loader threads
     *
     * @throws IOException
     */
    public static void reload() throws IOException {

        boolean locked = Boolean.FALSE;
        while (Boolean.FALSE.equals(locked)) {
            // Locks the object to avoid shut down and submit in parallel
            locked = tryLock();
            if (locked) {
                reloadAndUnlock();
            }
        }
    }
}
