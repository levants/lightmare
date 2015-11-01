package org.lightmare.criteria.orm;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Adds reflection meta data to {@link QueryTuple} instance
 * 
 * @author Levan Tsinadze
 *
 */
public class ClassProcessor {

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
	Method method = ClassUtils.getDeclaredMethod(entityType, methodName);
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

	try {
	    String className = tuple.getEntityName();
	    Class<?> entityType = Class.forName(className);
	    tuple.setEntityType(entityType);
	    setMethodAndField(tuple);
	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	}
    }
}
