package org.lightmare.utils.reflect;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.ObjectUtils;

/**
 * Class to use reflection {@link Method} calls and {@link Field} information
 * sets
 * 
 * @author levan
 * 
 */
public class MetaUtils {

    // default values for primitives
    private static byte byteDef;

    private static boolean booleanDef;

    private static char charDef;

    private static short shortDef;

    private static int intDef;

    private static long longDef;

    private static float floatDef;

    private static double doubleDef;

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
	    if (ObjectUtils.notTrue(accessible)) {
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
     * Gets all declared methods from class
     * 
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @return {@link Method}
     * @throws IOException
     */
    public static Method[] getDeclaredMethods(Class<?> clazz)
	    throws IOException {

	try {
	    return clazz.getDeclaredMethods();
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Finds if passed {@link Class} has declared public {@link Method} with
     * appropriated name
     * 
     * @param clazz
     * @param methodName
     * @return <code>boolean</code>
     * @throws IOException
     */
    private static boolean classHasPublicMethod(Class<?> clazz,
	    String methodName) throws IOException {

	Method[] methods = getDeclaredMethods(clazz);
	boolean found = Boolean.FALSE;
	int length = methods.length;
	Method method;
	for (int i = 0; i < length && ObjectUtils.notTrue(found); i++) {
	    method = methods[i];
	    found = method.getName().equals(methodName)
		    && Modifier.isPublic(method.getModifiers());
	}

	return found;
    }

    /**
     * Gets one modifier int value for passed collection
     * 
     * @param modifiers
     * @return <code>int</code>
     */
    private static int createModifier(int[] modifiers) {

	int modifier = 0;
	if (ObjectUtils.available(modifiers)) {
	    int length = modifiers.length;
	    int modifierValue;
	    for (int i = 0; i < length; i++) {
		modifierValue = modifiers[i];
		modifier = modifier | modifierValue;
	    }
	}

	return modifier;
    }

    /**
     * Finds if passed {@link Class} has declared public {@link Method} with
     * appropriated name
     * 
     * @param clazz
     * @param modifiers
     * @param methodName
     * @return <code>boolean</code>
     * @throws IOException
     */
    private static boolean classHasMethod(Class<?> clazz, String methodName,
	    int... modofiers) throws IOException {

	Method[] methods = getDeclaredMethods(clazz);
	boolean found = Boolean.FALSE;
	int length = methods.length;
	Method method;
	for (int i = 0; i < length && ObjectUtils.notTrue(found); i++) {
	    method = methods[i];
	    found = method.getName().equals(methodName)
		    && Modifier.isPublic(method.getModifiers());
	}

	return found;
    }

    /**
     * Finds if passed {@link Class} has public {@link Method} with appropriated
     * name
     * 
     * @param clazz
     * @param methodName
     * @return <code>boolean</code>
     * @throws IOException
     */
    public static boolean hasPublicMethod(Class<?> clazz, String methodName)
	    throws IOException {

	Class<?> superClass = clazz;
	boolean found = Boolean.FALSE;
	while (ObjectUtils.notNull(superClass) && ObjectUtils.notTrue(found)) {
	    found = MetaUtils.classHasPublicMethod(superClass, methodName);
	    if (ObjectUtils.notTrue(found)) {
		superClass = superClass.getSuperclass();
	    }
	}

	return found;
    }

    /**
     * Gets declared field from passed class with specified name
     * 
     * @param clazz
     * @param name
     * @return {@link Field}
     * @throws IOException
     */
    public static Field getDeclaredField(Class<?> clazz, String name)
	    throws IOException {

	try {
	    return clazz.getDeclaredField(name);
	} catch (NoSuchFieldException ex) {
	    throw new IOException(ex);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Returns passed {@link Field}'s modifier
     * 
     * @param field
     * @return <code>int</code>
     */
    public static int getModifiers(Field field) {

	return field.getModifiers();
    }

    /**
     * Returns passed {@link Method}'s modifier
     * 
     * @param method
     * @return <code>int</code>
     */
    public static int getModifiers(Method method) {

	return method.getModifiers();
    }

    /**
     * Returns type of passed {@link Field} invoking {@link Field#getType()}
     * method
     * 
     * @param field
     * @return {@link Class}<?>
     */
    public static Class<?> getType(Field field) {

	return field.getType();
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
	    if (ObjectUtils.notTrue(accessible)) {
		method.setAccessible(Boolean.TRUE);
	    }

	    return invoke(method, data, arguments);
	} finally {
	    method.setAccessible(accessible);
	}
    }

    /**
     * Common method to invoke static {@link Method}
     * 
     * @param method
     * @param arguments
     * @return
     * @throws IOException
     */
    public static Object invokeStatic(Method method, Object... arguments)
	    throws IOException {

	try {

	    return method.invoke(null, arguments);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (InvocationTargetException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Common method to invoke private static {@link Method}
     * 
     * @param method
     * @param arguments
     * @return
     * @throws IOException
     */
    public static Object invokePrivateStatic(Method method, Object... arguments)
	    throws IOException {

	boolean accessible = method.isAccessible();
	try {
	    if (ObjectUtils.notTrue(accessible)) {
		method.setAccessible(Boolean.TRUE);
	    }

	    return invokeStatic(method, arguments);
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
	    if (ObjectUtils.notTrue(accessible)) {
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
	    if (ObjectUtils.notTrue(accessible)) {
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

    /**
     * Gets {@link List} of all {@link Method}s from passed class annotated with
     * specified annotation
     * 
     * @param clazz
     * @param annotationClass
     * @return {@link List}<Method>
     * @throws IOException
     */
    public static List<Method> getAnnotatedMethods(Class<?> clazz,
	    Class<? extends Annotation> annotationClass) throws IOException {

	List<Method> methods = new ArrayList<Method>();
	Method[] allMethods = getDeclaredMethods(clazz);
	for (Method method : allMethods) {
	    if (method.isAnnotationPresent(annotationClass)) {
		methods.add(method);
	    }
	}

	return methods;
    }

    /**
     * Gets {@link List} of all {@link Field}s from passed class annotated with
     * specified annotation
     * 
     * @param clazz
     * @param annotationClass
     * @return {@link List}<Field>
     * @throws IOException
     */
    public static List<Field> getAnnotatedFields(Class<?> clazz,
	    Class<? extends Annotation> annotationClass) throws IOException {

	List<Field> fields = new ArrayList<Field>();
	Field[] allFields = clazz.getDeclaredFields();
	for (Field field : allFields) {
	    if (field.isAnnotationPresent(annotationClass)) {
		fields.add(field);
	    }
	}

	return fields;
    }

    /**
     * Returns default values if passed class is primitive else returns null
     * 
     * @param clazz
     * @return Object
     */
    public static Object getDefault(Class<?> clazz) {

	Object value;
	if (clazz.isPrimitive()) {

	    if (clazz.equals(byte.class)) {
		value = byteDef;
	    } else if (clazz.equals(boolean.class)) {
		value = booleanDef;
	    } else if (clazz.equals(char.class)) {
		value = charDef;
	    } else if (clazz.equals(short.class)) {
		value = shortDef;
	    } else if (clazz.equals(int.class)) {
		value = intDef;
	    } else if (clazz.equals(long.class)) {
		value = longDef;
	    } else if (clazz.equals(float.class)) {
		value = floatDef;
	    } else if (clazz.equals(double.class)) {
		value = doubleDef;
	    } else {
		value = null;
	    }

	} else {
	    value = null;
	}

	return value;
    }
}
