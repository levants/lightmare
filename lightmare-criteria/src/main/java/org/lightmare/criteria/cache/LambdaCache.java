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
import java.util.concurrent.ConcurrentMap;

import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Caches lambda generated classes with phantom reference and cleans
 * appropriated cache when class is reclaimed
 * 
 * @author Levan Tsinadze
 *
 */
public class LambdaCache {

    // Cache for query parts by lambda parameters
    private static final ConcurrentMap<String, QueryTuple> LAMBDAS = new ConcurrentHashMap<>();

    /**
     * Adds passed lambda {@link Class} and
     * {@link org.lightmare.criteria.tuples.QueryTuple} to cache
     * 
     * @param type
     * @param value
     */
    public static void putLambda(Class<?> type, QueryTuple value) {
        QueryTuple existed = LAMBDAS.putIfAbsent(type.getName(), value);
        ObjectUtils.equals(value, existed, (x, y) -> LambdaReferences.trace(type));
    }

    /**
     * Adds lambda {@link Class} by object instance and
     * {@link org.lightmare.criteria.tuples.QueryTuple} to cache
     * 
     * @param lambda
     * @param value
     */
    public static void putByInstance(Object lambda, QueryTuple value) {
        putLambda(lambda.getClass(), value);
    }

    /**
     * Gets {@link org.lightmare.criteria.tuples.QueryTuple} from cache by
     * passed lambda {@link Class} name as key
     * 
     * @param type
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for this lambda
     *         {@link Class}
     */
    public static QueryTuple getLambda(Class<?> type) {
        return LAMBDAS.get(type.getName());
    }

    /**
     * Gets {@link org.lightmare.criteria.tuples.QueryTuple} from cache by
     * lambda {@link Class} name from passed lambda instance as key
     * 
     * @param lambda
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for this lambda
     *         {@link Class}
     */
    public static QueryTuple getByInstance(Object lambda) {
        return getLambda(lambda.getClass());
    }

    /**
     * Removes passed lambda {@link Class} and associated
     * {@link org.lightmare.criteria.tuples.QueryTuple} from cache
     * 
     * @param type
     */
    public static void remove(Class<?> type) {
        LAMBDAS.remove(type.getName());
    }
}
