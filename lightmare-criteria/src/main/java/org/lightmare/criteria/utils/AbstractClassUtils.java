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
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;

/**
 * ABstract utility class for reflection
 * 
 * @author Levan Tsinadze
 *
 */
abstract class AbstractClassUtils extends BytecodeUtils {

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
     * Validates if passed {@link java.lang.reflect.AnnotatedElement} is not
     * annotated with passed {@link java.lang.annotation.Annotation} type
     * 
     * @param element
     * @param annotationType
     * @return <code> boolean</code> validation result
     */
    public static boolean notAnnotated(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return ObjectUtils.notTrue(element.isAnnotationPresent(annotationType));
    }

    /**
     * Validates if passed {@link Class} is not an interface
     * 
     * @param type
     * @return @return <code> boolean</code> validation result
     */
    public static boolean notInterface(Class<?> type) {
        return ObjectUtils.notTrue(type.isInterface());
    }

    /**
     * Validates if two {@link Class} objects are not the same but first is
     * assignable from second one
     * 
     * @param type
     * @param from
     * @return <code> boolean</code> validation result
     */
    public static boolean isOnlyAssignable(Class<?> type, Class<?> from) {
        return (ObjectUtils.notEquals(type, from) && type.isAssignableFrom(from));
    }

    /**
     * Sets object accessible flag as true if it is not
     *
     * @param accessibleObject
     */
    protected static void makeAccessible(AccessibleObject accessibleObject) {
        ObjectUtils.invalid(accessibleObject, AccessibleObject::isAccessible, c -> {
            synchronized (c) {
                ObjectUtils.invalid(c, AccessibleObject::isAccessible, o -> o.setAccessible(Boolean.TRUE));
            }
        });
    }
}
