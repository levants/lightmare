package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.subqueries.SubQueryStream;

/**
 * Processes sub queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface GeneralSubQueryProcessor<T> {

    Class<T> getEntityType();

    // =========================sub=queries==================================//

    /**
     * Generates {@link SubQueryStream} for S type
     * 
     * @param subType
     * @param consumer
     * @return {@link SubQueryStream} similar stream for sub query
     */
    <S> QueryStream<T> subQuery(Class<S> subType, SubQueryConsumer<S, T> consumer);

    /**
     * Generates {@link SubQueryStream} for S type without conditions
     * 
     * @param consumer
     * @return {@link SubQueryStream} similar stream for sub query
     */
    default QueryStream<T> subQuery(SubQueryConsumer<T, T> consumer) {
        return subQuery(getEntityType(), consumer);
    }

    /**
     * Generates sub query for IN clause
     * 
     * @param field
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> in(EntityField<T, F> field, Class<S> subType, SubQueryConsumer<S, T> consumer);

    /**
     * Generates sub query part for NOT IN clause
     * 
     * @param field
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> notIn(EntityField<T, F> field, Class<S> subType, SubQueryConsumer<S, T> consumer);

    default <F, S> QueryStream<T> in(EntityField<T, F> field, Class<S> subType) {
        return in(field, subType, null);
    }

    default <F, S> QueryStream<T> notIn(EntityField<T, F> field, Class<S> subType) {
        return notIn(field, subType, null);
    }

    default <F> QueryStream<T> in(EntityField<T, F> field, SubQueryConsumer<T, T> consumer) {
        return in(field, getEntityType(), consumer);
    }

    default <F> QueryStream<T> notIn(EntityField<T, F> field, SubQueryConsumer<T, T> consumer) {
        return notIn(field, getEntityType(), consumer);
    }

    default <F> QueryStream<T> in(EntityField<T, F> field) {
        SubQueryConsumer<T, T> consumer = null;
        return in(field, consumer);
    }

    default <F> QueryStream<T> notIn(EntityField<T, F> field) {
        SubQueryConsumer<T, T> consumer = null;
        return notIn(field, consumer);
    }

    /**
     * Generates sub query part for EXISTS clause
     * 
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> exists(Class<S> subType, SubQueryConsumer<S, T> consumer);

    /**
     * Generates sub query part for NOT EXISTS clause
     * 
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> notExists(Class<S> subType, SubQueryConsumer<S, T> consumer);

    default <F, S> QueryStream<T> exists(Class<S> subType) {
        return exists(subType, null);
    }

    default <F, S> QueryStream<T> notExists(Class<S> subType) {
        return notExists(subType, null);
    }

    default <F> QueryStream<T> exists(SubQueryConsumer<T, T> consumer) {
        return exists(getEntityType(), consumer);
    }

    default <F> QueryStream<T> notExists(SubQueryConsumer<T, T> consumer) {
        return notExists(getEntityType(), consumer);
    }

    // =========================sub=queries==================================//
}
