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

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;

/**
 * ABstract utility class for reflection
 * 
 * @author Levan Tsinadze
 *
 */
abstract class AbstractClassUtils extends Primitives {

    /**
     * Wraps passed {@link Throwable} in {@link RuntimeException} with message
     * 
     * @param ex
     * @return {@link RuntimeException} wrapped
     */
    private static RuntimeException wrap(Throwable ex) {
        return new RuntimeException(ex.getMessage(), ex);
    }

    /**
     * Unwraps target {@link Throwable} from passed
     * {@link java.lang.reflect.InvocationTargetException} instance
     *
     * @param ex
     * @return {@link RuntimeException} wrapped
     */
    protected static RuntimeException unwrap(InvocationTargetException ex) {
        return ObjectUtils.ifNull(ex::getTargetException, c -> wrap(ex), AbstractClassUtils::wrap);
    }

    /**
     * Validates if passed {@link Class} is not annotated with passed
     * {@link Annotation} type
     * 
     * @param type
     * @param annotationType
     * @return <code> boolean</code> validation result
     */
    public static boolean notAnnotated(Class<?> type, Class<? extends Annotation> annotationType) {
        return ObjectUtils.notTrue(type.isAnnotationPresent(annotationType));
    }

    /**
     * Validates if two {@link Class}es are not the same but first is assignable
     * from second
     * 
     * @param type
     * @param from
     * @return <code> boolean</code> validation result
     */
    public static boolean isOnlyAssignable(Class<?> type, Class<?> from) {
        return (ObjectUtils.notEquals(type, from) && type.isAssignableFrom(from));
    }

    /**
     * Validates if passed {@link java.lang.reflect.AccessibleObject} instance
     * is not accessible
     *
     * @param accessibleObject
     * @return <code>boolean</code> validation result
     */
    private static boolean notAccessible(AccessibleObject accessibleObject) {
        return ObjectUtils.notTrue(accessibleObject.isAccessible());
    }

    /**
     * Sets accessible flag to {@link java.lang.reflect.AccessibleObject}
     * instance
     * 
     * @param accessibleObject
     */
    private static void setAccessible(AccessibleObject accessibleObject) {
        ObjectUtils.valid(accessibleObject, AbstractClassUtils::notAccessible, c -> c.setAccessible(Boolean.TRUE));
    }

    /**
     * Sets object accessible flag as true if it is not
     *
     * @param accessibleObject
     */
    protected static void makeAccessible(AccessibleObject accessibleObject) {

        if (notAccessible(accessibleObject)) {
            synchronized (accessibleObject) {
                setAccessible(accessibleObject);
            }
        }
    }
}
