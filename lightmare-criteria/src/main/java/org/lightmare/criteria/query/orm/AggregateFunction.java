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
package org.lightmare.criteria.query.orm;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.GroupByConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.orm.links.Aggregates;

/**
 * Aggregate functions for JPA query
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
public interface AggregateFunction<T, Q extends QueryStream<Object[], ?>> {

    /**
     * Operates with aggregate function and generates
     * {@link org.lightmare.criteria.query.providers.jpa.JpaQueryStream} for
     * {@link Object} array as result
     * 
     * @param field
     * @param function
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    <F> Q aggregate(EntityField<T, F> field, Aggregates function, GroupByConsumer<T, Q> consumer);

    /**
     * Operates with aggregate function and generates
     * {@link org.lightmare.criteria.query.providers.jpa.JpaQueryStream} for instant
     * result type
     * 
     * @param field
     * @param function
     * @param type
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     *         for numeric result type
     */
    <F, R extends Number, L extends QueryStream<R, ? super L>> L aggregate(EntityField<T, F> field, Aggregates function,
            Class<R> type);

    /**
     * Operates with aggregate function and generates
     * {@link org.lightmare.criteria.query.providers.jpa.JpaQueryStream} for
     * {@link Number} result type
     * 
     * @param field
     * @param function
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     *         for numeric result type
     */
    <N extends Number, L extends QueryStream<N, ? super L>> L aggregate(EntityField<T, N> field, Aggregates function);

    /**
     * Create an aggregate expression applying the AVG operation.
     *
     * @param field
     *
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     *         for numeric result type
     */
    default <N extends Number, L extends QueryStream<Double, ? super L>> L avg(EntityField<T, N> field) {
        return aggregate(field, Aggregates.AVG, Double.class);
    }

    /**
     * Create an aggregate expression applying the AVG operation and group by
     * clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <N extends Number> Q avg(EntityField<T, N> field, GroupByConsumer<T, Q> consumer) {
        return aggregate(field, Aggregates.AVG, consumer);
    }

    /**
     * Create an aggregate expression applying the SUM operation.
     *
     * @param field
     *            expression representing input value to sum operation
     *
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     *         for numeric result type
     */
    default <N extends Number, L extends QueryStream<N, ? super L>> L sum(EntityField<T, N> field) {
        return aggregate(field, Aggregates.SUM);
    }

    /**
     * Create an aggregate expression applying the numerical SUM operation and
     * group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <N extends Number> Q sum(EntityField<T, N> field, GroupByConsumer<T, Q> consumer) {
        return aggregate(field, Aggregates.SUM, consumer);
    }

    /**
     * Create an aggregate expression applying the numerical MAX operation.
     *
     * @param field
     *            expression representing input value to max operation
     *
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     *         for numeric result type
     */
    default <N extends Number, L extends QueryStream<N, ? super L>> L max(EntityField<T, N> field) {
        return aggregate(field, Aggregates.MAX);
    }

    /**
     * Create an aggregate expression applying the numerical MAX operation and
     * group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <N extends Number> Q max(EntityField<T, N> field, GroupByConsumer<T, Q> consumer) {
        return aggregate(field, Aggregates.MAX, consumer);
    }

    /**
     * Create an aggregate expression applying the numerical MIN operation.
     *
     * @param field
     *            expression representing input value to MIN operation
     *
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     *         for numeric result type
     */
    default <N extends Number, L extends QueryStream<N, ? super L>> L min(EntityField<T, N> field) {
        return aggregate(field, Aggregates.MIN);
    }

    /**
     * Create an aggregate expression applying the numerical MIN operation and
     * group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <N extends Number> Q min(EntityField<T, N> field, GroupByConsumer<T, Q> consumer) {
        return aggregate(field, Aggregates.MIN, consumer);
    }

    /**
     * Create an aggregate expression for finding the GREATEST of the values
     * (strings, dates, etc).
     *
     * @param field
     *            expression representing input value to GREATEST operation
     *
     * @return {{@link org.lightmare.criteria.query.QueryStream} implementation
     *         for numeric result type greatest expression
     */
    default <N extends Number, L extends QueryStream<N, ? super L>> L greatest(EntityField<T, N> field) {
        return aggregate(field, Aggregates.GREATEST);
    }

    /**
     * Create an aggregate expression for finding the GREATEST of the values
     * (strings, dates, etc) and group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <N extends Number> Q greatest(EntityField<T, N> field, GroupByConsumer<T, Q> consumer) {
        return aggregate(field, Aggregates.GREATEST, consumer);
    }

    /**
     * Create an aggregate expression for finding the LEAST of the values
     * (strings, dates, etc).
     *
     * @param field
     *            expression representing input value to LEAST operation
     *
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     *         for numeric result type least expression
     */
    default <N extends Number, L extends QueryStream<N, ? super L>> L least(EntityField<T, N> field) {
        return aggregate(field, Aggregates.LEAST);
    }

    /**
     * Create an aggregate expression for finding the LEAST of the values
     * (strings, dates, etc) and group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <N extends Number> Q least(EntityField<T, N> field, GroupByConsumer<T, Q> consumer) {
        return aggregate(field, Aggregates.LEAST, consumer);
    }

    /**
     * Create an aggregate expression applying the COUNT operation.
     *
     * @param field
     *            expression representing input value to count operation
     *
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     *         for numeric result type count expression
     */
    default <F, L extends QueryStream<Long, ? super L>> L count(EntityField<T, F> field) {
        return aggregate(field, Aggregates.COUNT, Long.class);
    }

    /**
     * Create an aggregate expression applying the COUNT operation and group by
     * clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <F> Q count(EntityField<T, F> field, GroupByConsumer<T, Q> consumer) {
        return aggregate(field, Aggregates.COUNT, consumer);
    }

    /**
     * Create an aggregate expression applying the COUNT DISTINCT operation.
     *
     * @param field
     *            expression representing input value to count distinct
     *            operation
     *
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     *         for numeric result type count distinct expression
     */
    default <F, L extends QueryStream<Long, ? super L>> L countDistinct(EntityField<T, F> field) {
        return aggregate(field, Aggregates.COUNT, Long.class);
    }

    /**
     * Create an aggregate expression applying the COUNT DISTINCT operation and
     * group by clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <F> Q countDistinct(EntityField<T, F> field, GroupByConsumer<T, Q> consumer) {
        return aggregate(field, Aggregates.COUNT_DISTINCT, consumer);
    }
}
