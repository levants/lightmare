package org.lightmare.criteria.query;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.lightmare.criteria.lambda.LambdaUtils;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Interface for entity field and data base field / column resolver
 * 
 * @author Levan Tsinadze
 *
 */
public interface QueryResolver<T> extends LayerStream<T> {

    /**
     * Operates on resolved field by expression
     * 
     * @param tuple
     * @param expression
     */
    default void operate(QueryTuple tuple, Consumer<QueryTuple> expression) {
        expression.accept(tuple);
    }

    /**
     * Operates on resolved field by expression with parameter
     * 
     * @param tuple
     * @param value
     * @param expression
     */
    default <V> void operate(QueryTuple tuple, V value, BiConsumer<QueryTuple, V> expression) {
        expression.accept(tuple, value);
    }

    /**
     * Gets appropriated {@link org.lightmare.criteria.tuples.QueryTuple} from
     * cache or generates from compiled class
     * 
     * @param field
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for passed
     *         lambda function
     */
    default QueryTuple resolve(Serializable field) {

        QueryTuple tuple;

        tuple = LambdaUtils.getOrInit(field);
        tuple.setAlias(getAlias());
        tuple.setFieldName(getLayerProvider().getColumnName(tuple));

        return tuple;
    }

    /**
     * Gets appropriated {@link org.lightmare.criteria.tuples.QueryTuple} from
     * cache or generates from compiled class with generic parameters
     * 
     * @param field
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for passed
     *         lambda function
     */
    default QueryTuple compose(Serializable field) {

        QueryTuple tuple = resolve(field);
        LambdaUtils.setGenericIfValid(getEntityType(), tuple);

        return tuple;
    }

    /**
     * Resolves entity field and operates on it by passed expression
     * 
     * @param field
     * @param expression
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for passed
     *         lambda function
     */
    default QueryTuple resolveAndOperate(Serializable field, Consumer<QueryTuple> expression) {

        QueryTuple tuple = compose(field);
        operate(tuple, expression);

        return tuple;
    }

    /**
     * Resolves entity field and operates on it with passed parameter by passed
     * expression
     * 
     * @param field
     * @param value
     * @param expression
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for passed
     *         lambda function
     */
    default <V> QueryTuple resolveAndOperate(Serializable field, V value, BiConsumer<QueryTuple, V> expression) {

        QueryTuple tuple = compose(field);
        operate(tuple, value, expression);

        return tuple;
    }
}
