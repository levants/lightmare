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

import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ClassUtils;
import org.lightmare.criteria.utils.CollectionUtils;

/**
 * Adds reflection data to {@link QueryTuple} instance
 * 
 * @author Levan Tsinadze
 *
 */
public class EntityProcessor {

    /**
     * Resolves argument types for method
     * 
     * @param names
     * @return {@link Class} array by names for argument types
     */
    private static Class<?>[] mapArgumentTypes(String[] names) {

        Class<?>[] argumentTypes = new Class<?>[names.length];
        CollectionUtils.map(names, argumentTypes, ClassUtils::classForName);

        return argumentTypes;
    }

    /**
     * Resolves argument types for method
     * 
     * @param tuple
     * @return {@link Class} array by names for argument types
     */
    private static Class<?>[] getArgumentTypes(QueryTuple tuple) {

        Class<?>[] argumentTypes;

        String[] names = tuple.getArguments();
        if (CollectionUtils.isEmpty(names)) {
            argumentTypes = new Class<?>[] {};
        } else {
            argumentTypes = mapArgumentTypes(names);
        }

        return argumentTypes;
    }

    /**
     * Sets {@link java.lang.reflect.Method} and {@link java.lang.reflect.Field}
     * by names to passed wrapper
     * 
     * @param tuple
     */
    private static void setMethodAndField(QueryTuple tuple) {

        Class<?> entityType = tuple.getEntityType();
        Class<?>[] argumentTypes = getArgumentTypes(tuple);
        Method method = ClassUtils.findMethod(entityType, tuple.getMethodName(), argumentTypes);
        tuple.setMethod(method);
        Field field = ClassUtils.findField(entityType, tuple.getFieldName());
        tuple.setField(field);
    }

    /**
     * Sets {@link Class}, {@link java.lang.reflect.Method} and
     * {@link java.lang.reflect.Field} by names to wrapper
     * 
     * @param tuple
     */
    public static void setMetaData(QueryTuple tuple) {

        String className = tuple.getEntityName();
        Class<?> entityType = ClassUtils.classForName(className);
        tuple.setEntityType(entityType);
        setMethodAndField(tuple);
    }
}
