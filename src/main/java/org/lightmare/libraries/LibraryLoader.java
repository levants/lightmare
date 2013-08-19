package org.lightmare.libraries;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private static final String CLOSE_METHOD_NAME = "close";

    private static final String LOADER_THREAD_NAME = "library-class-loader-thread";

    private static Method addURLMethod;

    private static final Lock LOCK = new ReentrantLock();

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

    /**
     * Gets {@link URLClassLoader} class addURL method
     * 
     * @return Method
     * @throws IOException
     */
    private static Method getURLMethod() throws IOException {

	if (addURLMethod == null) {
	    LOCK.lock();
	    try {
		if (addURLMethod == null
			&& MetaUtils.hasMethod(URLClassLoader.class,
				ADD_URL_METHOD_NAME)) {
		    addURLMethod = MetaUtils.getDeclaredMethod(
			    URLClassLoader.class, ADD_URL_METHOD_NAME,
			    URL.class);
		}
	    } finally {
		LOCK.unlock();
	    }
	}

	return addURLMethod;
    }

    /**
     * If passed {@link ClassLoader} is instance of {@link URLClassLoader} then
     * gets {@link URL}[] of this {@link ClassLoader} calling
     * {@link URLClassLoader#getURLs()} method
     * 
     * @param loader
     * @return {@link URL}[]
     */
    private static URL[] getURLs(ClassLoader loader) {

	URL[] urls;
	if (loader instanceof URLClassLoader) {
	    urls = ((URLClassLoader) loader).getURLs();
	} else {
	    urls = ObjectUtils.emptyArray(URL.class);
	}

	return urls;
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

    /**
     * Gets new {@link ClassLoader} enriched with passed {@link URL} array and
     * parent {@link ClassLoader} classes
     * 
     * @param urls
     * @param parent
     * @return {@link ClassLoader}
     * @throws IOException
     */
    public static ClassLoader getEnrichedLoader(URL[] urls, ClassLoader parent) {

	ClassLoader enrichedLoader = null;
	if (ObjectUtils.available(urls)) {
	    if (parent == null) {
		parent = getContextClassLoader();
	    }
	    enrichedLoader = EjbClassLoader.newInstance(urls, parent);
	}

	return enrichedLoader;
    }

    /**
     * Gets new {@link ClassLoader} enriched with passed {@link File} and it's
     * sub files {@link URL}s and parent {@link ClassLoader} classes
     * 
     * @param file
     * @param urls
     * @return {@link ClassLoader}
     * @throws IOException
     */
    public static ClassLoader getEnrichedLoader(File file, Set<URL> urls)
	    throws IOException {

	FileUtils.getSubfiles(file, urls);
	URL[] paths = ObjectUtils.toArray(urls, URL.class);
	ClassLoader parent = getContextClassLoader();
	ClassLoader enrichedLoader = getEnrichedLoader(paths, parent);

	return enrichedLoader;
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

    /**
     * Merges two {@link ClassLoader}s
     * 
     * @param newLoader
     * @param oldLoader
     * @return {@link ClassLoader}
     */
    public static ClassLoader createCommon(ClassLoader newLoader,
	    ClassLoader oldLoader) {

	URL[] urls = getURLs(oldLoader);
	ClassLoader commonLoader = URLClassLoader.newInstance(urls, oldLoader);
	urls = getURLs(newLoader);
	commonLoader = getEnrichedLoader(urls, newLoader);

	return commonLoader;
    }

    /**
     * Sets passed {@link Thread}'s context class loader appropriated
     * {@link ClassLoader} instance
     * 
     * @param thread
     * @param loader
     */
    public static void loadCurrentLibraries(Thread thread, ClassLoader loader) {

	if (ObjectUtils.notNull(loader)) {
	    thread.setContextClassLoader(loader);
	}
    }

    /**
     * Sets passed {@link ClassLoader} instance as current {@link Thread}'s
     * context class loader
     * 
     * @param loader
     */
    public static void loadCurrentLibraries(ClassLoader loader) {

	Thread thread = Thread.currentThread();
	loadCurrentLibraries(thread, loader);
    }

    /**
     * Adds {@link URL} array to system {@link ClassLoader} instance
     * 
     * @param urls
     * @param method
     * @param urlLoader
     * @throws IOException
     */
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
	    ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
	    if (systemLoader instanceof URLClassLoader) {
		URLClassLoader urlLoader = (URLClassLoader) systemLoader;

		Method method = getURLMethod();
		loadURLToSystem(paths, method, urlLoader);
	    }
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

	if (ObjectUtils.available(libraryPaths)) {
	    for (String libraryPath : libraryPaths) {
		loadLibraryFromPath(libraryPath);
	    }
	}
    }

    /**
     * Loads passed classes to specified {@link ClassLoader} instance
     * 
     * @param classes
     * @param loader
     */
    public static void loadClasses(Collection<String> classes,
	    ClassLoader loader) throws IOException {

	if (ObjectUtils.available(classes) && ObjectUtils.notNull(loader)) {
	    for (String className : classes) {
		try {
		    loader.loadClass(className);
		} catch (ClassNotFoundException ex) {
		    throw new IOException(ex);
		}
	    }
	}
    }

    /**
     * Loads passed classes to specified current {@link Thread}'s context class
     * loader
     * 
     * @param classes
     */
    public static void loadClasses(Collection<String> classes)
	    throws IOException {

	ClassLoader loader = getContextClassLoader();
	loadClasses(classes, loader);
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
		// Finds if loader associated class or superclass has "close"
		// method
		Class<?> loaderClass = loader.getClass();
		boolean hasMethod = MetaUtils.hasPublicMethod(loaderClass,
			CLOSE_METHOD_NAME);
		if (hasMethod) {
		    ((URLClassLoader) loader).close();
		}
	    } catch (Throwable th) {
		LOG.error(th.getMessage(), th);
	    }
	}
    }
}
