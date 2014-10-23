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
package org.lightmare.utils.reflect;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Utility class to use reflection {@link Method}, {@link Constructor} or any
 * {@link AccessibleObject} calls and get / set / modify {@link Field} value
 * 
 * @author Levan Tsinadze
 * @since 0.0.26-SNAPSHOT
 */
public class ClassUtils {

    // default value for modifier
    private static final int DEFAULT_MODIFIER = 0;

    // Lock to modify accessible mode of AccessibleObject instances
    private static final Lock ACCESSOR_LOCK = new ReentrantLock();

    /**
     * Gets target {@link Throwable} from passed
     * {@link InvocationTargetException} instance
     * 
     * @param ex
     * @return {@link IOException}
     */
    private static IOException unwrap(InvocationTargetException ex) {

	IOException exception;

	Throwable targetException = ex.getTargetException();
	if (targetException == null) {
	    exception = new IOException(ex.getMessage(), ex);
	} else {
	    exception = new IOException(targetException.getMessage(),
		    targetException);
	}

	return exception;
    }

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
     * Sets object accessible flag as true if it is not
     * 
     * @param accessibleObject
     * @param accessible
     */
    private static void setAccessible(AccessibleObject accessibleObject,
	    boolean accessible) {

	if (ObjectUtils.notTrue(accessible)) {
	    try {
		// Should I use synchronized(accessibleObject) block
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
		// Should I use synchronized(accessibleObject) block
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
	    throw unwrap(ex);
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
     * {@link ClassUtils#getConstructor(Class, Class...)} method call
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
	return classForName(className, null);
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
	return classForName(className, Boolean.TRUE, loader);
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
	    for (int i = CollectionUtils.FIRST_INDEX; i < length; i++) {
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
	for (int i = CollectionUtils.FIRST_INDEX; i < length
		&& ObjectUtils.notTrue(found); i++) {
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
	    found = ClassUtils
		    .classHasMethod(superClass, methodName, modifiers);
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
	return ClassUtils.hasMethod(clazz, methodName, Modifier.PUBLIC);
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
     * Returns passed {@link Member}'s modifiers
     * 
     * @param member
     * @return <code>int</code> modifiers
     */
    public static int getModifiers(Member member) {
	return member.getModifiers();
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
	    throw unwrap(ex);
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
	return getFieldValue(field, null);
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
     * Gets wrapper class if passed class is a primitive type
     * 
     * @param type
     * @return {@link Class}<T>
     */
    public static <T> Class<T> getWrapper(Class<?> type) {
	return Primitives.getWrapper(type);
    }

    /**
     * Returns default values if passed class is primitive else returns null
     * 
     * @param clazz
     * @return Object
     */
    public static Object getDefault(Class<?> type) {
	return Primitives.getDefault(type);
    }
}
