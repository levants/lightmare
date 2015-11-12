package org.lightmare.deploy.deployers;

import java.util.concurrent.ThreadFactory;

import org.lightmare.deploy.LoaderPoolManager;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.StringUtils;

/**
 * Implementation of {@link ThreadFactory} interface for application loading
 *
 * @author Levan Tsinadze
 * @since 0.0.77
 */
public class LoaderThreadFactory implements ThreadFactory {

    // Name prefix of deployment threads
    private static final String LOADER_THREAD_NAME = "Ejb-Loader-Thread-";

    /**
     * Constructs and sets thread name
     *
     * @param thread
     */
    private void nameThread(Thread thread) {
        String name = StringUtils.concat(LOADER_THREAD_NAME, thread.getId());
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
        ClassLoader parent = LoaderPoolManager.getCurrent();
        LibraryLoader.loadCurrentLibraries(thread, parent);
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
