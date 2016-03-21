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
package org.lightmare.criteria.query.internal.orm;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.GroupByConsumer;
import org.lightmare.criteria.query.internal.orm.links.Aggregates;
import org.lightmare.criteria.query.providers.JpaQueryStream;

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
     * Operates with aggregate function and generates
     * {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     * {@link Object} array as result
     * 
     * @param field
     * @param function
     * @param consumer
     * @return {@link JpaQueryStream} with grouping
     */
    <F> JpaQueryStream<Object[]> aggregate(EntityField<T, F> field, Aggregates function, GroupByConsumer<T> consumer);

    /**
     * Operates with aggregate function and generates
     * {@link org.lightmare.criteria.query.providers.JpaQueryStream} for instant
     * result type
     * 
     * @param field
     * @param function
     * @param type
     * @return {@link JpaQueryStream} with instant result type
     */
    <F, R extends Number> JpaQueryStream<R> aggregate(EntityField<T, F> field, Aggregates function, Class<R> type);

    /**
     * Operates with aggregate function and generates
     * {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     * {@link Number} result type
     * 
     * @param field
     * @param function
     * @return {@link JpaQueryStream} with {@link Number} result type
     */
    <N extends Number> JpaQueryStream<N> aggregate(EntityField<T, N> field, Aggregates function);

    /**
     * Create an aggregate expression applying the AVG operation.
     *
     * @param field
     *
     * @return AVG expression
     */
    default <N extends Number> JpaQueryStream<Double> avg(EntityField<T, N> field) {
        return aggregate(field, Aggregates.AVG, Double.class);
    }

    /**
     * Create an aggregate expression applying the AVG operation and group by
     * clause
     * 
     * @param field
     * @param consumer
     * @return {@link JpaQueryStream} with {@link Number} result type
     */
    default <N extends Number> JpaQueryStream<Object[]> avg(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.AVG, consumer);
    }

    /**
     * Create an aggregate expression applying the SUM operation.
     *
     * @param field
     *            expression representing input value to sum operation
     *
     * @return sum expression
     */
    default <N extends Number> JpaQueryStream<N> sum(EntityField<T, N> field) {
        return aggregate(field, Aggregates.SUM);
    }

    /**
     * Create an aggregate expression applying the numerical SUM operation and
     * group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         {@link Object}[]
     */
    default <N extends Number> JpaQueryStream<Object[]> sum(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.SUM, consumer);
    }

    /**
     * Create an aggregate expression applying the numerical MAX operation.
     *
     * @param field
     *            expression representing input value to max operation
     *
     * @return max expression
     */
    default <N extends Number> JpaQueryStream<N> max(EntityField<T, N> field) {
        return aggregate(field, Aggregates.MAX);
    }

    /**
     * Create an aggregate expression applying the numerical MAX operation and
     * group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         {@link Object}[]
     */
    default <N extends Number> JpaQueryStream<Object[]> max(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.MAX, consumer);
    }

    /**
     * Create an aggregate expression applying the numerical MIN operation.
     *
     * @param field
     *            expression representing input value to MIN operation
     *
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         {@link Number}
     */
    default <N extends Number> JpaQueryStream<N> min(EntityField<T, N> field) {
        return aggregate(field, Aggregates.MIN);
    }

    /**
     * Create an aggregate expression applying the numerical MIN operation and
     * group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         {@link Object}[]
     */
    default <N extends Number> JpaQueryStream<Object[]> min(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.MIN, consumer);
    }

    /**
     * Create an aggregate expression for finding the GREATEST of the values
     * (strings, dates, etc).
     *
     * @param field
     *            expression representing input value to GREATEST operation
     *
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         greatest expression
     */
    default <N extends Number> JpaQueryStream<N> greatest(EntityField<T, N> field) {
        return aggregate(field, Aggregates.GREATEST);
    }

    /**
     * Create an aggregate expression for finding the GREATEST of the values
     * (strings, dates, etc) and group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         {@link Object}[]
     */
    default <N extends Number> JpaQueryStream<Object[]> greatest(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.GREATEST, consumer);
    }

    /**
     * Create an aggregate expression for finding the LEAST of the values
     * (strings, dates, etc).
     *
     * @param field
     *            expression representing input value to LEAST operation
     *
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         forleast expression
     */
    default <N extends Number> JpaQueryStream<N> least(EntityField<T, N> field) {
        return aggregate(field, Aggregates.LEAST);
    }

    /**
     * Create an aggregate expression for finding the LEAST of the values
     * (strings, dates, etc) and group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         {@link Object}[]
     */
    default <N extends Number> JpaQueryStream<Object[]> least(EntityField<T, N> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.LEAST, consumer);
    }

    /**
     * Create an aggregate expression applying the COUNT operation.
     *
     * @param field
     *            expression representing input value to count operation
     *
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         count expression
     */
    default <F> JpaQueryStream<Long> count(EntityField<T, F> field) {
        return aggregate(field, Aggregates.COUNT, Long.class);
    }

    /**
     * Create an aggregate expression applying the COUNT operation and group by
     * clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         {@link Object}[]
     */
    default <F> JpaQueryStream<Object[]> count(EntityField<T, F> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.COUNT, consumer);
    }

    /**
     * Create an aggregate expression applying the COUNT DISTINCT operation.
     *
     * @param field
     *            expression representing input value to count distinct
     *            operation
     *
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         count distinct expression
     */
    default <F> JpaQueryStream<Long> countDistinct(EntityField<T, F> field) {
        return aggregate(field, Aggregates.COUNT, Long.class);
    }

    /**
     * Create an aggregate expression applying the COUNT DISTINCT operation and
     * group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         {@link Object}[]
     */
    default <F> JpaQueryStream<Object[]> countDistinct(EntityField<T, F> field, GroupByConsumer<T> consumer) {
        return aggregate(field, Aggregates.COUNT_DISTINCT, consumer);
    }
}
