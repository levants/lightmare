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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Resolves generic type from {@link java.util.Collection} field
 * 
 * @author Levan Tsinadze
 *
 */
public class GenericProcessor {

    /**
     * Sets generic type from {@link java.lang.reflect.ParameterizedType}
     * instance
     * 
     * @param parametrizedType
     * @param tuple
     */
    private static void setParametrizedType(ParameterizedType parametrizedType, QueryTuple tuple) {

        Type[] typeArguments = parametrizedType.getActualTypeArguments();
        Type parameterType = CollectionUtils.getFirst(typeArguments);
        ObjectUtils.castIfValid(parameterType, Class.class, tuple::setGenericType);
    }

    /**
     * Sets generic type of {@link java.lang.reflect.Field} to tuple
     * 
     * @param field
     * @param tuple
     */
    private static void setGenericType(Field field, QueryTuple tuple) {
        Type type = field.getGenericType();
        ObjectUtils.castIfValid(type, ParameterizedType.class, c -> setParametrizedType(c, tuple));
    }

    /**
     * Sets generic type of collection {@link java.lang.reflect.Field} to
     * wrapper
     * 
     * @param tuple
     */
    public static void setGenericType(QueryTuple tuple) {
        ObjectUtils.nonNull(tuple.getField(), c -> setGenericType(c, tuple));
    }
}
