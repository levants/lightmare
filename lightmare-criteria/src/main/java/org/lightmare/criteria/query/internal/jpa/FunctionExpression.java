package org.lightmare.criteria.query.internal.jpa;

/**
 * Processes functional expressions in JPA query
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface FunctionExpression<T> extends F2OExpression<T>, F2FExpression<T> {

}
