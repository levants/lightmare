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
package org.lightmare.criteria.lambda;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.lightmare.criteria.cache.LambdaCache;
import org.lightmare.criteria.cache.QueryCache;
import org.lightmare.criteria.meta.EntityValidator;
import org.lightmare.criteria.resolvers.FieldResolver;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Utility class to translate lambda expression to JPA query expression
 * 
 * @author Levan Tsinadze
 *
 */
public class LambdaUtils {

    // Debug message
    private static final String MESSAGE_FORMAT = "Key %s is not bound to cache";

    private static final Logger LOG = Logger.getLogger(LambdaUtils.class);

    /**
     * Logs resolved lambda expression parameters on DEBUG level
     * 
     * @param lambda
     */
    private static void debug(LambdaInfo lambda) {
        String message = String.format(MESSAGE_FORMAT, lambda);
        LOG.info(message);
    }

    /**
     * Resolves {@link org.lightmare.criteria.tuples.QueryTuple} from
     * {@link org.lightmare.criteria.lambda.LambdaInfo} fields
     * 
     * @param lambda
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} from
     *         {@link org.lightmare.criteria.lambda.LambdaInfo}
     */
    private static QueryTuple resolvefromLambda(LambdaInfo lambda) {

        QueryTuple tuple;

        tuple = FieldResolver.resolve(lambda);
        QueryCache.putQuery(lambda, tuple);
        debug(lambda);

        return tuple;
    }

    /**
     * Gets appropriated {@link org.lightmare.criteria.tuples.QueryTuple} from
     * serialized lambda cache or analyzes appropriated lambda expression from
     * compiled class
     * 
     * @param method
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} from cache
     */
    private static QueryTuple getByLambda(Serializable method) {

        QueryTuple tuple;

        LambdaInfo lambda = LambdaReplacements.getReplacement(method);
        tuple = ObjectUtils.getOrInit(() -> QueryCache.getQuery(lambda), () -> resolvefromLambda(lambda));

        return tuple;
    }

    /**
     * Clones passed {@link org.lightmare.criteria.tuples.QueryTuple} without
     * throwing an exception
     * 
     * @param instance
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} clone
     */
    private static QueryTuple cloneTuple(QueryTuple instance) {

        QueryTuple cloneInstance;

        try {
            cloneInstance = ObjectUtils.cast(instance.clone());
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }

        return cloneInstance;
    }

    /**
     * Resolves and caches {@link org.lightmare.criteria.tuples.QueryTuple} from
     * lambda expression
     * 
     * @param method
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} from lambda
     *         function
     */
    private static QueryTuple initAndCache(Serializable method) {

        QueryTuple tuple = getByLambda(method);
        LambdaCache.putByInstance(method, tuple);

        return tuple;
    }

    /**
     * Gets appropriated {@link org.lightmare.criteria.tuples.QueryTuple} from
     * cache or initializes and caches new instance
     * 
     * @param method
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} from cache
     */
    private static QueryTuple getOrInitOriginal(Serializable method) {
        return ObjectUtils.getOrInit(() -> LambdaCache.getByInstance(method), () -> initAndCache(method));
    }

    /**
     * Sets generic parameters to passed
     * {@link org.lightmare.criteria.tuples.QueryTuple} by entity {@link Class}
     * after validation
     * 
     * @param type
     * @param tuple
     */
    private static void setGenericType(Class<?> type, QueryTuple tuple) {

        if (EntityValidator.typeMismatched(type, tuple)) {
            tuple.setTypeAndName(type);
            FieldResolver.setGenericData(tuple);
        }
    }

    /**
     * Sets generic parameters to passed
     * {@link org.lightmare.criteria.tuples.QueryTuple} by entity {@link Class}
     * instance if it is not <code>null</code>
     * 
     * @param type
     * @param tuple
     */
    public static void setGenericIfValid(Class<?> type, QueryTuple tuple) {
        ObjectUtils.nonNull(tuple, c -> setGenericType(type, c));
    }

    /**
     * Gets appropriated {@link org.lightmare.criteria.tuples.QueryTuple} from
     * cache or initializes and caches new instance
     * 
     * @param method
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} from cache
     */
    public static QueryTuple getOrInit(Serializable method) {
        return ObjectUtils.ifNotNull(() -> getOrInitOriginal(method), LambdaUtils::cloneTuple);
    }
}
