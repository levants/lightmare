/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
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
 * @author Levan Tsinadze
 * @since 0.0.81-SNAPSHOT
 */
public abstract class CollectionUtils {

    // First index of array
    public static final int FIRST_INDEX = 0;

    // Second index of array
    public static final int SECOND_INDEX = 1;

    // Index of not existing data in collection
    public static final int NOT_EXISTING_INDEX = -1;

    // Length of empty array
    public static final int EMPTY_ARRAY_LENGTH = 0;

    // Empty array of objects
    public static final Object[] EMPTY_ARRAY = {};

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
    public static boolean valid(Collection<?> collection) {
	return collection != null && !collection.isEmpty();
    }

    /**
     * Checks passed {@link Map} instance on null and emptiness returns true if
     * it is not null and is not empty
     * 
     * @param map
     * @return <code>boolean</code>
     */
    public static boolean valid(Map<?, ?> map) {
	return map != null && !map.isEmpty();
    }

    /**
     * Checks if passed {@link Map} instance is null or is empty
     * 
     * @param map
     * @return <code>boolean</code>
     */
    public static boolean invalid(Map<?, ?> map) {
	return !valid(map);
    }

    /**
     * Checks if passed {@link Collection} instance is null or is empty
     * 
     * @param collection
     * @return <code>boolean</code>
     */
    public static boolean invalid(Collection<?> collection) {
	return !valid(collection);
    }

    /**
     * Checks if there is null or empty {@link Collection} instance is passed
     * collections
     * 
     * @param collections
     * @return <code>boolean</code>
     */
    public static boolean invalidAll(Collection<?>... collections) {
	return !valid(collections);
    }

    /**
     * Checks if each of passed {@link Map} instances is not null and is not
     * empty
     * 
     * @param maps
     * @return <code>boolean</code>
     */
    public static boolean validAll(Map<?, ?>... maps) {

	boolean avaliable = ObjectUtils.notNull(maps);

	if (avaliable) {
	    Map<?, ?> map;
	    for (int i = FIRST_INDEX; i < maps.length && avaliable; i++) {
		map = maps[i];
		avaliable = avaliable && valid(map);
	    }
	}

	return avaliable;
    }

    /**
     * Checks if passed array of {@link Object}'s instances is not null and is
     * not empty
     * 
     * @param array
     * @return <code>boolean</code>
     */
    public static boolean valid(Object[] array) {
	return array != null && array.length > EMPTY_ARRAY_LENGTH;
    }

    /**
     * Checks if passed {@link Object} array is null or is empty
     * 
     * @param array
     * @return <code>boolean</code>
     */
    public static boolean invalid(Object[] array) {
	return !valid(array);
    }

    /**
     * Checks if each of passed {@link Collection} instances is not null and is
     * not empty
     * 
     * @param collections
     * @return <code>boolean</code>
     */
    public static boolean validAll(Collection<?>... collections) {

	boolean avaliable = ObjectUtils.notNull(collections);

	if (avaliable) {
	    Collection<?> collection;
	    for (int i = FIRST_INDEX; i < collections.length && avaliable; i++) {
		collection = collections[i];
		avaliable = avaliable && valid(collection);
	    }
	}

	return avaliable;
    }

    /**
     * * Checks if each of passed {@link Object} array instances is not null and
     * is not empty
     * 
     * @param arrays
     * @return <code>boolean</code>
     */
    public static boolean validAll(Object[]... arrays) {

	boolean avaliable = ObjectUtils.notNull(arrays);

	if (avaliable) {
	    Object[] collection;
	    int length = arrays.length;
	    for (int i = FIRST_INDEX; i < length && avaliable; i++) {
		collection = arrays[i];
		avaliable = avaliable && valid(collection);
	    }
	}

	return avaliable;
    }

    /**
     * Opposite to {@link Map#containsKey(Object)} method for passed {@link Map}
     * map and K key
     * 
     * @param map
     * @param key
     * @return <code>boolean</code>
     */
    public static <K, V> boolean notContains(Map<K, V> map, K key) {
	return valid(map) && ObjectUtils.notTrue(map.containsKey(key));
    }

    /**
     * Opposite to {@link Collection#contains(Object)} method for passed
     * {@link Collection} collection and E element
     * 
     * @param collection
     * @param element
     * @return <code>boolean</code>
     */
    public static <E> boolean notContains(Collection<E> collection, E element) {
	return valid(collection)
		&& ObjectUtils.notTrue(collection.contains(element));
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

	boolean valid = valid(from);
	if (valid) {
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
	for (int i = FIRST_INDEX; i < length && ObjectUtils.notNull(result); i++) {
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

	int length = keys.length - SECOND_INDEX;
	Object[] subKeys = new Object[length];
	Object key = keys[length];
	for (int i = FIRST_INDEX; i < length; i++) {
	    subKeys[i] = keys[i];
	}

	Map<?, ?> result = getAsMap(from, subKeys);
	if (valid(result)) {
	    value = ObjectUtils.cast(result.get(key));
	} else {
	    value = null;
	}

	return value;
    }

    /**
     * Puts passed value to passed {@link Map} instance on passed key of such
     * does not contained
     * 
     * @param map
     * @param key
     * @param value
     */
    public static <K, V> void putIfAbscent(Map<K, V> map, K key, V value) {

	boolean contained = map.containsKey(key);
	if (ObjectUtils.notTrue(contained)) {
	    map.put(key, value);
	}
    }

    /**
     * Puts passed value to passed {@link Map} instance on passed key of such
     * does not contained or its associated key does not equals passed value
     * 
     * @param map
     * @param key
     * @param value
     */
    public static <K, V> void checkAndAdd(Map<K, V> map, K key, V value) {

	boolean contained = map.containsKey(key)
		&& ObjectUtils.notEquals(value, map.get(key));
	if (ObjectUtils.notTrue(contained)) {
	    map.put(key, value);
	}
    }

    /**
     * Creates new {@link Set} from passed {@link Collection} instance
     * 
     * @param collection
     * @return {@link Set} translated from collection
     */
    public static <T> Set<T> translateToSet(Collection<T> collection) {

	Set<T> set;

	if (valid(collection)) {
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
     * @return {@link Set} translated from array
     */
    public static <T> Set<T> translateToSet(T[] array) {

	List<T> collection;

	if (valid(array)) {
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
     * @return {@link List} translated from collection
     */
    public static <T> List<T> translateToList(Collection<T> collection) {

	List<T> list;

	if (valid(collection)) {
	    list = new ArrayList<T>(collection);
	} else {
	    list = Collections.emptyList();
	}

	return list;
    }

    /**
     * Creates array of generic type <code>T</code> of specific size
     * 
     * @param type
     * @param size
     * @return <code>T[]</code> initialized array
     */
    private static <T> T[] toArray(Class<T> type, int size) {

	T[] array;

	Object arrayObject = Array.newInstance(type, size);
	array = ObjectUtils.cast(arrayObject);

	return array;
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
     * Checks if passed {@link Object} is array of primitives
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
     * Checks if passed {@link Object} is array
     * 
     * @param data
     * @return <code>boolean</code>
     */
    public static boolean isArray(final Object data) {

	boolean valid = (isObjectArray(data) || isPrimitiveArray(data));

	return valid;
    }

    /**
     * Converts passed {@link Collection} to array of appropriated {@link Class}
     * types
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

	T[] empty = toArray(type, EMPTY_ARRAY_LENGTH);

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

	if (valid(list)) {
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

	if (valid(collection)) {
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

	if (valid(values)) {
	    value = values[FIRST_INDEX];
	} else {
	    value = null;
	}

	return value;
    }
}
