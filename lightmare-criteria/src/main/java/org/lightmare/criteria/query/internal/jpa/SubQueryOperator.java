package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryStream;

/**
 * Provides method to process sub queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface SubQueryOperator<T> extends SubQuery<T> {

    /**
     * Processes sub query for entity field instant operator
     * 
     * @param field
     * @param operator
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> operateSubQuery(EntityField<T, F> field, String operator, Class<S> subType,
            QueryConsumer<S> consumer);
}
