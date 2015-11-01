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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Utility class to resolve generic type of {@link Collection} field
 * 
 * @author Levan Tsinadze
 *
 */
public class GenericProcessor {

    /**
     * Validates and sets generic type of field to tuple
     * 
     * @param parameterType
     * @param tuple
     */
    private static void validateAndSetType(Type parameterType, QueryTuple tuple) {

	if (parameterType instanceof Class<?>) {
	    Class<?> genericType = ObjectUtils.cast(parameterType);
	    tuple.setGenericType(genericType);
	}
    }

    /**
     * Sets generic type of field to tuple
     * 
     * @param field
     * @param tuple
     */
    private static void setGenericType(Field field, QueryTuple tuple) {

	Type type = field.getGenericType();
	if (type instanceof ParameterizedType) {
	    ParameterizedType parametrizedType = ObjectUtils.cast(field);
	    Type parameterType = CollectionUtils.getFirst(parametrizedType.getActualTypeArguments());
	    validateAndSetType(parameterType, tuple);
	}
    }

    /**
     * Sets generic type of collection field
     * 
     * @param tuple
     * @throws IOException
     */
    public static void setGenericType(QueryTuple tuple) throws IOException {
	Field field = tuple.getField();
	setGenericType(field, tuple);
    }
}
