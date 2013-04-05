package org.lightmare.libraries;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Class for load jar or class files from specified path
 * 
 * @author levan
 * 
 */
public class LibraryLoader {

    /**
     * Gets all jar or class subfiles from specified {@link File} recursively
     * 
     * @param file
     * @param urls
     * @throws IOException
     */
    public static void getSubfiles(File file, Set<URL> urls) throws IOException {
	if (file.isDirectory()) {
	    File[] subFiles = file.listFiles(new FilenameFilter() {

		@Override
		public boolean accept(File file, String name) {
		    return name.endsWith(".jar") || name.endsWith(".class")
			    || file.isDirectory();
		}
	    });
	    if (subFiles.length == 0) {
		return;
	    }
	    for (File subFile : subFiles) {
		if (subFile.isDirectory()) {
		    getSubfiles(subFile, urls);
		} else {
		    try {
			urls.add(subFile.toURI().toURL());
		    } catch (MalformedURLException ex) {
			throw new IOException(ex);
		    }
		}
	    }
	} else {
	    try {
		urls.add(file.toURI().toURL());
	    } catch (MalformedURLException ex) {
		throw new IOException(ex);
	    }
	}
    }

    public static ClassLoader getEnrichedLoader(File file, Set<URL> urls)
	    throws IOException {
	getSubfiles(file, urls);
	URL[] paths = urls.toArray(new URL[urls.size()]);
	URLClassLoader urlLoader = URLClassLoader.newInstance(paths, Thread
		.currentThread().getContextClassLoader());
	return urlLoader;
    }

    public static ClassLoader getEnrichedLoader(URL[] urls) {
	URLClassLoader urlLoader = null;
	if (urls.length > 0) {
	    urlLoader = URLClassLoader.newInstance(urls,
		    MetaUtils.getContextClassLoader());
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

    public static void loadEarLibraries() {

    }

    private static void loadLibraryFromPath(String libraryPath)
	    throws IOException {
	File file = new File(libraryPath);
	if (file.exists()) {
	    Set<URL> urls = new HashSet<URL>();
	    getSubfiles(file, urls);

	    URL[] paths = urls.toArray(new URL[urls.size()]);
	    URLClassLoader urlLoader = (URLClassLoader) ClassLoader
		    .getSystemClassLoader();

	    Method method = MetaUtils.getDeclaredMethod(URLClassLoader.class,
		    "addURL", URL.class);
	    for (URL url : paths) {
		MetaUtils
			.invokePrivate(method, urlLoader, new Object[] { url });
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

	for (String libraryPath : libraryPaths) {
	    loadLibraryFromPath(libraryPath);
	}
    }
}
