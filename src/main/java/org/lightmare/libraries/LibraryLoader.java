package org.lightmare.libraries;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.lightmare.libraries.loaders.EjbClassLoader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.fs.FileUtils;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Class for load jar or class files from specified path
 * 
 * @author levan
 * 
 */
public class LibraryLoader {

    private static final String ADD_URL_METHOD_NAME = "addURL";

    private static final String LOADER_THREAD_NAME = "library-class-loader-thread";

    private static Method addURLMethod;

    private static final Logger LOG = Logger.getLogger(LibraryLoader.class);

    /**
     * {@link Callable}<ClassLoader> implementation to initialize
     * {@link ClassLoader} in separate thread
     * 
     * @author levan
     * 
     */
    private static class LibraryLoaderInit implements Callable<ClassLoader> {

	private URL[] urls;

	private ClassLoader parent;

	public LibraryLoaderInit(final URL[] urls, final ClassLoader parent) {
	    this.urls = urls;
	    this.parent = parent;
	}

	@Override
	public ClassLoader call() throws Exception {

	    ClassLoader loader = cloneContextClassLoader(urls, parent);

	    return loader;
	}
    }

    private static Method getURLMethod() throws IOException {

	if (addURLMethod == null) {
	    synchronized (LibraryLoader.class) {
		addURLMethod = MetaUtils.getDeclaredMethod(
			URLClassLoader.class, ADD_URL_METHOD_NAME, URL.class);
	    }
	}

	return addURLMethod;
    }

    /**
     * Initializes and returns enriched {@link ClassLoader} in separated
     * {@link Thread} to load bean and library classes
     * 
     * @param urls
     * @return {@link ClassLoader}
     * @throws IOException
     */
    public static ClassLoader initializeLoader(final URL[] urls)
	    throws IOException {

	ClassLoader parent = getContextClassLoader();

	LibraryLoaderInit initializer = new LibraryLoaderInit(urls, parent);
	FutureTask<ClassLoader> task = new FutureTask<ClassLoader>(initializer);
	Thread thread = new Thread(task);
	thread.setName(LOADER_THREAD_NAME);
	thread.setPriority(Thread.MAX_PRIORITY);
	thread.start();

	ClassLoader ejbLoader;
	try {
	    ejbLoader = task.get();
	} catch (InterruptedException ex) {
	    throw new IOException(ex);
	} catch (ExecutionException ex) {
	    throw new IOException(ex);
	}

	return ejbLoader;
    }

    /**
     * Gets current {@link Thread}'s context {@link ClassLoader} object
     * 
     * @return {@link ClassLoader}
     */
    public static ClassLoader getContextClassLoader() {

	PrivilegedAction<ClassLoader> action = new PrivilegedAction<ClassLoader>() {

	    public ClassLoader run() {
		Thread currentThread = Thread.currentThread();
		ClassLoader classLoader = currentThread.getContextClassLoader();
		return classLoader;
	    }
	};
	ClassLoader loader = AccessController.doPrivileged(action);

	return loader;
    }

    public static ClassLoader getEnrichedLoader(File file, Set<URL> urls)
	    throws IOException {
	FileUtils.getSubfiles(file, urls);
	URL[] paths = ObjectUtils.toArray(urls, URL.class);
	ClassLoader parent = getContextClassLoader();
	URLClassLoader urlLoader = URLClassLoader.newInstance(paths, parent);
	return urlLoader;
    }

    public static ClassLoader getEnrichedLoader(URL[] urls, ClassLoader parent)
	    throws IOException {
	EjbClassLoader urlLoader = null;
	if (ObjectUtils.available(urls)) {
	    if (parent == null) {
		parent = getContextClassLoader();
	    }
	    urlLoader = EjbClassLoader.newInstance(urls, parent);
	}
	return urlLoader;
    }

    /**
     * Initializes new {@link ClassLoader} from loaded {@link URL}'s from
     * enriched {@link ClassLoader} for beans and libraries
     * 
     * @param urls
     * @return {@link ClassLoader}
     * @throws IOException
     */
    public static ClassLoader cloneContextClassLoader(final URL[] urls,
	    ClassLoader parent) throws IOException {

	URLClassLoader loader = (URLClassLoader) getEnrichedLoader(urls, parent);
	try {
	    // get all resources for cloning
	    URL[] urlArray = loader.getURLs();
	    URL[] urlClone = urlArray.clone();

	    if (parent == null) {
		parent = getContextClassLoader();
	    }
	    ClassLoader clone = EjbClassLoader.newInstance(urlClone, parent);

	    return clone;
	} finally {
	    closeClassLoader(loader);
	    // dereference cloned class loader instance
	    loader = null;
	}
    }

    public static void loadCurrentLibraries(ClassLoader loader) {
	if (ObjectUtils.notNull(loader)) {
	    Thread.currentThread().setContextClassLoader(loader);
	}
    }

    public static void loadCurrentLibraries(Thread thread, ClassLoader loader) {
	if (ObjectUtils.notNull(loader)) {
	    thread.setContextClassLoader(loader);
	}
    }

    public static void loadURLToSystem(URL[] urls, Method method,
	    URLClassLoader urlLoader) throws IOException {

	for (URL url : urls) {
	    MetaUtils.invokePrivate(method, urlLoader, url);
	}
    }

    private static void loadLibraryFromPath(String libraryPath)
	    throws IOException {
	File file = new File(libraryPath);
	if (file.exists()) {
	    Set<URL> urls = new HashSet<URL>();
	    FileUtils.getSubfiles(file, urls);

	    URL[] paths = ObjectUtils.toArray(urls, URL.class);
	    URLClassLoader urlLoader = (URLClassLoader) ClassLoader
		    .getSystemClassLoader();

	    Method method = getURLMethod();
	    loadURLToSystem(paths, method, urlLoader);
	}
    }

    /**
     * Loads jar or <code>.class</code> files to the current thread from
     * libraryPaths recursively
     * 
     * @param libraryPaths
     * @throws IOException
     */
    public static void loadLibraries(String... libraryPaths) throws IOException {

	for (String libraryPath : libraryPaths) {
	    loadLibraryFromPath(libraryPath);
	}
    }

    /**
     * Closes passed {@link ClassLoader} if it is instance of
     * {@link URLClassLoader} class
     * 
     * @param loader
     * @throws IOException
     */
    public static void closeClassLoader(ClassLoader loader) throws IOException {

	if (ObjectUtils.notNull(loader) && loader instanceof URLClassLoader) {
	    try {
		loader.clearAssertionStatus();
		((URLClassLoader) loader).close();
	    } catch (Throwable th) {
		LOG.error(th.getMessage(), th);
	    }
	}
    }
}
