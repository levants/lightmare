package org.lightmare.utils.reflect;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class to use reflection {@link Method} calls and {@link Field} information
 * sets
 * 
 * @author levan
 * 
 */
public class MetaUtils {

    /**
     * Loads class by name
     * 
     * @param className
     * @return {@link Class}
     * @throws IOException
     */
    public static Class<?> classForName(String className) throws IOException {

	Class<?> clazz = classForName(className, null);

	return clazz;
    }

    /**
     * Loads class by name with specific {@link ClassLoader} if it is not
     * <code>null</code>
     * 
     * @param className
     * @param loader
     * @return {@link Class}
     * @throws IOException
     */
    public static Class<?> classForName(String className, ClassLoader loader)
	    throws IOException {

	Class<?> clazz;
	try {
	    if (loader == null) {
		clazz = Class.forName(className);
	    } else {
		clazz = Class.forName(className, true, loader);
	    }

	    return clazz;

	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Creates {@link Class} instance by {@link Class#newInstance()} method call
     * 
     * @param clazz
     * @return
     */
    public static <T> T instantiate(Class<T> clazz) throws IOException {

	try {
	    return clazz.newInstance();
	} catch (InstantiationException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Gets declared method from class
     * 
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @return {@link Method}
     * @throws IOException
     */
    public static Method getDeclaredMethod(Class<?> clazz, String methodName,
	    Class<?> parameterTypes) throws IOException {

	try {
	    return clazz.getDeclaredMethod(methodName, parameterTypes);
	} catch (NoSuchMethodException ex) {
	    throw new IOException(ex);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Common method to invoke {@link Method}
     * 
     * @param method
     * @param data
     * @param arguments
     * @return {@link Object}
     * @throws IOException
     */
    public static Object invoke(Method method, Object data, Object... arguments)
	    throws IOException {

	try {
	    return method.invoke(data, arguments);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (InvocationTargetException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Common method to invoke {@link Method}
     * 
     * @param method
     * @param data
     * @param arguments
     * @return {@link Object}
     * @throws IOException
     */
    public static Object invokePrivate(Method method, Object data,
	    Object... arguments) throws IOException {

	boolean accessible = method.isAccessible();
	try {
	    if (!accessible) {
		method.setAccessible(true);
	    }

	    return invoke(method, data, arguments);
	} finally {
	    method.setAccessible(accessible);
	}
    }

    /**
     * Sets value to {@link Field} sets accessible true remporary if needed
     * 
     * @param field
     * @param value
     * @throws IOException
     */
    public static void setFieldValue(Field field, Object data, Object value)
	    throws IOException {
	boolean accessible = field.isAccessible();

	try {
	    if (!accessible) {
		field.setAccessible(true);
	    }
	    field.set(data, value);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} finally {
	    field.setAccessible(accessible);
	}
    }

    /**
     * Gets value of specific field in specific {@link Object}
     * 
     * @param field
     * @param data
     * @return {@link Object}
     * @throws IOException
     */
    public static Object getFieldValue(Field field, Object data)
	    throws IOException {

	Object value;
	boolean accessible = field.isAccessible();
	try {
	    if (!accessible) {
		field.setAccessible(true);
	    }
	    value = field.get(data);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	}

	field.setAccessible(accessible);

	return value;
    }

    /**
     * Gets value of specific static field
     * 
     * @param field
     * @return {@link Object}
     * @throws IOException
     */
    public static Object getFieldValue(Field field) throws IOException {

	Object value = getFieldValue(field, null);

	return value;
    }
}
