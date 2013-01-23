package org.lightmare.libraries;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

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
			urlLoader = URLClassLoader.newInstance(urls, Thread.currentThread()
					.getContextClassLoader());
		}
		return urlLoader;
	}

	public static void loadCurrentLibraries(ClassLoader loader) {
		if (loader != null) {
			Thread.currentThread().setContextClassLoader(loader);
		}
	}

	public static void loadCurrentLibraries(Thread thread, ClassLoader loader) {
		if (loader != null) {
			thread.setContextClassLoader(loader);
		}
	}

	public static void loadEarLibraries() {

	}

	private static void loadLibraryFromPath(String libraryPath)
			throws IOException {
		File file = new File(libraryPath);
		try {
			if (file.exists()) {
				Set<URL> urls = new HashSet<URL>();
				getSubfiles(file, urls);

				URL[] paths = urls.toArray(new URL[urls.size()]);
				URLClassLoader urlLoader = (URLClassLoader) ClassLoader
						.getSystemClassLoader();

				Method method = URLClassLoader.class.getDeclaredMethod(
						"addURL", URL.class);
				boolean accessible = method.isAccessible();
				method.setAccessible(true);
				for (URL url : paths) {
					method.invoke(urlLoader, new Object[] { url });
				}
				method.setAccessible(accessible);
			}
		} catch (MalformedURLException ex) {
			throw new IOException(ex);
		} catch (NoSuchMethodException ex) {
			throw new IOException(ex);
		} catch (SecurityException ex) {
			throw new IOException(ex);
		} catch (IllegalAccessException ex) {
			throw new IOException(ex);
		} catch (IllegalArgumentException ex) {
			throw new IOException(ex);
		} catch (InvocationTargetException ex) {
			throw new IOException(ex);
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
