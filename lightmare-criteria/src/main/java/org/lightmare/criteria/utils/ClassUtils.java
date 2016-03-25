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

/**
 * Utility class to load and manage classes ({@link Class})
 *
 * @author Levan Tsinadze
 */
public class ClassUtils extends AbstractMemberUtils {

    /**
     * Initializes {@link Class} by name
     * 
     * @param className
     * @return {@link Class} by name
     */
    private static Class<?> forName(String className) {
        return ObjectUtils.applyQuietly(className, Class::forName);
    }

    /**
     * Initializes {@link Class} by name in passed {@link ClassLoader} realm
     * 
     * @param className
     * @param initialize
     * @param loader
     * @return {@link Class} by name
     */
    private static Class<?> forName(String className, boolean initialize, ClassLoader loader) {
        return ObjectUtils.applyQuietly(className, initialize, (c, i) -> Class.forName(c, i, loader));
    }

    /**
     * Loads and if initialize parameter is <code>true</code> initializes
     * {@link Class} by name with specific {@link ClassLoader} if it is not
     * <code>null</code>
     *
     * @param className
     * @param initialize
     * @param loader
     * @return {@link Class} by name
     */
    public static Class<?> classForName(String className, boolean initialize, ClassLoader loader) {
        return ObjectUtils.ifIsNull(loader, c -> forName(className), c -> forName(className, initialize, c));
    }

    /**
     * Loads class by name with specific {@link ClassLoader} if it is not
     * <code>null</code> or empty
     *
     * @param className
     * @param loader
     * @return {@link Class} by name
     */
    public static Class<?> classForName(String className, ClassLoader loader) {
        return classForName(className, Boolean.TRUE, loader);
    }

    /**
     * Loads class by name
     *
     * @param className
     * @return {@link Class} by name
     */
    public static Class<?> classForName(String className) {
        return classForName(className, null);
    }
}
