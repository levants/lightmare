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
import java.util.NoSuchElementException;

import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

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

    private static final String VM_VENDOR_PROPERY = "java.vm.vendor";

    private static final String JAVA_VENDOR_PROPERY = "java.vendor";

    private static final String SUN_MICROSYSTEMS_PREFIX = "Sun Microsystems";

    private static final String ORACLE_CORP_PREFIX = "Oracle Corporation";

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
     * Implementation of {@link Enumeration} for class loader resources
     * 
     * @author Levan Tsinadze
     * 
     * @param <E>
     */
    protected static class MergeEnumeration<E> implements Enumeration<E> {

	private Enumeration<E>[] enums;

	private int index = CollectionUtils.FIRST_INDEX;

	public MergeEnumeration(Enumeration<E>[] enums) {
	    this.enums = enums;
	}

	private boolean next() {

	    boolean validToBreack = Boolean.FALSE;

	    Enumeration<E> current;
	    int length = this.enums.length;
	    while (this.index < length && ObjectUtils.notTrue(validToBreack)) {
		current = this.enums[this.index];
		validToBreack = (ObjectUtils.notNull(current) && (current
			.hasMoreElements()));
		if (ObjectUtils.notTrue(validToBreack)) {
		    this.index++;
		}
	    }

	    return validToBreack;
	}

	public boolean hasMoreElements() {
	    return next();
	}

	private boolean hasNotElements() {
	    return ObjectUtils.notTrue((next()));
	}

	public E nextElement() {

	    if (hasNotElements()) {
		throw new NoSuchElementException();
	    }

	    return this.enums[this.index].nextElement();
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
     * Checks if passed {@link String} names of platform vendor contains
     * appropriated vendor's name for extension of {@link ClassLoader} and / or
     * {@link URLClassLoader} resource search
     * 
     * @param platform
     * @return <code>boolean</code>
     */
    private static boolean checkVendor(String... platforms) {

	boolean valid = CollectionUtils.valid(platforms);

	if (valid) {
	    int length = platforms.length;
	    String platform;
	    boolean check = Boolean.FALSE;
	    for (int i = CollectionUtils.FIRST_INDEX; ObjectUtils
		    .notTrue(check) && i < length; i++) {
		platform = platforms[i];
		check = StringUtils.valid(platform)
			&& (platform.contains(ORACLE_CORP_PREFIX) || platform
				.contains(SUN_MICROSYSTEMS_PREFIX));
	    }
	    valid = check;
	}

	return valid;
    }

    /**
     * Checks if platform is appropriated for extension of {@link ClassLoader}
     * and / or {@link URLClassLoader} resource search
     * 
     * @return <code>boolean</code>
     */
    private static boolean checkPlatform() {

	boolean valid;

	String javaPlatform = System.getProperty(JAVA_VENDOR_PROPERY);
	String vmPlatform = System.getProperty(VM_VENDOR_PROPERY);
	valid = checkVendor(javaPlatform, vmPlatform);

	return valid;
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
    private static Enumeration<URL> getBootstrapResources(String name)
	    throws IOException {

	final Enumeration<Resource> enumeration = getBootstrapClassPath()
		.getResources(name);
	return new Enumeration<URL>() {
	    public URL nextElement() {
		return (enumeration.nextElement()).getURL();
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
    public Enumeration<URL> getOnlyResources(String name) throws IOException {

	Enumeration<URL> resources;

	if (checkPlatform()) {
	    Enumeration<URL>[] tmps = ObjectUtils
		    .cast(new Enumeration[RESOURCES_DEFAULT_LENGTH]);
	    tmps[CollectionUtils.FIRST_INDEX] = getBootstrapResources(name);
	    tmps[CollectionUtils.SECOND_INDEX] = super.findResources(name);
	    resources = new MergeEnumeration<URL>(tmps);
	} else {
	    resources = super.getResources(name);
	}

	return resources;
    }
}
