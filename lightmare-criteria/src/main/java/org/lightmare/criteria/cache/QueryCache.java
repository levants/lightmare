package org.lightmare.criteria.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.lightmare.criteria.lambda.LambdaData;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * To cache queries by resolved lambda parameters
 * 
 * @author Levan Tsinadze
 *
 */
public class QueryCache {

    private static final ConcurrentHashMap<String, QueryTuple> QUERIES = new ConcurrentHashMap<>();

    private static final String PREFIX = "LAMBDA:";

    private static final String DELIM = ":";

    /**
     * Generates key from {@link LambdaData} fields
     * 
     * @param lambda
     * @return
     */
    private static String generateKey(LambdaData lambda) {

	String key;

	String type = lambda.getImplClass();
	String method = lambda.getImplMethodName();
	String sign = lambda.getImplMethodSignature();
	key = PREFIX.concat(type).concat(DELIM).concat(method).concat(DELIM).concat(sign);

	return key;
    }

    public static QueryTuple getQuery(String key) {
	return QUERIES.get(key);
    }

    public static QueryTuple getQuery(LambdaData lambda) {

	QueryTuple tuple;

	String key = generateKey(lambda);
	tuple = getQuery(key);

	return tuple;
    }

    public static void putQuery(String key, QueryTuple value) {
	QUERIES.putIfAbsent(key, value);
    }

    public static void putQuery(LambdaData lambda, QueryTuple value) {
	String key = generateKey(lambda);
	putQuery(key, value);
    }
}
