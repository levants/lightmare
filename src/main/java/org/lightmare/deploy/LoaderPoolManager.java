package org.lightmare.deploy;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lightmare.cache.MetaContainer;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

/**
 * Manager class for application deployment
 * 
 * @author levan
 * 
 */
public class LoaderPoolManager {

    // Amount of deployment thread pool
    private static final int LOADER_POOL_SIZE = 5;

    // Name prefix of deployment threads
    private static final String LOADER_THREAD_NAME = "Ejb-Loader-Thread-";

    // Lock for pool reopening
    private static final Lock LOCK = new ReentrantLock();

    /**
     * Tries to lock {@link Lock} object or waits while {@link Lock} will be
     * available
     * 
     * @return <code>boolean</code>
     */
    private static boolean tryLock() {

	boolean locked = LOCK.tryLock();

	while (ObjectUtils.notTrue(locked)) {
	    locked = LOCK.tryLock();
	}

	return locked;
    }

    /**
     * Locks the {@link Lock} object
     */
    private static void lock() {

	LOCK.lock();
    }

    /**
     * Releases {@link Lock} object
     */
    private static void unlock() {

	LOCK.unlock();
    }

    /**
     * Gets class loader for existing {@link org.lightmare.deploy.MetaCreator}
     * instance
     * 
     * @return {@link ClassLoader}
     */
    protected static ClassLoader getCurrent() {

	ClassLoader current;

	MetaCreator creator = MetaContainer.getCreator();
	ClassLoader creatorLoader;
	if (ObjectUtils.notNull(creator)) {
	    // Gets class loader for this deployment
	    creatorLoader = creator.getCurrent();
	    if (ObjectUtils.notNull(creatorLoader)) {
		current = creatorLoader;
	    } else {
		// Gets default, context class loader for current thread
		current = LibraryLoader.getContextClassLoader();
	    }
	} else {
	    // Gets default, context class loader for current thread
	    current = LibraryLoader.getContextClassLoader();
	}

	return current;
    }

    /**
     * Implementation of {@link ThreadFactory} interface for application loading
     * 
     * @author levan
     * 
     */
    private static final class LoaderThreadFactory implements ThreadFactory {

	/**
	 * Constructs and sets thread name
	 * 
	 * @param thread
	 */
	private void nameThread(Thread thread) {

	    String name = StringUtils
		    .concat(LOADER_THREAD_NAME, thread.getId());
	    thread.setName(name);
	}

	/**
	 * Sets priority of {@link Thread} instance
	 * 
	 * @param thread
	 */
	private void setPriority(Thread thread) {

	    thread.setPriority(Thread.MAX_PRIORITY);
	}

	/**
	 * Sets {@link ClassLoader} to passed {@link Thread} instance
	 * 
	 * @param thread
	 */
	private void setContextClassLoader(Thread thread) {

	    ClassLoader parent = getCurrent();
	    thread.setContextClassLoader(parent);
	}

	/**
	 * Configures (sets name, priority and {@link ClassLoader}) passed
	 * {@link Thread} instance
	 * 
	 * @param thread
	 */
	private void configureThread(Thread thread) {

	    nameThread(thread);
	    setPriority(thread);
	    setContextClassLoader(thread);
	}

	@Override
	public Thread newThread(Runnable runnable) {

	    Thread thread = new Thread(runnable);
	    configureThread(thread);

	    return thread;
	}
    }

    // Thread pool for deploying and removal of beans and temporal resources
    private static ExecutorService LOADER_POOL;

    /**
     * Checks if loader {@link ExecutorService} is null or is shut down or is
     * terminated
     * 
     * @return <code>boolean</code>
     */
    private static boolean invalid() {

	return LOADER_POOL == null || LOADER_POOL.isShutdown()
		|| LOADER_POOL.isTerminated();
    }

    private static void initLoaderPool() {

	if (invalid()) {
	    LOADER_POOL = Executors.newFixedThreadPool(LOADER_POOL_SIZE,
		    new LoaderThreadFactory());
	}
    }

    /**
     * Checks and if not valid reopens deploy {@link ExecutorService} instance
     * 
     * @return {@link ExecutorService}
     */
    protected static ExecutorService getLoaderPool() {

	if (invalid()) {

	    lock();

	    try {
		initLoaderPool();
	    } finally {
		unlock();
	    }
	}

	return LOADER_POOL;
    }

    /**
     * Submit passed {@link Runnable} implementation in loader pool
     * 
     * @param runnable
     */
    public static void submit(Runnable runnable) {

	ExecutorService pool = getLoaderPool();
	pool.submit(runnable);
    }

    /**
     * Submits passed {@link Callable} implementation in loader pool
     * 
     * @param callable
     * @return {@link Future}<code><T></code>
     */
    public static <T> Future<T> submit(Callable<T> callable) {

	ExecutorService pool = getLoaderPool();
	Future<T> future = pool.submit(callable);

	return future;
    }

    /**
     * Clears existing {@link ExecutorService}s from loader threads
     */
    public static void reload() {

	boolean locked = tryLock();
	if (locked) {
	    try {
		if (ObjectUtils.notNull(LOADER_POOL)) {
		    LOADER_POOL.shutdown();
		    LOADER_POOL = null;
		}
	    } finally {
		unlock();
	    }
	}
    }
}
