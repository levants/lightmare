package org.lightmare.criteria.query.mongo;

import org.lightmare.criteria.query.QueryResolver;
import org.lightmare.criteria.query.mongo.layers.MongoExpressions.Binaries;
import org.lightmare.criteria.query.mongo.layers.MongoExpressions.Unaries;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Resolver implementation for MongoDB queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 */
public interface MongoResolver<T> extends QueryResolver<T> {

    @Override
    default void operate(final QueryTuple tuple, String expression) {

        Unaries unary = Unaries.valueOf(expression);
        String column = getLayerProvider().getColumnName(tuple);
        unary.function.apply(column);
    }

    @Override
    default void operate(QueryTuple tuple, String expression, Object value) {

        Binaries binary = Binaries.valueOf(expression);
        String column = getLayerProvider().getColumnName(tuple);
        binary.function.apply(column, value);
    }
}
