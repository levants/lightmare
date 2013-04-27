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

    private static Method addURLMethod;

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

    /**
     * Closes passed {@link ClassLoader} if it is instance of
     * {@link URLClassLoader} class
     * 
     * @param loader
     * @throws IOException
     */
    public static void clearClassLoader(ClassLoader loader) throws IOException {

	if (loader instanceof URLClassLoader) {
	    ((URLClassLoader) loader).close();
	}
    }

    public static ClassLoader getEnrichedLoader(URL[] urls) {
	EjbClassLoader urlLoader = null;
	if (ObjectUtils.available(urls)) {
	    ClassLoader parent = getContextClassLoader();
	    urlLoader = EjbClassLoader.newInstance(urls, parent);
	}
	return urlLoader;
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
}
