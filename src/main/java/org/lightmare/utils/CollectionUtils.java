package org.lightmare.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to work with {@link Collection} instances
 * 
 * @author Levan
 * 
 */
public class CollectionUtils {

    public static final int FIRST_INDEX = 0;

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

	boolean avaliable = ObjectUtils.notNull(maps);
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

	boolean avaliable = ObjectUtils.notNull(collections);
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

	boolean avaliable = ObjectUtils.notNull(arrays);
	if (avaliable) {
	    Object[] collection;
	    int length = arrays.length;
	    for (int i = 0; i < length && avaliable; i++) {
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
	if (available(from)) {
	    Object objectValue = from.get(key);
	    if (objectValue instanceof Map) {
		result = ObjectUtils.cast(objectValue);
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
	if (available(result)) {
	    value = ObjectUtils.cast(result.get(key));
	} else {
	    value = null;
	}

	return value;
    }

    /**
     * Creates new {@link Set} from passed {@link Collection} instance
     * 
     * @param collection
     * @return {@link Set}<code><T></code>
     */
    public static <T> Set<T> translateToSet(Collection<T> collection) {

	Set<T> set;

	if (ObjectUtils.available(collection)) {
	    set = new HashSet<T>(collection);
	} else {
	    set = Collections.emptySet();
	}

	return set;
    }

    /**
     * Creates new {@link Set} from passed array instance
     * 
     * @param array
     * @return {@link Set}<code><T></code>
     */
    public static <T> Set<T> translateToSet(T[] array) {

	List<T> collection;

	if (ObjectUtils.available(array)) {
	    collection = Arrays.asList(array);
	} else {
	    collection = null;
	}

	return translateToSet(collection);
    }

    /**
     * Creates new {@link List} from passed {@link Collection} instance
     * 
     * @param collection
     * @return {@link List}<code><T></code>
     */
    public static <T> List<T> translateToList(Collection<T> collection) {

	List<T> list;

	if (ObjectUtils.available(collection)) {
	    list = new ArrayList<T>(collection);
	} else {
	    list = Collections.emptyList();
	}

	return list;
    }

    private static <T> T[] toArray(Class<T> type, int size) {

	Object arrayObject = Array.newInstance(type, size);

	T[] array = ObjectUtils.cast(arrayObject);

	return array;
    }

    /**
     * Checks if passed {@link Object} is array
     * 
     * @param data
     * @return <code>boolean</code>
     */
    public static boolean isArray(final Object data) {

	boolean valid = (data instanceof Object[] || data instanceof boolean[]
		|| data instanceof byte[] || data instanceof short[]
		|| data instanceof char[] || data instanceof int[]
		|| data instanceof long[] || data instanceof float[] || data instanceof double[]);

	return valid;
    }

    /**
     * Checks if passed {@link Object} is {@link Object} types array
     * 
     * @param data
     * @return <code>boolean</code>
     */
    public static boolean isObjectArray(final Object data) {

	boolean valid = (data instanceof Object[]);

	return valid;
    }

    /**
     * Checks if passed {@link Object} is primitive types array
     * 
     * @param data
     * @return <code>boolean</code>
     */
    public static boolean isPrimitiveArray(final Object data) {

	boolean valid = (data instanceof boolean[] || data instanceof byte[]
		|| data instanceof short[] || data instanceof char[]
		|| data instanceof int[] || data instanceof long[]
		|| data instanceof float[] || data instanceof double[]);

	return valid;
    }

    /**
     * Converts passed {@link Collection} to array of appropriated {@link Class}
     * type
     * 
     * @param collection
     * @param type
     * @return <code>T[]</code>
     */
    public static <T> T[] toArray(Collection<T> collection, Class<T> type) {

	T[] array;

	if (ObjectUtils.notNull(collection)) {
	    array = toArray(type, collection.size());
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

	T[] empty = toArray(type, ObjectUtils.EMPTY_ARRAY_LENGTH);

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

	if (ObjectUtils.available(list)) {
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

	if (ObjectUtils.available(collection)) {

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

	if (ObjectUtils.available(values)) {
	    value = values[FIRST_INDEX];
	} else {
	    value = null;
	}

	return value;
    }
}
