package org.lightmare.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Utility class to help with general object checks
 * 
 * @author levan
 * 
 */
public class ObjectUtils {

    public static boolean notNull(Object data) {

	return data != null;
    }

    public static boolean available(Collection<?> collection) {

	return collection != null && !collection.isEmpty();
    }

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

    public static boolean notEmpty(Collection<?> collection) {

	return !collection.isEmpty();
    }

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
}
