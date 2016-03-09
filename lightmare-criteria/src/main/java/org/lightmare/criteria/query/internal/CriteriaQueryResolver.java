package org.lightmare.criteria.query.internal;

import java.io.Serializable;
import java.util.function.BiConsumer;

import org.lightmare.criteria.query.QueryResolver;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryResolver} of JPA
 * criteria queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface CriteriaQueryResolver<T> extends QueryResolver<T> {

    /**
     * Operates on resolved field by expression with other resolved field
     * 
     * @param tuple1
     * @param tuple2
     * @param expression
     */
    default void operate(QueryTuple tuple1, QueryTuple tuple2, BiConsumer<QueryTuple, QueryTuple> expression) {
        expression.accept(tuple1, tuple2);
    }

    /**
     * Resolves and operates on fields by passed expression
     * 
     * @param field1
     * @param field2
     * @param expression
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for passed first
     *         lambda function
     */
    default <V> QueryTuple resolveAndOperateField(Serializable field1, Serializable field2,
            BiConsumer<QueryTuple, QueryTuple> expression) {

        QueryTuple tuple1 = compose(field1);
        QueryTuple tuple2 = compose(field2);
        operate(tuple1, tuple2, expression);

        return tuple1;
    }
}
