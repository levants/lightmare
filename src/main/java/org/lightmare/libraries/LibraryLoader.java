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
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.fs.FileUtils;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Class for load jar or class files from specified path
 * 
 * @author Levan Tsinadze
 * @since 0.0.15-SNAPSHOT
 */
public class LibraryLoader {

    // Method name to add URL to class loader
    private static final String ADD_URL_METHOD_NAME = "addURL";

    private static final String CLOSE_METHOD_NAME = "close";

    // Caches if class URLClassLoader has close method
    private static Boolean hasCloseMethod;

    // Name of class loader isolated thread
    private static final String LOADER_THREAD_NAME = "library-class-loader-thread";

    // Class extension
    private static final String CLASS_EXTENSION = ".class";

    // Inaccessible method to add URL to existing class loader
    private static Method addURLMethod;

    // Lock to synchronize class loading
    private static final Lock LOCK = new ReentrantLock();

    private static final Logger LOG = Logger.getLogger(LibraryLoader.class);

    /**
     * implementation of {@link Callable}<ClassLoader> interface to initialize
     * {@link ClassLoader} in separate thread
     * 
     * @author Levan Tsinadze
     * @since 0.0.15-SNAPSHOT
     */
    private static class LibraryLoaderInit implements Callable<ClassLoader> {

	// Classes URL array
	private URL[] urls;

