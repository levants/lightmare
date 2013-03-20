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
}
