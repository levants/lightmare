package org.lightmare.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

	boolean valid = data instanceof Object[] || data instanceof boolean[]
		|| data instanceof byte[] || data instanceof short[]
		|| data instanceof char[] || data instanceof int[]
		|| data instanceof long[] || data instanceof float[]
		|| data instanceof double[];

	return valid;
    }

    /**
     * Checks if passed {@link Object} is {@link Object} types array
     * 
     * @param data
     * @return <code>boolean</code>
     */
    public static boolean isObjectArray(final Object data) {

	boolean valid = data instanceof Object[];

	return valid;
    }

    /**
     * Checks if passed {@link Object} is primitive types array
     * 
     * @param data
     * @return <code>boolean</code>
     */
    public static boolean isPrimitiveArray(final Object data) {

	boolean valid = data instanceof boolean[] || data instanceof byte[]
		|| data instanceof short[] || data instanceof char[]
		|| data instanceof int[] || data instanceof long[]
		|| data instanceof float[] || data instanceof double[];

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
