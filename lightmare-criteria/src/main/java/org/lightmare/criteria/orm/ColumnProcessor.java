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
package org.lightmare.criteria.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import javax.persistence.Temporal;

import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Finds appropriated {@link javax.persistence.TemporalType} for field
 * 
 * @author Levan Tsinadze
 *
 */
public class ColumnProcessor {

    /**
     * Finds {@link javax.persistence.Temporal} annotated field or getter method
     * 
     * @param entityType
     * @param fieldName
     * @param methodName
     * @return {@link javax.persistence.Temporal} annotation
     */
    private static Temporal getTemporal(Field field, Method method) {

        Temporal temporal;

        temporal = field.getAnnotation(Temporal.class);
        if (temporal == null) {
            temporal = method.getAnnotation(Temporal.class);
        }

        return temporal;
    }

    /**
     * Sets {@link Temporal#value()} to passed {@link QueryTuple} if
     * {@link Temporal} is not null
     * 
     * @param temporal
     * @param tuple
     */
    private static void setTemporalType(Temporal temporal, QueryTuple tuple) {

        if (Objects.nonNull(temporal)) {
            tuple.setTemporalType(temporal.value());
        }
    }

    /**
     * Sets appropriated {@link javax.persistence.TemporalType} to passed tuple
     * from field or method annotation
     * 
     * @param tuple
     */
    public static void setTemporalType(QueryTuple tuple) {

        Field field = tuple.getField();
        Method method = tuple.getMethod();
        Temporal temporal = getTemporal(field, method);
        setTemporalType(temporal, tuple);
    }
}
