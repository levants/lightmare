package org.lightmare.criteria.query;

import java.io.Serializable;

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
    void operate(QueryTuple tuple, String expression);

    /**
     * Operates on resolved field by expression with parameter
     * 
     * @param tuple
     * @param expression
     * @param value
     */
    void operate(QueryTuple tuple, String expression, Object value);

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
    default QueryTuple resolveAndOperate(Serializable field, String expression) {

        QueryTuple tuple = compose(field);
        operate(tuple, expression);

        return tuple;
    }

    /**
     * Resolves entity field and operates on it by passed expression and
     * parameter
     * 
     * @param field
     * @param expression
     * @param value
     * @return @link org.lightmare.criteria.tuples.QueryTuple} for passed lambda
     *         function
     */
    default QueryTuple resolveAndOperate(Serializable field, String expression, Object value) {

        QueryTuple tuple = compose(field);
        operate(tuple, expression, value);

        return tuple;
    }
}
