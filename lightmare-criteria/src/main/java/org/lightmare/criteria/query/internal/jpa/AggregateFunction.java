/*
 * Lightmare-criteria, JPA-QL query generator using lambda expressions
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.GroupByConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Aggregates;

/**
 * Aggregate functions for JPA query language
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface AggregateFunction<T> {

    <F> QueryStream<Object[]> aggregate(EntityField<T, F> field, Aggregates function, GroupByConsumer<T> consumer);

    <F, R extends Number> QueryStream<R> aggregate(EntityField<T, F> field, Aggregates function, Class<R> type);

    <N extends Number> QueryStream<N> aggregate(EntityField<T, N> field, Aggregates function);

    /**
     * Create an aggregate expression applying the AVG operation.
     *
     * @param field
     *
     * @return AVG expression
     */
    default <N extends Number> QueryStream<Double> avg(EntityField<T, N> field) {
        return aggregate(field, Aggregates.AVG, Double.class);
    }

    default <N extends Number> QueryStream<Object[]> avg(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.AVG, consumer);
    }

    /**
     * Create an aggregate expression applying the SUM operation.
     *
     * @param x
     *            expression representing input value to sum operation
     *
     * @return sum expression
     */
    default <N extends Number> QueryStream<N> sum(EntityField<T, N> field) {
        return aggregate(field, Aggregates.SUM);
    }

    default <N extends Number> QueryStream<Object[]> sum(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.SUM, consumer);
    }

    /**
     * Create an aggregate expression applying the numerical max operation.
     *
     * @param x
     *            expression representing input value to max operation
     *
     * @return max expression
     */
    default <N extends Number> QueryStream<N> max(EntityField<T, N> field) {
        return aggregate(field, Aggregates.MAX);
    }

    default <N extends Number> QueryStream<Object[]> max(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.MAX, consumer);
    }

    /**
     * Create an aggregate expression applying the numerical min operation.
     *
     * @param x
     *            expression representing input value to min operation
     *
     * @return min expression
     */
    default <N extends Number> QueryStream<N> min(EntityField<T, N> field) {
        return aggregate(field, Aggregates.MIN);
    }

    default <N extends Number> QueryStream<Object[]> min(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.MIN, consumer);
    }

    /**
     * Create an aggregate expression for finding the greatest of the values
     * (strings, dates, etc).
     *
     * @param x
     *            expression representing input value to greatest operation
     *
     * @return greatest expression
     */
    default <N extends Number> QueryStream<N> greatest(EntityField<T, N> field) {
        return aggregate(field, Aggregates.GREATEST);
    }

    default <N extends Number> QueryStream<Object[]> greatest(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.GREATEST, consumer);
    }

    /**
     * Create an aggregate expression for finding the least of the values
     * (strings, dates, etc).
     *
     * @param x
     *            expression representing input value to least operation
     *
     * @return least expression
     */
    default <N extends Number> QueryStream<N> least(EntityField<T, N> field) {
        return aggregate(field, Aggregates.LEAST);
    }

    default <N extends Number> QueryStream<Object[]> least(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.LEAST, consumer);
    }

    /**
     * Create an aggregate expression applying the count operation.
     *
     * @param x
     *            expression representing input value to count operation
     *
     * @return count expression
     */
    default <F> QueryStream<Long> count(EntityField<T, F> field) {
        return aggregate(field, Aggregates.COUNT, Long.class);
    }

    /**
     * Create an aggregate expression applying the count distinct operation.
     *
     * @param x
     *            expression representing input value to count distinct
     *            operation
     *
     * @return count distinct expression
     */
    default <F> QueryStream<Long> countDistinct(EntityField<T, F> field) {
        return aggregate(field, Aggregates.COUNT, Long.class);
    }

    default <F> QueryStream<Object[]> countDistinct(EntityField<T, F> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.COUNT_DISTINCT, consumer);
    }

    default <F> QueryStream<Object[]> count(EntityField<T, F> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.COUNT, consumer);
    }
}
