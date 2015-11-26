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
interface SubQueryOperator<T> {

    <F, S> QueryStream<T> operateSubQuery(EntityField<T, F> field, String operator, Class<S> subType,
            QueryConsumer<S> consumer);
}
