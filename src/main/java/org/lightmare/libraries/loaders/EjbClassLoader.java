package org.lightmare.libraries.loaders;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Implementation of {@link URLClassLoader} class for deployed EJB applications
 * 
 * @author levan
 * @since 0.0.45-SNAPSHOT
 */
public class EjbClassLoader extends URLClassLoader {

    /**
     * Implementation of {@link PrivilegedAction} for initialization of
     * {@link EjbClassLoader} class
     * 
     * @author levan
     * @since 0.0.45-SNAPSHOT
     */
    protected static class EjbLoaderAction implements
	    PrivilegedAction<EjbClassLoader> {

	// Classes URL array
	private URL[] urls;

	//Parent class loader
	private ClassLoader parent;

	public EjbLoaderAction(URL[] urls) {
	    this.urls = urls;
	}

	public EjbLoaderAction(URL[] urls, ClassLoader parent) {
	    this(urls);
	    this.parent = parent;
	}

	@Override
	public EjbClassLoader run() {

	    EjbClassLoader ejbClassLoader;

	    if (parent == null) {
		ejbClassLoader = new EjbClassLoader(urls);
	    } else {
		ejbClassLoader = new EjbClassLoader(urls, parent);
	    }

	    return ejbClassLoader;
	}
    }

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

	EjbClassLoader loader;

	PrivilegedAction<EjbClassLoader> action = new EjbLoaderAction(urls);
	loader = AccessController.doPrivileged(action);

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

	EjbClassLoader loader;

	PrivilegedAction<EjbClassLoader> action = new EjbLoaderAction(urls,
		parent);
	loader = AccessController.doPrivileged(action);

	return loader;
    }
}
