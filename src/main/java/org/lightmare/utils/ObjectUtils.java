package org.lightmare.utils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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

    public static final String EMPTY_STRING = "";

    public static boolean notTrue(boolean statement) {

	return !statement;
    }

    public static boolean isFalse(Boolean data) {

	return !data;
    }

    public static boolean notNull(Object data) {

	return (data != null);
    }

    public static boolean notNullAll(Object... datas) {

	boolean valid = datas != null;
	if (valid) {
	    int length = datas.length;
	    for (int i = 0; i < length && valid; i++) {
		valid = datas[i] != null;
	    }
	}

	return valid;
    }

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

    public static boolean notAvailable(Map<?, ?> map) {

	return !available(map);
    }

    public static boolean notAvailable(Collection<?> collection) {

	return !available(collection);
    }

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

    public static boolean available(Object[] collection) {

	return collection != null && collection.length > 0;
    }

    public static boolean notAvailable(Object[] collection) {

	return !available(collection);
    }

    public static boolean available(CharSequence chars) {

	return chars != null && chars.length() > 0;
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

    public static boolean availableAll(Object[]... collections) {

	boolean avaliable = notNull(collections);
	if (avaliable) {
	    Object[] collection;
	    for (int i = 0; i < collections.length && avaliable; i++) {
		collection = collections[i];
		avaliable = avaliable && available(collection);
	    }
	}

	return avaliable;
    }

    /**
     * Converts passed {@link Collection} to array of appropriated {@link Class}
     * type
     * 
     * @param collection
     * @param type
     * @return <code>T[]</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> collection, Class<T> type) {

	T[] array;
	if (notNull(collection)) {
	    array = (T[]) Array.newInstance(type, collection.size());
	    array = collection.toArray(array);
	} else {
	    array = null;
	}

	return array;
    }

    /**
     * Creates empty array of passed type
     * 
     * @param type
     * @return <code>T[]</code>
     */
    public static <T> T[] emptyArray(Class<T> type) {

	@SuppressWarnings("unchecked")
	T[] empty = (T[]) Array.newInstance(type, EMPTY_ARRAY_LENGTH);

	return empty;
    }

    /**
     * Peaks first element from list
     * 
     * @param list
     * @return T
     */
    private static <T> T getFirstFromList(List<T> list) {

	T value;
	if (available(list)) {
	    value = list.get(FIRST_INDEX);
	} else {
	    value = null;
	}

	return value;
    }

    /**
     * Peaks first element from collection
     * 
     * @param collection
     * @return T
     */
    public static <T> T getFirst(Collection<T> collection) {

	T value;
	if (available(collection)) {

	    if (collection instanceof List) {
		value = getFirstFromList(((List<T>) collection));
	    } else {
		Iterator<T> iterator = collection.iterator();
		value = iterator.next();
	    }

	} else {
	    value = null;
	}

	return value;
    }

    /**
     * Peaks first element from array
     * 
     * @param collection
     * @return T
     */
    public static <T> T getFirst(T[] values) {

	T value;
	if (available(values)) {
	    value = values[FIRST_INDEX];
	} else {
	    value = null;
	}

	return value;
    }

    /**
     * Gets value from passed {@link Map} as other {@link Map} instance
     * 
     * @param key
     * @param from
     * @return {@link Map}<K,V>
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> getAsMap(Object key, Map<?, ?> from) {

	Map<K, V> result;
	if (ObjectUtils.available(from)) {
	    Object objectValue = from.get(key);
	    if (objectValue instanceof Map) {
		result = (Map<K, V>) objectValue;
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
	int length = keys.length - 1;
	Object key;
	for (int i = length; i <= 0 && ObjectUtils.notNull(result); i--) {
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
    @SuppressWarnings("unchecked")
    public static <V> V getSubValue(Map<?, ?> from, Object... keys) {

	V value;
	int length = keys.length - 1;
	Object[] subKeys = new Object[length];
	Object key = getFirst(keys);
	for (int i = 0; i < length; i++) {
	    subKeys[i] = keys[i];
	}

	Map<?, ?> result = getAsMap(from, subKeys);
	if (ObjectUtils.available(result)) {
	    value = (V) result.get(key);
	} else {
	    value = null;
	}

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
}
