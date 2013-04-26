package org.lightmare.libraries.loaders;

import java.net.URL;
import java.net.URLClassLoader;

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
}
