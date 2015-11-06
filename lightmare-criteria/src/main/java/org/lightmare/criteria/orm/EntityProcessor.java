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
import java.lang.reflect.Method;

import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ClassUtils;

/**
 * Adds reflection meta data to {@link QueryTuple} instance
 * 
 * @author Levan Tsinadze
 *
 */
public class EntityProcessor {

    /**
     * Sets {@link Field} by name to passed wrapper
     * 
     * @param tuple
     * @throws IOException
     */
    private static void setField(QueryTuple tuple) throws IOException {

	String fieldName = tuple.getFieldName();
	Class<?> entityType = tuple.getEntityType();
	Field field = ClassUtils.getDeclaredField(entityType, fieldName);
	tuple.setField(field);
    }

    /**
     * Sets {@link Method} and {@link Field} by names to passed wrapper
     * 
     * @param tuple
     * @throws IOException
     */
    private static void setMethodAndField(QueryTuple tuple) throws IOException {

	String methodName = tuple.getMethodName();
	Class<?> entityType = tuple.getEntityType();
	Method method = ClassUtils.findMethod(entityType, methodName);
	tuple.setMethod(method);
	setField(tuple);
    }

    /**
     * Sets {@link Class}, {@link Method} and {@link Field} by names to wrapper
     * 
     * @param tuple
     * @throws IOException
     */
    public static void setMetaData(QueryTuple tuple) throws IOException {

	String className = tuple.getEntityName();
	Class<?> entityType = ClassUtils.classForName(className);
	tuple.setEntityType(entityType);
	setMethodAndField(tuple);
    }
}
