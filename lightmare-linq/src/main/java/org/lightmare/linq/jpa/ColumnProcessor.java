package org.lightmare.linq.jpa;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import javax.persistence.Temporal;

import org.lightmare.linq.tuples.QueryTuple;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Finds appropriated {@link javax.persistence.TemporalType} for field
 * 
 * @author Levan Tsinadze
 *
 */
public class ColumnProcessor {

    private static Temporal getTemporal(Class<?> entityType, String fieldName, String methodName) throws IOException {

	Temporal temporal;

	Field field = ClassUtils.getDeclaredField(entityType, fieldName);
	temporal = field.getAnnotation(Temporal.class);
	if (temporal == null) {
	    Method method = ClassUtils.getDeclaredMethod(entityType, methodName);
	    temporal = method.getAnnotation(Temporal.class);
	}

	return temporal;
    }

    public static void setTemporalType(QueryTuple tuple) throws IOException {

	try {
	    String className = tuple.getEntity();
	    Class<?> entityType = Class.forName(className);
	    String fieldName = tuple.getField();
	    String methodName = tuple.getMethod();
	    Temporal temporal = getTemporal(entityType, fieldName, methodName);
	    if (Objects.nonNull(temporal)) {
		tuple.setTemporalType(temporal.value());
	    }
	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	}

    }
}
