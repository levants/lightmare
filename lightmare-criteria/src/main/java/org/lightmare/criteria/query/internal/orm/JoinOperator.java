package org.lightmare.criteria.query.internal.orm;

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.query.QueryStream;

/**
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
public interface JoinOperator<T, Q extends QueryStream<T, ? super Q>> {

    /**
     * Processes JOIN statement with ON clause
     * 
     * @param field
     * @param expression
     * @param on
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    <E, C extends Collection<E>, S extends LambdaStream<E, ? super S>> Q procesJoin(EntityField<T, C> field,
            String expression, QueryConsumer<E, S> on, QueryConsumer<E, S> consumer);

    /**
     * Processes JOIN statement
     * 
     * @param field
     * @param expression
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>, S extends LambdaStream<E, ? super S>> Q procesJoin(EntityField<T, C> field,
            String expression, QueryConsumer<E, S> consumer) {
        return procesJoin(field, expression, null, consumer);
    }
}
