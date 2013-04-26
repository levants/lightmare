package org.lightmare.libraries.loaders;

import java.net.URL;
import java.net.URLClassLoader;

public class EjbClassLoader extends URLClassLoader {

    public EjbClassLoader(final URL[] urls) {
	super(urls);
    }

    public EjbClassLoader(final URL[] urls, final ClassLoader parent) {
	super(urls, parent);
    }

    public static EjbClassLoader newInstance(final URL[] urls) {
	URLClassLoader urlLoader = newInstance(urls);
	EjbClassLoader instance = new EjbClassLoader(urls, urlLoader);

	return instance;
    }

    public static EjbClassLoader newInstance(final URL[] urls,
	    ClassLoader parent) {
	URLClassLoader urlLoader = newInstance(urls, parent);
	EjbClassLoader instance = new EjbClassLoader(urls, urlLoader);

	return instance;
    }
}
