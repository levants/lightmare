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
 * Aggregate functions for JPA query
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface AggregateFunction<T> {

    /**
     * Operates with aggregate function and generates {@link QueryStream} for
     * {@link Object} array as result
     * 
     * @param field
     * @param function
     * @param consumer
     * @return {@link QueryStream} with grouping
     */
    <F> QueryStream<Object[]> aggregate(EntityField<T, F> field, Aggregates function, GroupByConsumer<T> consumer);

    /**
     * Operates with aggregate function and generates {@link QueryStream} for
     * instant result type
     * 
     * @param field
     * @param function
     * @param type
     * @return {@link QueryStream} with instant result type
     */
    <F, R extends Number> QueryStream<R> aggregate(EntityField<T, F> field, Aggregates function, Class<R> type);

    /**
     * Operates with aggregate function and generates {@link QueryStream} for
     * {@link Number} result type
     * 
     * @param field
     * @param function
     * @return {@link QueryStream} with {@link Number} result type
     */
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

    /**
     * Create an aggregate expression applying the AVG operation and group by
     * clause
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} with {@link Number} result type
     */
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

    /**
     * Create an aggregate expression applying the numerical SUM operation and
     * group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} for {@link Object}[]
     */
    default <N extends Number> QueryStream<Object[]> sum(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.SUM, consumer);
    }

    /**
     * Create an aggregate expression applying the numerical MAX operation.
     *
     * @param x
     *            expression representing input value to max operation
     *
     * @return max expression
     */
    default <N extends Number> QueryStream<N> max(EntityField<T, N> field) {
        return aggregate(field, Aggregates.MAX);
    }

    /**
     * Create an aggregate expression applying the numerical MAX operation and
     * group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} for {@link Object}[]
     */
    default <N extends Number> QueryStream<Object[]> max(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.MAX, consumer);
    }

    /**
     * Create an aggregate expression applying the numerical MIN operation.
     *
     * @param x
     *            expression representing input value to MIN operation
     *
     * @return min expression
     */
    default <N extends Number> QueryStream<N> min(EntityField<T, N> field) {
        return aggregate(field, Aggregates.MIN);
    }

    /**
     * Create an aggregate expression applying the numerical MIN operation and
     * group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} for {@link Object}[]
     */
    default <N extends Number> QueryStream<Object[]> min(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.MIN, consumer);
    }

    /**
     * Create an aggregate expression for finding the GREATEST of the values
     * (strings, dates, etc).
     *
     * @param x
     *            expression representing input value to GREATEST operation
     *
     * @return greatest expression
     */
    default <N extends Number> QueryStream<N> greatest(EntityField<T, N> field) {
        return aggregate(field, Aggregates.GREATEST);
    }

    /**
     * Create an aggregate expression for finding the GREATEST of the values
     * (strings, dates, etc) and group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} for {@link Object}[]
     */
    default <N extends Number> QueryStream<Object[]> greatest(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.GREATEST, consumer);
    }

    /**
     * Create an aggregate expression for finding the LEAST of the values
     * (strings, dates, etc).
     *
     * @param x
     *            expression representing input value to LEAST operation
     *
     * @return least expression
     */
    default <N extends Number> QueryStream<N> least(EntityField<T, N> field) {
        return aggregate(field, Aggregates.LEAST);
    }

    /**
     * Create an aggregate expression for finding the LEAST of the values
     * (strings, dates, etc) and group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} for {@link Object}[]
     */
    default <N extends Number> QueryStream<Object[]> least(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.LEAST, consumer);
    }

    /**
     * Create an aggregate expression applying the COUNT operation.
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
     * Create an aggregate expression applying the COUNT operation and group by
     * clause
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} for {@link Object}[]
     */
    default <F> QueryStream<Object[]> count(EntityField<T, F> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.COUNT, consumer);
    }

    /**
     * Create an aggregate expression applying the COUNT DISTINCT operation.
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

    /**
     * Create an aggregate expression applying the COUNT DISTINCT operation and
     * group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} for {@link Object}[]
     */
    default <F> QueryStream<Object[]> countDistinct(EntityField<T, F> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.COUNT_DISTINCT, consumer);
    }
}
