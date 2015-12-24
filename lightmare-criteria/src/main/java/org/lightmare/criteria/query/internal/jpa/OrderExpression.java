package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Orders;

/**
 * Query expressions for ORDER BY clauses
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface OrderExpression<T> {

    // =========================order=by=====================================//

    /**
     * Generates ORDER BY part for field
     * 
     * @param dir
     * @param field
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> order(String dir, EntityField<T, F> field);

    /**
     * Generates ORDER BY part for field
     * 
     * @param field
     * @return {@link QueryStream} current instance
     */
    default <F> QueryStream<T> orderBy(EntityField<T, F> field) {
        return order(null, field);
    }

    /**
     * Generates ORDER BY with DESC for field
     * 
     * @param field
     * @return {@link QueryStream} current instance
     */
    default <F> QueryStream<T> orderByDesc(EntityField<T, F> field) {
        return order(Orders.DESC, field);
    }
}
