package org.lightmare.utils.reflect;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lightmare.libraries.LibraryLoader;

/**
 * Class to use reflection {@link Method} calls and {@link Field} information
 * sets
 * 
 * @author levan
 * 
 */
public class MetaUtils {

    /**
     * Makes accessible passed {@link Constructor}'s and invokes
     * {@link Constructor#newInstance(Object...)} method
     * 
     * @param constructor
     * @param parameters
     * @return <code>T</code>
     * @throws IOException
     */
    public static <T> T newInstance(Constructor<T> constructor,
	    Object... parameters) throws IOException {

	boolean accessible = constructor.isAccessible();
	try {
	    if (!accessible) {
		constructor.setAccessible(Boolean.TRUE);
	    }
	    T instance = constructor.newInstance(parameters);

	    return instance;
	} catch (InstantiationException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (InvocationTargetException ex) {
	    throw new IOException(ex);
	} finally {
	    constructor.setAccessible(accessible);
	}
    }

    /**
     * Gets declared constructor for given {@link Class} and given parameters
     * 
     * @param type
     * @param parameterTypes
     * @return {@link Constructor}
     * @throws IOException
     */
    public static <T> Constructor<T> getConstructor(Class<T> type,
	    Class<?>... parameterTypes) throws IOException {

	try {
	    Constructor<T> constructor = type
		    .getDeclaredConstructor(parameterTypes);

	    return constructor;
	} catch (NoSuchMethodException ex) {
	    throw new IOException(ex);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Instantiates class by {@link Constructor} (MetaUtils
     * {@link #newInstance(Constructor, Object...)}) after
     * {@link MetaUtils#getConstructor(Class, Class...)} method call
     * 
     * @param type
     * @param parameterTypes
     * @param parameters
     * @return <code>T</code>
     * @throws IOException
     */
    public static <T> T callConstructor(Class<T> type,
	    Class<?>[] parameterTypes, Object... parameters) throws IOException {

	Constructor<T> constructor = getConstructor(type, parameterTypes);
	T instance = newInstance(constructor, parameters);

	return instance;
    }

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
		clazz = Class.forName(className, Boolean.TRUE, loader);
	    }

	    return clazz;

	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Loads and if initialize parameter is true initializes class by name with
     * specific {@link ClassLoader} if it is not <code>null</code>
     * 
     * @param className
     * @param initialize
     * @param loader
     * @return {@link Class}
     * @throws IOException
     */
    public static Class<?> classForName(String className, boolean initialize,
	    ClassLoader loader) throws IOException {

	Class<?> clazz;
	try {
	    if (loader == null) {
		clazz = Class.forName(className);
	    } else {
		clazz = Class.forName(className, initialize, loader);
	    }

	    return clazz;

	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Loads class by name with current {@link Thread}'s {@link ClassLoader} and
     * initializes it
     * 
     * @param className
     * @param loader
     * @return {@link Class}
     * @throws IOException
     */
    public static Class<?> initClassForName(String className)
	    throws IOException {

	Class<?> clazz;
	try {
	    ClassLoader loader = LibraryLoader.getContextClassLoader();
	    clazz = Class.forName(className, Boolean.TRUE, loader);
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
	    Class<?>... parameterTypes) throws IOException {

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
		method.setAccessible(Boolean.TRUE);
	    }

	    return invoke(method, data, arguments);
	} finally {
	    method.setAccessible(accessible);
	}
    }

    /**
     * Sets value to {@link Field} sets accessible Boolean.TRUE remporary if
     * needed
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
		field.setAccessible(Boolean.TRUE);
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
		field.setAccessible(Boolean.TRUE);
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
