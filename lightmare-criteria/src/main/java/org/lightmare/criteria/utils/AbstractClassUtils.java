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
     * Unwraps target {@link Throwable} from passed
     * {@link java.lang.reflect.InvocationTargetException} instance
     *
     * @param ex
     * @return {@link java.io.IOException} wrapped
     */
    protected static RuntimeException unwrap(InvocationTargetException ex) {

        RuntimeException exception;

        Throwable targetException = ex.getTargetException();
        if (targetException == null) {
            exception = new RuntimeException(ex.getMessage(), ex);
        } else {
            exception = new RuntimeException(targetException.getMessage(), targetException);
        }

        return exception;
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
    private static void setAccessibleFlag(AccessibleObject accessibleObject) {

        if (notAccessible(accessibleObject)) {
            accessibleObject.setAccessible(Boolean.TRUE);
        }
    }

    /**
     * Sets object accessible flag as true if it is not
     *
     * @param accessibleObject
     */
    protected static void makeAccessible(AccessibleObject accessibleObject) {

        if (notAccessible(accessibleObject)) {
            synchronized (accessibleObject) {
                setAccessibleFlag(accessibleObject);
            }
        }
    }
}
