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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract class for class members
 * 
 * @author Levan Tsinadze
 *
 */
abstract class AbstractMemberUtils extends AbstractClassUtils {

    /**
     * Validates for next iteration class member search methods
     * 
     * @param type
     * @param member
     * @return <code>boolean</code> validation result
     */
    private static boolean validate(Class<?> type, Member member) {
	return (Objects.nonNull(type) && (member == null));
    }

    /**
     * Finds passed {@link Class}'s or one of th's superclass {@link Method}
     * with appropriated name and parameters
     * 
     * @param type
     * @param methodName
     * @param parameters
     * @return {@link Method} for type
     * @throws IOException
     */
    public static Method findMethod(Class<?> type, String methodName, Class<?>... parameters) throws IOException {

	Method method = null;

	Class<?> superClass = type;
	while (validate(superClass, method)) {
	    try {
		method = superClass.getDeclaredMethod(methodName, parameters);
	    } catch (NoSuchMethodException ex) {
		superClass = superClass.getSuperclass();
	    } catch (SecurityException ex) {
		throw new IOException(ex);
	    }
	}

	return method;
    }

    /**
     * Finds passed {@link Class}'s or one of th's superclass {@link Field} with
     * appropriated name
     * 
     * @param type
     * @param fieldName
     * @return {@link Field} for type
     * @throws IOException
     */
    public static Field findField(Class<?> type, String fieldName) throws IOException {

	Field field = null;

	Class<?> superClass = type;
	while (validate(superClass, field)) {
	    try {
		field = superClass.getDeclaredField(fieldName);
	    } catch (NoSuchFieldException ex) {
		superClass = superClass.getSuperclass();
	    } catch (SecurityException ex) {
		throw new IOException(ex);
	    }
	}

	return field;
    }

    /**
     * Common method to invoke {@link Method} with reflection
     *
     * @param method
     * @param data
     * @param arguments
     * @return {@link Object}
     * @throws IOException
     */
    public static Object invoke(Method method, Object data, Object... arguments) throws IOException {

	Object value;

	try {
	    value = method.invoke(data, arguments);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (InvocationTargetException ex) {
	    throw unwrap(ex);
	}

	return value;
    }

    /**
     * Common method to invoke {@link Method} with reflection
     *
     * @param method
     * @param data
     * @param arguments
     * @return {@link Object}
     * @throws IOException
     */
    public static Object invokePrivate(Method method, Object data, Object... arguments) throws IOException {

	Object value;

	makeAccessible(method);
	value = invoke(method, data, arguments);

	return value;
    }

    /**
     * Gets all declared methods from class
     *
     * @param type
     * @return array of {@link Method}s
     * @throws IOException
     */
    public static Method[] getDeclaredMethods(Class<?> type) throws IOException {

	Method[] methods;

	try {
	    methods = type.getDeclaredMethods();
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}

	return methods;
    }

    /**
     * Gets {@link List} of all {@link Method}s from passed class annotated with
     * specified annotation
     *
     * @param type
     * @param annotationType
     * @return {@link List}<Method>
     * @throws IOException
     */
    public static List<Method> getAnnotatedMethods(Class<?> type, Class<? extends Annotation> annotationType)
	    throws IOException {

	List<Method> methods = new ArrayList<Method>();

	Method[] allMethods = getDeclaredMethods(type);
	methods = filterByAnnotation(allMethods, annotationType);

	return methods;
    }

    /**
     * Gets {@link List} of all {@link Field}s from passed class annotated with
     * specified annotation
     *
     * @param type
     * @param annotationType
     * @return {@link List}<Field>
     * @throws IOException
     */
    public static List<Field> getAnnotatedFields(Class<?> type, Class<? extends Annotation> annotationType)
	    throws IOException {

	List<Field> fields = new ArrayList<Field>();

	Field[] allFields = type.getDeclaredFields();
	fields = filterByAnnotation(allFields, annotationType);

	return fields;
    }
}
