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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.lightmare.criteria.cache.LambdaCache;
import org.lightmare.criteria.cache.QueryCache;
import org.lightmare.criteria.resolvers.FieldResolver;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Utility class to translate lambda expression to query
 * 
 * @author Levan Tsinadze
 *
 */
public class LambdaUtils {

    // Debug messages
    private static final String DEBUG_MESSAGE_FORMAT = "Key %s is not bound to cache";

    private static final Logger LOG = Logger.getLogger(LambdaUtils.class);

    /**
     * Gets appropriated {@link QueryTuple} from serialized lambda cache or
     * analyzes appropriated lambda expression from compiled class
     * 
     * @param field
     * @return {@link QueryTuple} from cache
     * @throws IOException
     */
    private static QueryTuple getByLambda(Object method) throws IOException {

	QueryTuple tuple;

	LambdaData lambda = LambdaReplacements.getReplacement(method);
	tuple = QueryCache.getQuery(lambda);
	if (tuple == null) {
	    tuple = FieldResolver.resolve(lambda);
	    QueryCache.putQuery(lambda, tuple);
	    LOG.debug(String.format(DEBUG_MESSAGE_FORMAT, lambda));
	}

	return tuple;
    }

    /**
     * Gets appropriated {@link QueryTuple} from cache or initializes and caches
     * new instance
     * 
     * @param method
     * @return {@link QueryTuple} from cache
     * @throws IOException
     */
    public static QueryTuple getOrInit(Object method) throws IOException {

	QueryTuple tuple = LambdaCache.getByInstance(method);

	if (tuple == null) {
	    tuple = getByLambda(method);
	    LambdaCache.putByInstance(method, tuple);
	}

	return tuple;
    }
}
