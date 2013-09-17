package org.lightmare.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Utility class to help with general object checks
 * 
 * @author levan
 * 
 */
public class ObjectUtils {

    public static final int EMPTY_ARRAY_LENGTH = 0;

    public static final Object[] EMPTY_ARRAY = {};

    public static final int FIRST_INDEX = 0;

    public static final int SECOND_INDEX = 1;

    public static final int NOT_EXISTING_INDEX = -1;

    /**
     * Checks if passed boolean value is not true
     * 
     * @param statement
     * @return <code>boolean</code>
     */
    public static boolean notTrue(boolean statement) {

	return !statement;
    }

    /**
     * Checks if passed object is not null
     * 
     * @param data
     * @return <code>boolean</code>
     */
    public static boolean notNull(Object data) {

	return (data != null);
    }

    /**
     * Checks if not a single object passed objects is not null
     * 
     * @param datas
     * @return <code>boolean</code>
     */
    public static boolean notNullAll(Object... datas) {

	boolean valid = notNull(datas);
	if (valid) {
	    int length = datas.length;
	    Object data;
	    for (int i = 0; i < length && valid; i++) {
		data = datas[i];
		valid = notNull(data);
	    }
	}

	return valid;
    }

    /**
     * Checks if parameters not equals
     * 
     * @param data1
     * @param data2
     * @return <code>boolean</code>
     */
    public static boolean notEquals(Object data1, Object data2) {

	return !data1.equals(data2);
    }

    /**
     * Checks if parameters not equals
     * 
     * @param x
     * @param y
     * @return <code>boolean</code>
     */
    public static boolean notEquals(int x, int y) {

	return x != y;
    }

    public static boolean notNullNotEquals(Object data1, Object data2) {

	return notNullAll(data1, data2) && notEquals(data1, data2);
    }

    /**
     * Checks if passed {@link Collection} instance is not empty
     * 
     * @param collection
     * @return <code>boolean</code>
     */
    public static boolean notEmpty(Collection<?> collection) {

	return !collection.isEmpty();
    }

    /**
     * Checks passed {@link Collection} instance on null and on emptiness
     * returns true if it is not null and is not empty
     * 
     * @param collection
     * @return <code></code>
     */
    public static boolean available(Collection<?> collection) {

	return collection != null && !collection.isEmpty();
    }

    /**
     * Checks passed {@link Map} instance on null and emptiness returns true if
     * it is not null and is not empty
     * 
     * @param map
     * @return <code>boolean</code>
     */
    public static boolean available(Map<?, ?> map) {

	return map != null && !map.isEmpty();
    }

    /**
     * Checks if passed {@link Map} instance is null or is empty
     * 
     * @param map
     * @return <code>boolean</code>
     */
    public static boolean notAvailable(Map<?, ?> map) {

	return !available(map);
    }

    /**
     * Checks if passed {@link Collection} instance is null or is empty
     * 
     * @param collection
     * @return <code>boolean</code>
     */
    public static boolean notAvailable(Collection<?> collection) {

	return !available(collection);
    }

    /**
     * Checks if there is null or empty {@link Collection} instance is passed
     * collections
     * 
     * @param collections
     * @return <code>boolean</code>
     */
    public static boolean notAvailable(Collection<?>... collections) {

	return !available(collections);
    }

    public static boolean availableAll(Map<?, ?>... maps) {

	boolean avaliable = notNull(maps);
	if (avaliable) {
	    Map<?, ?> map;
	    for (int i = 0; i < maps.length && avaliable; i++) {
		map = maps[i];
		avaliable = avaliable && available(map);
	    }
	}

	return avaliable;
    }

    public static boolean available(Object[] array) {

	return array != null && array.length > EMPTY_ARRAY_LENGTH;
    }

    public static boolean notAvailable(Object[] array) {

	return !available(array);
    }

    public static boolean available(CharSequence chars) {

	return chars != null && chars.length() > EMPTY_ARRAY_LENGTH;
    }

    public static boolean notAvailable(CharSequence chars) {

	return !available(chars);
    }

    public static boolean availableAll(Collection<?>... collections) {

	boolean avaliable = notNull(collections);
	if (avaliable) {
	    Collection<?> collection;
	    for (int i = 0; i < collections.length && avaliable; i++) {
		collection = collections[i];
		avaliable = avaliable && available(collection);
	    }
	}

	return avaliable;
    }

    public static boolean availableAll(Object[]... arrays) {

	boolean avaliable = notNull(arrays);
	if (avaliable) {
	    Object[] collection;
	    for (int i = 0; i < arrays.length && avaliable; i++) {
		collection = arrays[i];
		avaliable = avaliable && available(collection);
	    }
	}

	return avaliable;
    }

    /**
     * Gets value from passed {@link Map} as other {@link Map} instance
     * 
     * @param key
     * @param from
     * @return {@link Map}<K,V>
     */
    public static <K, V> Map<K, V> getAsMap(Object key, Map<?, ?> from) {

	Map<K, V> result;
	if (ObjectUtils.available(from)) {
	    Object objectValue = from.get(key);
	    if (objectValue instanceof Map) {
		result = cast(objectValue);
	    } else {
		result = null;
	    }
	} else {
	    result = null;
	}

	return result;
    }

    /**
     * Gets values from passed {@link Map} as other {@link Map} instance
     * recursively by passed keys array
     * 
     * @param from
     * @param keys
     * @return {@link Map}
     */
    public static Map<?, ?> getAsMap(Map<?, ?> from, Object... keys) {

	Map<?, ?> result = from;
	int length = keys.length;
	Object key;
	for (int i = 0; i < length && ObjectUtils.notNull(result); i++) {
	    key = keys[i];
	    result = getAsMap(key, result);
	}

	return result;
    }

    /**
     * Gets values from passed {@link Map} as other {@link Map} instance
     * recursively by passed keys array and for first key get value from last
     * {@link Map} instance
     * 
     * @param from
     * @param keys
     * @return <code>V</code>
     */
    public static <V> V getSubValue(Map<?, ?> from, Object... keys) {

	V value;
	int length = keys.length - 1;
	Object[] subKeys = new Object[length];
	Object key = keys[length];
	for (int i = 0; i < length; i++) {
	    subKeys[i] = keys[i];
	}

	Map<?, ?> result = getAsMap(from, subKeys);
	if (ObjectUtils.available(result)) {
	    value = cast(result.get(key));
	} else {
	    value = null;
	}

	return value;
    }

    /**
     * Cats passed {@link Object} to generic parameter
     * 
     * @param data
     * @return <code>T</code>
     */
    public static <T> T cast(Object data) {

	@SuppressWarnings("unchecked")
	T value = (T) data;

	return value;
    }

    /**
     * Checks if passed {@link Closeable} instance is not null and if not calls
     * {@link Closeable#close()} method
     * 
     * @param closeable
     * @throws IOException
     */
    public static void close(Closeable closeable) throws IOException {

	if (ObjectUtils.notNull(closeable)) {
	    closeable.close();
	}
    }

    /**
     * Checks if passed array of {@link Closeable}'s is valid and closes all of
     * them
     * 
     * @param closeables
     * @throws IOException
     */
    public static void closeAll(Closeable... closeables) throws IOException {

	if (available(closeables)) {
	    for (Closeable closeable : closeables) {
		close(closeable);
	    }
	}
    }
}
