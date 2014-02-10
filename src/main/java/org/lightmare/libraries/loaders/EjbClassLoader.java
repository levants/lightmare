/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
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
package org.lightmare.libraries.loaders;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;

import org.lightmare.utils.CollectionUtils;

import sun.misc.CompoundEnumeration;
import sun.misc.Launcher;
import sun.misc.Resource;
import sun.misc.URLClassPath;

/**
 * Implementation of {@link URLClassLoader} class for deployed EJB applications
 * 
 * @author Levan Tsinadze
 * @since 0.0.45-SNAPSHOT
 * @see URLClassLoader
 */
public class EjbClassLoader extends URLClassLoader {

    private static final int RESOURCES_DEFAULT_LENGTH = 2;

    /**
     * Implementation of {@link PrivilegedAction} for initialization of
     * {@link EjbClassLoader} class
     * 
     * @author Levan Tsinadze
     * @since 0.0.45-SNAPSHOT
     */
    protected static class EjbLoaderAction implements
	    PrivilegedAction<EjbClassLoader> {

	// Classes URL array
	private URL[] urls;

	// Parent class loader
	private ClassLoader parent;

	/**
	 * Constructor with classes {@link URL} array
	 * 
	 * @param urls
	 */
	public EjbLoaderAction(URL[] urls) {
	    this.urls = urls;
	}

	/**
	 * Constructor with classes {@link URL} array and parent
	 * {@link ClassLoader} instance
	 * 
	 * @param urls
	 * @param parent
	 */
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

    /**
     * Constructor with classes {@link URL} array
     * 
     * @param urls
     */
    public EjbClassLoader(final URL[] urls) {
	super(urls);
    }

    /**
     * Constructor with classes {@link URL} array and parent {@link ClassLoader}
     * instance
     * 
     * @param urls
     * @param parent
     */
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

    /**
     * Replica of parent {@link URLClassLoader} and {@link ClassLoader} class
     * method for other method
     * 
     * @return {@link URLClassPath}
     */
    static URLClassPath getBootstrapClassPath() {
	return Launcher.getBootstrapClassPath();
    }

    /**
     * Replica of parent {@link URLClassLoader} and {@link ClassLoader} class
     * method for other method
     * 
     * @return {@link URL}
     */
    @SuppressWarnings("rawtypes")
    private static Enumeration getBootstrapResources(String name)
	    throws IOException {

	final Enumeration enumeration = getBootstrapClassPath().getResources(
		name);
	return new Enumeration() {
	    public Object nextElement() {
		return ((Resource) enumeration.nextElement()).getURL();
	    }

	    public boolean hasMoreElements() {
		return enumeration.hasMoreElements();
	    }
	};
    }

    /**
     * Gets resource only from current {@link EjbClassLoader} not from parent
     * class loader
     * 
     * @param name
     * @return {@link URL}
     */
    @SuppressWarnings("rawtypes")
    public Enumeration<URL> getOnlyResources(String name) throws IOException {

	Enumeration[] tmp = new Enumeration[RESOURCES_DEFAULT_LENGTH];
	tmp[CollectionUtils.FIRST_INDEX] = getBootstrapResources(name);
	tmp[CollectionUtils.SECOND_INDEX] = findResources(name);

	return new CompoundEnumeration<URL>(tmp);
    }
}
