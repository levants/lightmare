package org.lightmare.utils.reflect;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private static final byte BYTE_DEF = 0;

    private static final boolean BOOLEAN_DEF = Boolean.FALSE;

    private static final char CHAR_DEF = '\u0000';

    private static final short SHORT_DEF = 0;

    private static final int INT_DEF = 0;

    private static final long LONG_DEF = 0L;

    private static final float FLOAT_DEF = 0F;

    private static final double DOUBLE_DEF = 0D;

    // default value for modifier
    private static final int DEFAULT_MODIFIER = 0;

    // Lock to modify accessible mode of AccessibleObject instances
    private static final Lock ACCESSOR_LOCK = new ReentrantLock();

    /**
     * Checks if passed {@link AccessibleObject} instance is not accessible
     * 
     * @param accessibleObject
     * @return <code>boolean</code>
     */
    private static boolean notAccessible(AccessibleObject accessibleObject) {

	return ObjectUtils.notTrue(accessibleObject.isAccessible());
    }

    /**
     * Modifies passed {@link AccessibleObject} as accessible
     * 
     * @param accessibleObject
     * @return <code>boolean</code>
     */
    private static boolean makeAccessible(AccessibleObject accessibleObject) {

	boolean locked = ObjectUtils.tryLock(ACCESSOR_LOCK);
	if (locked) {
	    try {
		if (notAccessible(accessibleObject)) {
		    accessibleObject.setAccessible(Boolean.TRUE);
		}
	    } finally {
		ObjectUtils.unlock(ACCESSOR_LOCK);
	    }
	}

	return locked;
    }

    /**
     * Sets object accessible flag as true if it is not
     * 
     * @param accessibleObject
     * @param accessible
     */
    private static void setAccessible(AccessibleObject accessibleObject,
	    boolean accessible) {

	if (ObjectUtils.notTrue(accessible)) {
	    try {
		ObjectUtils.lock(ACCESSOR_LOCK);
		if (notAccessible(accessibleObject)) {
		    accessibleObject.setAccessible(Boolean.TRUE);
		}
	    } finally {
		ObjectUtils.unlock(ACCESSOR_LOCK);
	    }
	}
    }

    /**
     * Modifies passed {@link AccessibleObject} as not accessible
     * 
     * @param accessibleObject
     * @return <code>boolean</code>
     */
    private static boolean makeInaccessible(AccessibleObject accessibleObject) {

	boolean locked = ObjectUtils.tryLock(ACCESSOR_LOCK);
	if (locked) {
	    try {
		if (accessibleObject.isAccessible()) {
		    accessibleObject.setAccessible(Boolean.FALSE);
		}
	    } finally {
		ObjectUtils.unlock(ACCESSOR_LOCK);
	    }
	}

	return locked;
    }

    /**
     * Sets passed {@link AccessibleObject}'s accessible flag as passed
     * accessible boolean value if the last one is false
     * 
     * @param accessibleObject
     * @param accessible
     */
    private static void resetAccessible(AccessibleObject accessibleObject,
	    boolean accessible) {

	if (ObjectUtils.notTrue(accessible)) {
	    try {
		ObjectUtils.lock(ACCESSOR_LOCK);
		if (accessibleObject.isAccessible()) {
		    accessibleObject.setAccessible(accessible);
		}
	    } finally {
		ObjectUtils.unlock(ACCESSOR_LOCK);
	    }
	}
    }

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

	T instance;

	boolean accessible = constructor.isAccessible();
	try {
	    setAccessible(constructor, accessible);
	    instance = constructor.newInstance(parameters);
	} catch (InstantiationException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (InvocationTargetException ex) {
	    throw new IOException(ex);
	} finally {
	    resetAccessible(constructor, accessible);
	}

	return instance;
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

	Constructor<T> constructor;

	try {
	    constructor = type.getDeclaredConstructor(parameterTypes);
	} catch (NoSuchMethodException ex) {
	    throw new IOException(ex);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}

	return constructor;
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

	T instance;

	Constructor<T> constructor = getConstructor(type, parameterTypes);
	instance = newInstance(constructor, parameters);

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

	Class<?> clazz = classForName(className, Boolean.TRUE, loader);

	return clazz;
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

	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	}

	return clazz;
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

	ClassLoader loader = LibraryLoader.getContextClassLoader();
	clazz = classForName(className, Boolean.TRUE, loader);

	return clazz;
    }

    /**
     * Creates {@link Class} instance by {@link Class#newInstance()} method call
     * 
     * @param clazz
     * @return
     */
    public static <T> T instantiate(Class<T> clazz) throws IOException {

	T instance;

	try {
	    instance = clazz.newInstance();
	} catch (InstantiationException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	}

	return instance;
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

	Method method;

	try {
	    method = clazz.getDeclaredMethod(methodName, parameterTypes);
	} catch (NoSuchMethodException ex) {
	    throw new IOException(ex);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}

	return method;
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

	Method[] methods;

	try {
	    methods = clazz.getDeclaredMethods();
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}

	return methods;
    }

    /**
     * Gets one modifier <code>int</code> value for passed collection
     * 
     * @param modifiers
     * @return <code>int</code>
     */
    private static int calculateModifier(int[] modifiers) {

	int modifier = DEFAULT_MODIFIER;

	if (ObjectUtils.notNull(modifiers)) {
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
	    int... modifiers) throws IOException {

	boolean found = Boolean.FALSE;

	Method[] methods = getDeclaredMethods(clazz);
	int length = methods.length;
	int modifier = calculateModifier(modifiers);
	Method method;
	for (int i = 0; i < length && ObjectUtils.notTrue(found); i++) {
	    method = methods[i];
	    found = method.getName().equals(methodName);
	    if (found && ObjectUtils.notEquals(modifier, DEFAULT_MODIFIER)) {
		found = ((method.getModifiers() & modifier) > DEFAULT_MODIFIER);
	    }
	}

	return found;
    }

    /**
     * Finds if passed {@link Class} has {@link Method} with appropriated name
     * and modifiers
     * 
     * @param clazz
     * @param methodName
     * @param modifiers
     * @return <code>boolean</code>
     * @throws IOException
     */
    public static boolean hasMethod(Class<?> clazz, String methodName,
	    int... modifiers) throws IOException {

	boolean found = Boolean.FALSE;

	Class<?> superClass = clazz;
	while (ObjectUtils.notNull(superClass) && ObjectUtils.notTrue(found)) {
	    found = MetaUtils.classHasMethod(superClass, methodName, modifiers);
	    if (ObjectUtils.notTrue(found)) {
		superClass = superClass.getSuperclass();
	    }
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

	boolean found = MetaUtils.hasMethod(clazz, methodName, Modifier.PUBLIC);

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

	Field field;

	try {
	    field = clazz.getDeclaredField(name);
	} catch (NoSuchFieldException ex) {
	    throw new IOException(ex);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}

	return field;
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
     * Common method to invoke {@link Method} with reflection
     * 
     * @param method
     * @param data
     * @param arguments
     * @return {@link Object}
     * @throws IOException
     */
    public static Object invoke(Method method, Object data, Object... arguments)
	    throws IOException {

	Object value;

	try {
	    value = method.invoke(data, arguments);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (InvocationTargetException ex) {
	    throw new IOException(ex);
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
    public static Object invokePrivate(Method method, Object data,
	    Object... arguments) throws IOException {

	Object value;

	boolean accessible = method.isAccessible();
	try {
	    setAccessible(method, accessible);
	    value = invoke(method, data, arguments);
	} finally {
	    resetAccessible(method, accessible);
	}

	return value;
    }

    /**
     * Common method to invoke static {@link Method} with reflection
     * 
     * @param method
     * @param arguments
     * @return
     * @throws IOException
     */
    public static Object invokeStatic(Method method, Object... arguments)
	    throws IOException {

	Object value;

	try {

	    value = method.invoke(null, arguments);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (InvocationTargetException ex) {
	    throw new IOException(ex);
	}

	return value;
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

	Object value;

	boolean accessible = method.isAccessible();
	try {
	    setAccessible(method, accessible);
	    value = invokeStatic(method, arguments);
	} finally {
	    resetAccessible(method, accessible);
	}

	return value;
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
	    setAccessible(field, accessible);
	    field.set(data, value);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} finally {
	    resetAccessible(field, accessible);
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
	    setAccessible(field, accessible);
	    value = field.get(data);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} finally {
	    resetAccessible(field, accessible);
	}

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
     * Gets wrapper class if passed class is primitive type
     * 
     * @param type
     * @return {@link Class}<T>
     */
    public static <T> Class<T> getWrapper(Class<?> type) {

	Class<T> wrapper;

	if (type.isPrimitive()) {

	    if (type.equals(byte.class)) {
		wrapper = ObjectUtils.cast(Byte.class);
	    } else if (type.equals(boolean.class)) {
		wrapper = ObjectUtils.cast(Boolean.class);
	    } else if (type.equals(char.class)) {
		wrapper = ObjectUtils.cast(Character.class);
	    } else if (type.equals(short.class)) {
		wrapper = ObjectUtils.cast(Short.class);
	    } else if (type.equals(int.class)) {
		wrapper = ObjectUtils.cast(Integer.class);
	    } else if (type.equals(long.class)) {
		wrapper = ObjectUtils.cast(Long.class);
	    } else if (type.equals(float.class)) {
		wrapper = ObjectUtils.cast(Float.class);
	    } else if (type.equals(double.class)) {
		wrapper = ObjectUtils.cast(Double.class);
	    } else {
		wrapper = ObjectUtils.cast(type);
	    }

	} else {
	    wrapper = ObjectUtils.cast(type);
	}

	return wrapper;
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
		value = BYTE_DEF;
	    } else if (clazz.equals(boolean.class)) {
		value = BOOLEAN_DEF;
	    } else if (clazz.equals(char.class)) {
		value = CHAR_DEF;
	    } else if (clazz.equals(short.class)) {
		value = SHORT_DEF;
	    } else if (clazz.equals(int.class)) {
		value = INT_DEF;
	    } else if (clazz.equals(long.class)) {
		value = LONG_DEF;
	    } else if (clazz.equals(float.class)) {
		value = FLOAT_DEF;
	    } else if (clazz.equals(double.class)) {
		value = DOUBLE_DEF;
	    } else {
		value = null;
	    }

	} else {
	    value = null;
	}

	return value;
    }
}
