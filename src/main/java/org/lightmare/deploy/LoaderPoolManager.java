package org.lightmare.deploy;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.lightmare.cache.MetaContainer;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.ObjectUtils;

/**
 * Manager class for application deployment
 * 
 * @author levan
 * 
 */
public class LoaderPoolManager {

    private static final int LOADER_POOL_SIZE = 5;

    private static final String LOADER_THREAD_NAME = "Ejb-Loader-Thread-%s";

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
	    creatorLoader = creator.getCurrent();
	    if (ObjectUtils.notNull(creatorLoader)) {
		current = creatorLoader;
	    } else {
		current = LibraryLoader.getContextClassLoader();
	    }
	} else {
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

	    String name = String.format(LOADER_THREAD_NAME, thread.getId());
	    thread.setName(name);
	}

	@Override
	public Thread newThread(Runnable runnable) {

	    Thread thread = new Thread(runnable);
	    nameThread(thread);
	    thread.setPriority(Thread.MAX_PRIORITY);

	    ClassLoader parent = getCurrent();
	    thread.setContextClassLoader(parent);

	    return thread;
	}
    }

    // Thread pool for deploying and removal of beans and temporal resources
    private static ExecutorService LOADER_POOL = Executors.newFixedThreadPool(
	    LOADER_POOL_SIZE, new LoaderThreadFactory());

    protected static ExecutorService getLoaderPool() {

	if (LOADER_POOL == null || LOADER_POOL.isShutdown()
		|| LOADER_POOL.isTerminated()) {

	    LOADER_POOL = Executors.newFixedThreadPool(LOADER_POOL_SIZE,
		    new LoaderThreadFactory());
	}

	return LOADER_POOL;
    }

    public static void submit(Runnable runnable) {

	getLoaderPool().submit(runnable);
    }

    public static <T> Future<T> submit(Callable<T> callable) {

	Future<T> future = getLoaderPool().submit(callable);

	return future;
    }

    /**
     * Clears existing {@link ExecutorService}s from loader threads
     */
    public static void reload() {

	LOADER_POOL.shutdown();
	LOADER_POOL = null;
	getLoaderPool();
    }
}
