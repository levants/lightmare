package org.lightmare.criteria.query.internal.jpa;

/**
 * Processes functional expressions in JPA query
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface FunctionExpression<T>
        extends FuntionToObjectExpression<T>, FunctionToFunctionExpression<T>, FuntionToColumnExpression<T> {

}
