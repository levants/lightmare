package org.lightmare.libraries.loaders;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Distinct {@link URLClassLoader} implementations for deployed ejb applications
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
     * Constructs new instance of {@link EjbClassLoader} and loads passed
     * {@link URL} array
     * 
     * @param urls
     * @return {@link EjbClassLoader}
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
     * Constructs new instance of {@link EjbClassLoader} and loads passed
     * {@link URL} array with {@link ClassLoader} parameter as parent class
     * loader
     * 
     * @param urls
     * @param parent
     * @return {@link EjbClassLoader}
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
