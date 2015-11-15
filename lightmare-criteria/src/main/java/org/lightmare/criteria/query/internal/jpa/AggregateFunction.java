package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.GroupByConsumer;
import org.lightmare.criteria.query.QueryStream;

/**
 * Aggregate functions for JPA query language
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface AggregateFunction<T> {

    /**
     * Create an aggregate expression applying the AVG operation.
     *
     * @param field
     *
     * @return AVG expression
     */
    <N extends Number> QueryStream<Double> avg(EntityField<T, N> field);

    /**
     * Create an aggregate expression applying the SUM operation.
     *
     * @param x
     *            expression representing input value to sum operation
     *
     * @return sum expression
     */
    <N extends Number> QueryStream<N> sum(EntityField<T, N> field);

    /**
     * Create an aggregate expression applying the sum operation to an
     * Integer-valued expression, returning a Long result.
     *
     * @param x
     *            expression representing input value to sum operation
     *
     * @return sum expression
     */
    QueryStream<Long> sumAsLong(EntityField<T, Integer> field);

    /**
     * Create an aggregate expression applying the sum operation to a
     * Float-valued expression, returning a Double result.
     *
     * @param x
     *            expression representing input value to sum operation
     *
     * @return sum expression
     */
    QueryStream<Double> sumAsDouble(EntityField<T, Float> field);

    /**
     * Create an aggregate expression applying the numerical max operation.
     *
     * @param x
     *            expression representing input value to max operation
     *
     * @return max expression
     */
    <N extends Number> QueryStream<N> max(EntityField<T, N> field);

    /**
     * Create an aggregate expression applying the numerical min operation.
     *
     * @param x
     *            expression representing input value to min operation
     *
     * @return min expression
     */
    <N extends Number> QueryStream<N> min(EntityField<T, N> field);

    /**
     * Create an aggregate expression for finding the greatest of the values
     * (strings, dates, etc).
     *
     * @param x
     *            expression representing input value to greatest operation
     *
     * @return greatest expression
     */
    <C extends Comparable<? super C>> QueryStream<C> greatest(EntityField<T, C> field);

    /**
     * Create an aggregate expression for finding the least of the values
     * (strings, dates, etc).
     *
     * @param x
     *            expression representing input value to least operation
     *
     * @return least expression
     */
    <C extends Comparable<? super C>> QueryStream<C> least(EntityField<T, C> field);

    /**
     * Create an aggregate expression applying the count operation.
     *
     * @param x
     *            expression representing input value to count operation
     *
     * @return count expression
     */
    <F> QueryStream<Long> count(EntityField<T, F> field);

    QueryStream<T> count();

    /**
     * Create an aggregate expression applying the count distinct operation.
     *
     * @param x
     *            expression representing input value to count distinct
     *            operation
     *
     * @return count distinct expression
     */
    <F> QueryStream<Long> countDistinct(EntityField<T, F> field);

    <F> QueryStream<Object[]> count(EntityField<T, F> field, GroupByConsumer<T> consumer);
}
