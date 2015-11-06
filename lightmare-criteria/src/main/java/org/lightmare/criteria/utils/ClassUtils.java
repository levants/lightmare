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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class to use reflection {@link Method}, {@link Constructor} or any
 * {@link AccessibleObject} calls and get / set / modify {@link Field} value
 *
 * @author Levan Tsinadze
 */
public class ClassUtils {

    // default value for modifier
    private static final int DEFAULT_MODIFIER = 0;

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
	    exception = new IOException(targetException.getMessage(), targetException);
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
     * Sets accessible flag to {@link AccessibleObject} instance
     * 
     * @param accessibleObject
     */
    private static void makeAccessible(AccessibleObject accessibleObject) {

	if (notAccessible(accessibleObject)) {
	    accessibleObject.setAccessible(Boolean.TRUE);
	}
    }

    /**
     * Sets object accessible flag as true if it is not
     *
     * @param accessibleObject
     */
    private static void setAccessible(AccessibleObject accessibleObject) {

	if (notAccessible(accessibleObject)) {
	    synchronized (accessibleObject) {
		makeAccessible(accessibleObject);
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
    public static <T> T newInstance(Constructor<T> constructor, Object... parameters) throws IOException {

	T instance;

	try {
	    setAccessible(constructor);
	    instance = constructor.newInstance(parameters);
	} catch (InstantiationException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (InvocationTargetException ex) {
	    throw unwrap(ex);
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
    public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... parameterTypes) throws IOException {

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
    public static <T> T callConstructor(Class<T> type, Class<?>[] parameterTypes, Object... parameters)
	    throws IOException {

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
    public static Class<?> classForName(String className, ClassLoader loader) throws IOException {
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
    public static Class<?> classForName(String className, boolean initialize, ClassLoader loader) throws IOException {

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
    public static Class<?> initClassForName(String className) throws IOException {

	Class<?> clazz;

	ClassLoader loader = ClassLoaderUtils.getContextClassLoader();
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
    public static Method getDeclaredMethod(Class<?> type, String methodName, Class<?>... parameterTypes)
	    throws IOException {

	Method method;

	try {
	    method = type.getDeclaredMethod(methodName, parameterTypes);
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
     * Gets one modifier <code>int</code> value for passed collection
     *
     * @param modifiers
     * @return <code>int</code>
     */
    private static int calculateModifier(int[] modifiers) {

	int modifier = DEFAULT_MODIFIER;

	if (Objects.nonNull(modifiers)) {
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
     * @param type
     * @param modifiers
     * @param methodName
     * @return <code>boolean</code>
     * @throws IOException
     */
    private static boolean classHasMethod(Class<?> type, String methodName, int... modifiers) throws IOException {

	boolean found = Boolean.FALSE;

	Method[] methods = getDeclaredMethods(type);
	int length = methods.length;
	int modifier = calculateModifier(modifiers);
	Method method;
	for (int i = CollectionUtils.FIRST_INDEX; i < length && Boolean.FALSE.equals(found); i++) {
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
     * @param type
     * @param methodName
     * @param modifiers
     * @return <code>boolean</code>
     * @throws IOException
     */
    public static boolean hasMethod(Class<?> type, String methodName, int... modifiers) throws IOException {

	boolean found = Boolean.FALSE;

	Class<?> superClass = type;
	while (Objects.nonNull(superClass) && ObjectUtils.notTrue(found)) {
	    found = ClassUtils.classHasMethod(superClass, methodName, modifiers);
	    if (ObjectUtils.notTrue(found)) {
		superClass = superClass.getSuperclass();
	    }
	}

	return found;
    }

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
     * Finds if passed {@link Class} has public {@link Method} with appropriated
     * name
     *
     * @param clazz
     * @param methodName
     * @return <code>boolean</code>
     * @throws IOException
     */
    public static boolean hasPublicMethod(Class<?> clazz, String methodName) throws IOException {
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
    public static Field getDeclaredField(Class<?> clazz, String name) throws IOException {

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

	setAccessible(method);
	value = invoke(method, data, arguments);

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
    public static Object invokeStatic(Method method, Object... arguments) throws IOException {

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
    public static Object invokePrivateStatic(Method method, Object... arguments) throws IOException {

	Object value;

	setAccessible(method);
	value = invokeStatic(method, arguments);

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
    public static void setFieldValue(Field field, Object data, Object value) throws IOException {

	try {
	    setAccessible(field);
	    field.set(data, value);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
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
    public static Object getFieldValue(Field field, Object data) throws IOException {

	Object value;

	try {
	    setAccessible(field);
	    value = field.get(data);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
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
     * Gets {@link List} of {@link AccessibleObject}s with instant annotation
     * 
     * @param array
     * @param annotationType
     * @return
     */
    private static <T extends AccessibleObject> List<T> filterByAnnotation(T[] array,
	    Class<? extends Annotation> annotationType) {
	return Stream.of(array).filter(c -> c.isAnnotationPresent(annotationType)).collect(Collectors.toList());
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
     * @param clazz
     * @param annotationClass
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
