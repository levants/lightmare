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

import java.io.File;
import java.io.InputStream;
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

    // Class file extension
    private static final String CLASS = ".class";

    /**
     * Generates class file name from class name
     * 
     * @param name
     * @return {@link String} class file name
     */
    public static String getAsResource(String name) {
        return name.replace(StringUtils.DOT, File.separatorChar).concat(CLASS);
    }

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
     * Gets resource as {@link java.io.InputStream} by name from current
     * {@link Thread}'s context class loader
     * 
     * @param resource
     * @return {@link java.io.InputStream} from current {@link ClassLoader}
     */
    public static InputStream getResourceAsStream(String resource) {

        InputStream is;

        ClassLoader loader = getContextClassLoader();
        is = loader.getResourceAsStream(resource);

        return is;
    }

    /**
     * Gets resource as {@link java.io.InputStream} by name from {@link Class}'s
     * class loader
     * 
     * @param name
     * @param resource
     * @return {@link java.io.InputStream} from current {@link ClassLoader}
     */
    private static InputStream getClassResourceAsStream(String name, String resource) {

        InputStream is;

        Class<?> type = ClassUtils.classForName(name);
        is = type.getResourceAsStream(resource);

        return is;
    }

    /**
     * Gets class file {@link java.io.InputStream} by name from current class
     * loader
     * 
     * @param name
     * @return {@link java.io.InputStream} from current {@link ClassLoader}
     */
    public static InputStream getClassAsStream(String name) {

        InputStream is;

        String resource = getAsResource(name);
        is = ObjectUtils.getOrInit(() -> getResourceAsStream(resource), () -> getClassResourceAsStream(name, resource));

        return is;
    }

    /**
     * Loads class by name to passed {@link ClassLoader} instance
     * 
     * @param className
     * @param loader
     */
    private static void loadClass(String className, ClassLoader loader) {
        ObjectUtils.call(loader, className, ClassLoader::loadClass);
    }

    /**
     * Loads passed classes to specified {@link ClassLoader} instance
     * 
     * @param names
     * @param loader
     */
    private static void loadAll(Collection<String> names, ClassLoader loader) {
        names.forEach(name -> loadClass(name, loader));
    }

    /**
     * Loads passed classes to current {@link Thread}'s context
     * {@link ClassLoader} instance
     * 
     * @param names
     */
    private static void loadAll(Collection<String> names) {
        ClassLoader loader = getContextClassLoader();
        loadAll(names, loader);
    }

    /**
     * Loads passed classes to specified {@link ClassLoader} instance
     *
     * @param names
     * @param loader
     */
    public static void loadClasses(Collection<String> names, ClassLoader loader) {

        if (CollectionUtils.valid(names) && Objects.nonNull(loader)) {
            loadAll(names, loader);
        }
    }

    /**
     * Loads passed classes to specified current {@link Thread}'s context class
     * loader
     *
     * @param names
     */
    public static void loadClasses(Collection<String> names) {
        CollectionUtils.valid(names, ClassLoaderUtils::loadAll);
    }
}
