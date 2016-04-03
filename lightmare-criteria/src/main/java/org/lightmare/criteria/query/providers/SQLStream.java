package org.lightmare.criteria.query.providers;

import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.QueryExpression;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryStream} and
 * {@link org.lightmare.criteria.query.internal.orm.QueryExpression} for SQL
 * queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 * @param <O>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
public interface SQLStream<T, Q extends QueryStream<T, ? super Q>, O extends QueryStream<Object[], ? super O>>
        extends QueryStream<T, Q>, QueryExpression<T, Q, O> {

}
