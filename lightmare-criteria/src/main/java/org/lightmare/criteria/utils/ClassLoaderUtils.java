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
package org.lightmare.criteria.utils;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Objects;

/**
 * Class for load jar or class files from specified path
 *
 * @author Levan Tsinadze
 */
public class ClassLoaderUtils {

    /**
     * Gets current {@link Thread}'s context {@link ClassLoader} object
     *
     * @return {@link ClassLoader}
     */
    public static ClassLoader getContextClassLoader() {

	ClassLoader loader;

	Thread current = Thread.currentThread();
	PrivilegedAction<ClassLoader> action = current::getContextClassLoader;
	loader = AccessController.doPrivileged(action);

	return loader;
    }

    /**
     * Loads class by name to passed {@link ClassLoader} instance
     * 
     * @param className
     * @param loader
     * @throws IOException
     */
    private static void loadClass(String className, ClassLoader loader) throws IOException {

	try {
	    loader.loadClass(className);
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

	if (CollectionUtils.valid(classes) && Objects.nonNull(loader)) {
	    for (String className : classes) {
		loadClass(className, loader);
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
}
