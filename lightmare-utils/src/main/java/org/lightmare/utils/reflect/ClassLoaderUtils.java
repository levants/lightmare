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
package org.lightmare.utils.reflect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Class for load jar or class files from specified path
 *
 * @author Levan Tsinadze
 * @since 0.0.15
 */
public class ClassLoaderUtils {

    private static final String CLOSE_METHOD_NAME = "close";

    // Caches if class URLClassLoader has close method
    private static Boolean hasCloseMethod;

    // Class extension
    private static final String CLASS_EXTENSION = ".class";

    private static final Logger LOG = Logger.getLogger(ClassLoaderUtils.class);

    /**
     * Gets file for current class
     *
     * @param executing
     * @return File where current class is
     */
    public static File classFile(Class<?> executing) {

	final File file = new File(executing.getProtectionDomain().getCodeSource().getLocation().getPath());

	return file;
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
	String fileName = className.replace(StringUtils.DOT, File.pathSeparatorChar);
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
	String fileName = className.replace(StringUtils.DOT, File.separatorChar);
	String classFile = StringUtils.concat(fileName, CLASS_EXTENSION);
	URL classURL = loader.getResource(classFile);
	valid = ObjectUtils.notNull(classURL);

	return valid;

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
    public static void loadURLToSystem(URL[] urls, Method method, URLClassLoader urlLoader) throws IOException {

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
     * Loads passed classes to specified {@link ClassLoader} instance
     *
     * @param classes
     * @param loader
     */
    public static void loadClasses(Collection<String> classes, ClassLoader loader) throws IOException {

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
    public static void loadClasses(Collection<String> classes) throws IOException {

	ClassLoader loader = getContextClassLoader();
	loadClasses(classes, loader);
    }

    /**
     * Checks and caches if passed {@link ClassLoader} implementation or it's
     * parent class has "close" method
     *
     * @param loader
     * @throws IOException
     */
    private static void hasCloseMethod(Class<URLClassLoader> loaderClass) throws IOException {

	if (hasCloseMethod == null) {
	    hasCloseMethod = ClassUtils.hasPublicMethod(loaderClass, CLOSE_METHOD_NAME);
	}
    }

    /**
     * Checks and caches if passed {@link ClassLoader} implementation or it's
     * parent class has close method
     *
     * @param loader
     * @throws IOException
     */
    private static void checkOnClose(Class<URLClassLoader> loaderClass) throws IOException {

	if (hasCloseMethod == null) {
	    synchronized (ClassLoaderUtils.class) {
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
		URLClassLoader urlClassLoader = ObjectUtils.cast(loader, URLClassLoader.class);
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
