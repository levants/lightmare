package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryStream;

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
     * Generates {@link QueryStream} for S type
     * 
     * @param subType
     * @param consumer
     * @return {@link QueryStream} similar stream for sub query
     */
    <S> QueryStream<T> subQuery(Class<S> subType, QueryConsumer<S> consumer);

    /**
     * Generates {@link QueryStream} for S type without conditions
     * 
     * @param consumer
     * @return {@link QueryStream} similar stream for sub query
     */
    default QueryStream<T> subQuery(QueryConsumer<T> consumer) {
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
    <F, S> QueryStream<T> in(EntityField<T, F> field, Class<S> subType, QueryConsumer<S> consumer);

    /**
     * Generates sub query part for NOT IN clause
     * 
     * @param field
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> notIn(EntityField<T, F> field, Class<S> subType, QueryConsumer<S> consumer);

    default <F, S> QueryStream<T> in(EntityField<T, F> field, Class<S> subType) {
        return in(field, subType, null);
    }

    default <F, S> QueryStream<T> notIn(EntityField<T, F> field, Class<S> subType) {
        return notIn(field, subType, null);
    }

    default <F> QueryStream<T> in(EntityField<T, F> field, QueryConsumer<T> consumer) {
        return in(field, getEntityType(), consumer);
    }

    default <F> QueryStream<T> notIn(EntityField<T, F> field, QueryConsumer<T> consumer) {
        return notIn(field, getEntityType(), consumer);
    }

    default <F> QueryStream<T> in(EntityField<T, F> field) {
        QueryConsumer<T> consumer = null;
        return in(field, consumer);
    }

    default <F> QueryStream<T> notIn(EntityField<T, F> field) {
        QueryConsumer<T> consumer = null;
        return notIn(field, consumer);
    }

    /**
     * Generates sub query part for EXISTS clause
     * 
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> exists(Class<S> subType, QueryConsumer<S> consumer);

    /**
     * Generates sub query part for NOT EXISTS clause
     * 
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> notExists(Class<S> subType, QueryConsumer<S> consumer);

    default <F, S> QueryStream<T> exists(Class<S> subType) {
        return exists(subType, null);
    }

    default <F, S> QueryStream<T> notExists(Class<S> subType) {
        return notExists(subType, null);
    }

    default <F> QueryStream<T> exists(QueryConsumer<T> consumer) {
        return exists(getEntityType(), consumer);
    }

    default <F> QueryStream<T> notExists(QueryConsumer<T> consumer) {
        return notExists(getEntityType(), consumer);
    }

    // =========================sub=queries==================================//
}
