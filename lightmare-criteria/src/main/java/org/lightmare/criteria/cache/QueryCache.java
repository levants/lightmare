/*
 * Lightmare-criteria, JPA-QL query generator using lambda expressions
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
package org.lightmare.criteria.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.lightmare.criteria.lambda.LambdaInfo;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.StringUtils;

/**
 * To cache queries by resolved lambda parameters
 * 
 * @author Levan Tsinadze
 *
 */
public class QueryCache {

    // Cache for query containers
    private static final ConcurrentHashMap<String, QueryTuple> QUERIES = new ConcurrentHashMap<>();

    // Cache key prefix for lambda expression
    private static final String PREFIX = "LAMBDA:";

    // Cache key delimiter for lambda expression
    private static final String DELIM = ":";

    /**
     * Generates key from {@link org.lightmare.criteria.lambda.LambdaInfo}
     * fields
     * 
     * @param lambda
     * @return {@link String} generated key
     */
    private static String generateKey(LambdaInfo lambda) {

        String key;

        String type = lambda.getImplClass();
        String method = lambda.getImplMethodName();
        String sign = lambda.getImplMethodSignature();
        key = StringUtils.concat(PREFIX, type, DELIM, method, DELIM, sign);

        return key;
    }

    /**
     * Gets appropriated {@link org.lightmare.criteria.tuples.QueryTuple} from
     * cache by key
     * 
     * @param key
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} from cache
     */
    public static QueryTuple getQuery(String key) {
        return QUERIES.get(key);
    }

    /**
     * Gets appropriated {@link org.lightmare.criteria.tuples.QueryTuple} from
     * cache by appropriated {@link org.lightmare.criteria.lambda.LambdaInfo}
     * instance
     * 
     * @param lambda
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} from cache
     */
    public static QueryTuple getQuery(LambdaInfo lambda) {

        QueryTuple tuple;

        String key = generateKey(lambda);
        tuple = getQuery(key);

        return tuple;
    }

    /**
     * Caches {@link org.lightmare.criteria.tuples.QueryTuple} by key
     * 
     * @param key
     * @param value
     */
    public static void putQuery(String key, QueryTuple value) {
        QUERIES.putIfAbsent(key, value);
    }

    /**
     * Caches {@link org.lightmare.criteria.tuples.QueryTuple} by
     * {@link org.lightmare.criteria.lambda.LambdaInfo} instance
     * 
     * @param lambda
     * @param value
     */
    public static void putQuery(LambdaInfo lambda, QueryTuple value) {
        String key = generateKey(lambda);
        putQuery(key, value);
    }
}
