package org.lightmare.utils;

import java.util.Collection;

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

    public static boolean avaliable(Collection<?> collection) {

	return collection != null && !collection.isEmpty();
    }

    public static boolean avaliable(Object[] collection) {

	return collection != null && collection.length > 0;
    }

    public static boolean avaliableAll(Collection<?>... collections) {

	boolean avaliable = notNull(collections);
	if (avaliable) {
	    Collection<?> collection;
	    for (int i = 0; i < collections.length && avaliable; i++) {
		collection = collections[i];
		avaliable = avaliable && avaliable(collection);
	    }
	}

	return avaliable;
    }

    public static boolean avaliableAll(Object[]... collections) {

	boolean avaliable = notNull(collections);
	if (avaliable) {
	    Object[] collection;
	    for (int i = 0; i < collections.length && avaliable; i++) {
		collection = collections[i];
		avaliable = avaliable && avaliable(collection);
	    }
	}

	return avaliable;
    }
}
