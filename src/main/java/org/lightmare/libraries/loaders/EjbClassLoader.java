package org.lightmare.libraries.loaders;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Implementation of {@link URLClassLoader} class for deployed ejb applications
 * 
 * @author levan
 * 
 */
public class EjbClassLoader extends URLClassLoader {

    public EjbClassLoader(final URL[] urls) {
	super(urls);
    }

    public EjbClassLoader(final URL[] urls, final ClassLoader parent) {
	super(urls, parent);
    }

    /**
     * Creates a new instance of {@link EjbClassLoader} for the specified
     * {@link URL}s and default parent class loader. If a security manager is
     * installed, the <code>loadClass</code> method of the
     * {@link EjbClassLoader} returned by this method will invoke the
     * <code>SecurityManager.checkPackageAccess</code> before loading the class.
     * 
     * @param urls
     *            the URLs to search for classes and resources
     * @return the resulting class loader
     */
    public static EjbClassLoader newInstance(final URL[] urls) {

	PrivilegedAction<EjbClassLoader> action = new PrivilegedAction<EjbClassLoader>() {

	    @Override
	    public EjbClassLoader run() {

		EjbClassLoader ejbClassLoader = new EjbClassLoader(urls);

		return ejbClassLoader;
	    }
	};
	EjbClassLoader loader = AccessController.doPrivileged(action);

	return loader;
    }

    /**
     * Creates a new instance of {@link EjbClassLoader} for the specified
     * {@link URL}s and parent {@link ClassLoader}. If a security manager is
     * installed, the <code>loadClass</code> method of the
     * {@link EjbClassLoader} returned by this method will invoke the
     * <code>SecurityManager.checkPackageAccess</code> method before loading the
     * class.
     * 
     * @param urls
     *            the URLs to search for classes and resources
     * @param parent
     *            the parent class loader for delegation
     * @return the resulting class loader
     */
    public static EjbClassLoader newInstance(final URL[] urls,
	    final ClassLoader parent) {

	PrivilegedAction<EjbClassLoader> action = new PrivilegedAction<EjbClassLoader>() {

	    @Override
	    public EjbClassLoader run() {

		EjbClassLoader ejbClassLoader = new EjbClassLoader(urls, parent);

		return ejbClassLoader;
	    }
	};
	EjbClassLoader loader = AccessController.doPrivileged(action);

	return loader;
    }
}
