/*
 * Lightmare-criteria, JPA-QL query generator using lambda expressions
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
package org.lightmare.criteria.meta;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Temporal;

import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Finds appropriated {@link javax.persistence.TemporalType} for field
 * 
 * @author Levan Tsinadze
 *
 */
public class ColumnProcessor {

    /**
     * Gets {@link javax.persistence.Temporal} annotation from
     * {@link AnnotatedElement} instance
     * 
     * @param element
     * @return {@link javax.persistence.Temporal} annotation
     */
    public static Temporal getTemporal(AnnotatedElement element) {
        return element.getAnnotation(Temporal.class);
    }

    /**
     * Finds {@link javax.persistence.Temporal} annotated field or getter method
     * 
     * @param field
     * @param method
     * @return {@link javax.persistence.Temporal} annotation
     */
    private static Temporal getTemporal(Field field, Method method) {
        return ObjectUtils.thisOrDefault(getTemporal(field), () -> getTemporal(method));
    }

    /**
     * Sets appropriated {@link javax.persistence.TemporalType} to passed tuple
     * from {@link java.lang.reflect.Field} or {@link java.lang.reflect.Method}
     * annotations
     * 
     * @param tuple
     */
    public static void setTemporalType(QueryTuple tuple) {
        Temporal temporal = getTemporal(tuple.getField(), tuple.getMethod());
        tuple.setTemporalType(temporal);
    }
}
