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
import java.io.IOException;
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
     * Gets resource as {@link InputStream} by name from current {@link Thread}
     * 's context class loader
     * 
     * @param resource
     * @return {@link InputStream} from current {@link ClassLoader}
     */
    public static InputStream getResourceAsStream(String resource) {

        InputStream is;

        ClassLoader loader = getContextClassLoader();
        is = loader.getResourceAsStream(resource);

        return is;
    }

    /**
     * Gets resource as {@link InputStream} by name from {@link Class}'s class
     * loader
     * 
     * @param name
     * @param resource
     * @return {@link InputStream} from current {@link ClassLoader}
     */
    private static InputStream getClassResourceAsStream(String name, String resource) {

        InputStream is;

        try {
            Class<?> type = ClassUtils.classForName(name);
            is = type.getResourceAsStream(resource);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return is;
    }

    /**
     * Gets class file {@link InputStream} by name from current class loader
     * 
     * @param name
     * @return {@link InputStream} from current {@link ClassLoader}
     * @throws IOException
     */
    public static InputStream getClassAsStream(String name) throws IOException {

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
     * @throws IOException
     */
    private static void loadAll(Collection<String> classes, ClassLoader loader) throws IOException {

        for (String className : classes) {
            loadClass(className, loader);
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
            loadAll(classes, loader);
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