	// Parent class loader
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
     * Caches specific "addURL" method for {@link URLClassLoader} class
     * 
     * @throws IOException
     */
    private static void initURLMethod() throws IOException {

	if (addURLMethod == null
		&& ClassUtils.hasMethod(URLClassLoader.class,
			ADD_URL_METHOD_NAME)) {
	    addURLMethod = ClassUtils.getDeclaredMethod(URLClassLoader.class,
		    ADD_URL_METHOD_NAME, URL.class);
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
	    ObjectUtils.lock(LOCK);
	    try {
		initURLMethod();
	    } finally {
		ObjectUtils.unlock(LOCK);
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
	    URLClassLoader urlLoader = ObjectUtils.cast(loader,
		    URLClassLoader.class);
	    urls = urlLoader.getURLs();
	} else {
	    urls = CollectionUtils.emptyArray(URL.class);
	}

	return urls;
    }

    /**
     * Gets file for current class
     * 
     * @param executing
     * @return File where current class is
     */
    public static File classFile(Class<?> executing) {

	final File file = new File(executing.getProtectionDomain()
		.getCodeSource().getLocation().getPath());

	return file;
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

	ClassLoader ejbLoader;

	ClassLoader parent = getContextClassLoader();
	LibraryLoaderInit initializer = new LibraryLoaderInit(urls, parent);
	FutureTask<ClassLoader> task = new FutureTask<ClassLoader>(initializer);
	Thread thread = new Thread(task);
	thread.setName(LOADER_THREAD_NAME);
	thread.setPriority(Thread.MAX_PRIORITY);
	thread.start();

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

	/**
	 * Implementation of PrivilegedAction to get current thread's class
	 * loader
	 * 
	 * @author Levan Tsinadze
	 * 
	 */
	PrivilegedAction<ClassLoader> action = new PrivilegedAction<ClassLoader>() {

	    @Override
	    public ClassLoader run() {

		ClassLoader classLoader;

		Thread currentThread = Thread.currentThread();
		classLoader = currentThread.getContextClassLoader();

		return classLoader;
	    }
	};

	ClassLoader loader = AccessController.doPrivileged(action);

	return loader;
    }

    /**
     * Finds class file {@link URL} by class name in current {@link ClassLoader}
     * 's resources (claspath) or returns null if class file is not present
     * 
     * @param className
     * @return {@link URL} or class file
     */
    public static URL finadClass(String className) {

	URL classURL;

	ClassLoader loader = getContextClassLoader();
	String fileName = className.replace(StringUtils.DOT,
		File.pathSeparatorChar);
	String classFile = StringUtils.concat(fileName, CLASS_EXTENSION);
	classURL = loader.getResource(classFile);

	return classURL;
    }

    /**
     * Finds is class file in current {@link ClassLoader}'s resources (in
     * classpath) or not
     * 
     * @param className
     * @return <code>boolean</code>
     */
    public static boolean isClass(String className) {

	boolean valid;

	ClassLoader loader = getContextClassLoader();
	String fileName = className
		.replace(StringUtils.DOT, File.separatorChar);
	String classFile = StringUtils.concat(fileName, CLASS_EXTENSION);
	URL classURL = loader.getResource(classFile);
	valid = ObjectUtils.notNull(classURL);

	return valid;

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

	ClassLoader enrichedLoader;

	if (CollectionUtils.valid(urls)) {
	    if (parent == null) {
		parent = getContextClassLoader();
	    }
	    enrichedLoader = EjbClassLoader.newInstance(urls, parent);
	} else {
	    enrichedLoader = null;
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

	ClassLoader enrichedLoader;

	FileUtils.getSubfiles(file, urls);
	URL[] paths = CollectionUtils.toArray(urls, URL.class);
	ClassLoader parent = getContextClassLoader();
	enrichedLoader = getEnrichedLoader(paths, parent);

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

	ClassLoader clone;

	URLClassLoader loader = (URLClassLoader) getEnrichedLoader(urls, parent);
	try {
	    // get all resources for cloning
	    URL[] urlArray = loader.getURLs();
	    URL[] urlClone = urlArray.clone();

	    if (parent == null) {
		parent = getContextClassLoader();
	    }
	    clone = EjbClassLoader.newInstance(urlClone, parent);
	} finally {
	    closeClassLoader(loader);
	    // dereference cloned class loader instance
	    loader = null;
	}

	return clone;
    }

    /**
     * Merges two {@link ClassLoader}s in one
     * 
     * @param newLoader
     * @param oldLoader
     * @return {@link ClassLoader}
     */
    public static ClassLoader createCommon(ClassLoader newLoader,
	    ClassLoader oldLoader) {

	ClassLoader commonLoader;

	URL[] urls = getURLs(oldLoader);
	commonLoader = URLClassLoader.newInstance(urls, oldLoader);
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
	    ClassUtils.invokePrivate(method, urlLoader, url);
	}
    }

    /**
     * Loads class to system {@link ClassLoader} for general use
     * 
     * @param className
     * @throws IOException
     */
    public static void loadLibraryClass(String className) throws IOException {

	ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
	try {
	    systemLoader.loadClass(className);
	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Loads all files and sub files {@link URL}s to system class loader
     * 
     * @param file
     * @throws IOException
     */
    public static void loadLibraryFromFile(File file) throws IOException {

	if (file.exists()) {
	    Set<URL> urls = new HashSet<URL>();
	    FileUtils.getSubfiles(file, urls);
	    URL[] paths = CollectionUtils.toArray(urls, URL.class);
	    ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
	    if (systemLoader instanceof URLClassLoader) {
		URLClassLoader urlLoader = (URLClassLoader) systemLoader;

		Method method = getURLMethod();
		if (ObjectUtils.notNull(method)) {
		    loadURLToSystem(paths, method, urlLoader);
		}
	    }
	}
    }

    /**
     * Loads all files and sub files {@link URL}s to system class loader
     * 
     * @param libraryPath
     * @throws IOException
     */
    private static void loadLibraryFromPath(String libraryPath)
	    throws IOException {

	File file = new File(libraryPath);
	loadLibraryFromFile(file);
    }

    /**
     * Loads jar or <code>.class</code> files to the current thread from
     * libraryPaths recursively
     * 
     * @param libraryPaths
     * @throws IOException
     */
    public static void loadLibraries(String... libraryPaths) throws IOException {

	if (CollectionUtils.valid(libraryPaths)) {
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

	if (CollectionUtils.valid(classes) && ObjectUtils.notNull(loader)) {
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
     * Checks and caches if passed {@link ClassLoader} implementation or it's
     * parent class has close method
     * 
     * @param loader
     * @throws IOException
     */
    private static void hasCloseMethod(Class<URLClassLoader> loaderClass)
	    throws IOException {

	if (hasCloseMethod == null) {
	    // Finds if loader associated class or superclass has
	    // "close"
	    // method
	    hasCloseMethod = ClassUtils.hasPublicMethod(loaderClass,
		    CLOSE_METHOD_NAME);
	}
    }

    /**
     * Checks and caches if passed {@link ClassLoader} implementation or it's
     * parent class has close method
     * 
     * @param loader
     * @throws IOException
     */
    private static void checkOnClose(Class<URLClassLoader> loaderClass)
	    throws IOException {

	if (hasCloseMethod == null) {
	    synchronized (LibraryLoader.class) {
		// Finds if loader associated class or it's superclass has
		// "close"
		// method
		hasCloseMethod(loaderClass);
	    }
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
		URLClassLoader urlClassLoader = ObjectUtils.cast(loader,
			URLClassLoader.class);
		urlClassLoader.clearAssertionStatus();
		// Finds if loader associated class or superclass has "close"
		// method
		checkOnClose(URLClassLoader.class);

		if (hasCloseMethod) {
		    urlClassLoader.close();
		}
	    } catch (Throwable th) {
		LOG.error(th.getMessage(), th);
	    }
	}
    }
}
