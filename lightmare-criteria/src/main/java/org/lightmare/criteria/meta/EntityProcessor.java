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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.lightmare.criteria.tuples.Couple;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ClassUtils;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Adds reflection data to {@link org.lightmare.criteria.tuples.QueryTuple}
 * instance
 * 
 * @author Levan Tsinadze
 *
 */
public class EntityProcessor extends AbstractEntityProcessor {

    /**
     * Gets {@link org.lightmare.criteria.tuples.Couple} of
     * {@link java.lang.reflect.Method} and {@link java.lang.reflect.Field} for
     * entity type
     * 
     * @return {@link org.lightmare.criteria.tuples.Couple} of
     *         {@link java.lang.reflect.Method} and
     *         {@link java.lang.reflect.Field}
     */
    private static Couple<Method, Field> getEntityMembers(QueryTuple tuple) {

        Couple<Method, Field> couple;

        Method method = ClassUtils.findMethod(tuple.getEntityType(), tuple.getMethodName());
        Field field = ClassUtils.findField(tuple.getEntityType(), tuple.getFieldName());
        couple = Couple.of(method, field);

        return couple;
    }

    /**
     * Sets {@link java.lang.reflect.Method} and {@link java.lang.reflect.Field}
     * by names to passed wrapper
     * 
     * @param tuple
     */
    private static void setMethodAndField(QueryTuple tuple) {

        Couple<Method, Field> couple = getEntityMembers(tuple);
        ObjectUtils.nonNull(couple.getFirst(), c -> setProperField(c, tuple));
        ObjectUtils.nonNull(couple.getSecond(), tuple::setField);
    }

    /**
     * Sets {@link java.lang.reflect.Method} and {@link java.lang.reflect.Field}
     * by names, sets appropriated {@link javax.persistence.TemporalType} from
     * {@link java.lang.reflect.Field} or {@link java.lang.reflect.Method}
     * annotations and sets generic type of collection
     * {@link java.lang.reflect.Field} to passed
     * {@link org.lightmare.criteria.tuples.QueryTuple} instance
     * 
     * @param tuple
     */
    public static void setGenericData(QueryTuple tuple) {

        setMethodAndField(tuple);
        setTemporalType(tuple);
        setGenericType(tuple);
    }

    /**
     * Sets {@link Class}, {@link java.lang.reflect.Method} and
     * {@link java.lang.reflect.Field} by names to wrapper
     * 
     * @param tuple
     */
    public static void setEntityType(QueryTuple tuple) {

        String className = tuple.getEntityName();
        Class<?> entityType = ClassUtils.classForName(className);
        tuple.setEntityType(entityType);
    }
}
