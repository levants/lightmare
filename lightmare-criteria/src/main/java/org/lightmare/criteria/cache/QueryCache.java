package org.lightmare.criteria.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.lightmare.criteria.lambda.LambdaData;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * To cache queries by lambda data call
 * 
 * @author Levan Tsinadze
 *
 */
public class QueryCache {

    private static final ConcurrentHashMap<String, QueryTuple> QUERIES = new ConcurrentHashMap<>();

    private static final String DELIM = ":";

    private static String getKey(LambdaData lambda) {
	return lambda.getImplClass().concat(DELIM).concat(lambda.getImplMethodName()).concat(DELIM)
		.concat(lambda.getImplMethodSignature());
    }

    public static QueryTuple getQuery(String key) {
	return QUERIES.get(key);
    }

    public static QueryTuple getQuery(LambdaData lambda) {

	QueryTuple tuple;

	String key = getKey(lambda);
	tuple = getQuery(key);

	return tuple;
    }

    public static void putQuery(String key, QueryTuple value) {
	QUERIES.putIfAbsent(key, value);
    }

    public static void putQuery(LambdaData lambda, QueryTuple value) {
	String key = getKey(lambda);
	putQuery(key, value);
    }
}
