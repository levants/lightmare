package org.lightmare.linq.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.lightmare.linq.tuples.QueryTuple;

/**
 * To cache queries by lambda call
 * 
 * @author Levan Tsinadze
 *
 */
public class QueryCache {

    private static final ConcurrentHashMap<String, QueryTuple> QUERIES = new ConcurrentHashMap<>();

    public static QueryTuple getQuery(String key) {
	return QUERIES.get(key);
    }

    public static void putQuery(String key, QueryTuple value) {
	QUERIES.put(key, value);
    }
}
