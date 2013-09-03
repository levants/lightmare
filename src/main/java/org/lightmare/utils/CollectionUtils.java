package org.lightmare.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class to work with {@link Collection} instances
 * 
 * @author Levan
 * 
 */
public class CollectionUtils {

    public static <T> Set<T> translateToSet(Collection<T> collection) {

	Set<T> set;
	if (ObjectUtils.available(collection)) {
	    set = new HashSet<T>(collection);
	} else {
	    set = Collections.emptySet();
	}

	return set;
    }
}
