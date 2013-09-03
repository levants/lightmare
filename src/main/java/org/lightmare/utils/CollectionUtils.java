package org.lightmare.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class to work with {@link Collection} instances
 * 
 * @author Levan
 * 
 */
public class CollectionUtils {

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

	List<T> collection = Arrays.asList(array);

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
}
