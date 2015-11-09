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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class to use reflection {@link Method}, {@link Constructor} or any
 * {@link AccessibleObject} calls and get / set / modify {@link Field} value
 *
 * @author Levan Tsinadze
 */
public class ClassUtils extends AbstractMemberUtils {

    /**
     * Loads and if initialize parameter is true initializes class by name with
     * specific {@link ClassLoader} if it is not <code>null</code>
     *
     * @param className
     * @param initialize
     * @param loader
     * @return {@link Class}
     * @throws IOException
     */
    public static Class<?> classForName(String className, boolean initialize, ClassLoader loader) throws IOException {

	Class<?> type;

	try {
	    if (loader == null) {
		type = Class.forName(className);
	    } else {
		type = Class.forName(className, initialize, loader);
	    }
	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	}

	return type;
    }

    /**
     * Loads class by name with specific {@link ClassLoader} if it is not
     * <code>null</code>
     *
     * @param className
     * @param loader
     * @return {@link Class}
     * @throws IOException
     */
    public static Class<?> classForName(String className, ClassLoader loader) throws IOException {
	return classForName(className, Boolean.TRUE, loader);
    }

    /**
     * Loads class by name
     *
     * @param className
     * @return {@link Class}
     * @throws IOException
     */
    public static Class<?> classForName(String className) throws IOException {
	return classForName(className, null);
    }
}
