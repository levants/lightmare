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

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.links.Joins;
import org.lightmare.criteria.query.providers.JpaQueryStream;

/**
 * Interface for [INNER, LEFT, FETCH] JOIN implementations
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public interface JoinExpressions<T, Q extends QueryStream<T, ? super Q>> {

    /**
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

    /**
     * Method for INNER JOIN function call
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>> Q join(EntityField<T, C> field, QueryConsumer<E, JpaQueryStream<E>> consumer) {
        Q stream = procesJoin(field, Joins.JOIN, consumer);
        return stream;
    }

    /**
     * Method for INNER JOIN function call
     * 
     * @param field
     * @param on
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>> Q join(EntityField<T, C> field, QueryConsumer<E, JpaQueryStream<E>> on,
            QueryConsumer<E, JpaQueryStream<E>> consumer) {
        Q stream = procesJoin(field, Joins.JOIN, on, consumer);
        return stream;
    }

    /**
     * Method for INNER JOIN function call without conditions
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>> Q join(EntityField<T, C> field) {
        return join(field, null);
    }

    /**
     * Method for INNER JOIN function call without conditions
     * 
     * @param field
     * @param on
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>> Q joinOn(EntityField<T, C> field, QueryConsumer<E, JpaQueryStream<E>> on) {
        return join(field, on, null);
    }

    /**
     * Method for LEFT JOIN function call
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>> Q leftJoin(EntityField<T, C> field,
            QueryConsumer<E, JpaQueryStream<E>> consumer) {
        Q stream = procesJoin(field, Joins.LEFT, consumer);
        return stream;
    }

    /**
     * Method for LEFT JOIN function call
     * 
     * @param field
     * @param on
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>> Q leftJoin(EntityField<T, C> field, QueryConsumer<E, JpaQueryStream<E>> on,
            QueryConsumer<E, JpaQueryStream<E>> consumer) {
        Q stream = procesJoin(field, Joins.LEFT, on, consumer);
        return stream;
    }

    /**
     * Method for LEFT JOIN function call without conditions
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>> Q leftJoin(EntityField<T, C> field) {
        return leftJoin(field, null);
    }

    /**
     * Method for LEFT JOIN function call without conditions
     * 
     * @param field
     * @param on
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>> Q leftJoinOn(EntityField<T, C> field, QueryConsumer<E, JpaQueryStream<E>> on) {
        return leftJoin(field, on, null);
    }

    /**
     * Method for FETCH JOIN function call
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>> Q fetchJoin(EntityField<T, C> field,
            QueryConsumer<E, JpaQueryStream<E>> consumer) {
        Q stream = procesJoin(field, Joins.FETCH, consumer);
        return stream;
    }

    /**
     * Method for FETCH JOIN function call
     * 
     * @param field
     * @param on
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>> Q fetchJoin(EntityField<T, C> field, QueryConsumer<E, JpaQueryStream<E>> on,
            QueryConsumer<E, JpaQueryStream<E>> consumer) {
        Q stream = procesJoin(field, Joins.FETCH, on, consumer);
        return stream;
    }

    /**
     * Method for FETCH JOIN function call without conditions
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>> Q fetchJoin(EntityField<T, C> field) {
        return fetchJoin(field, null);
    }

    /**
     * Method for FETCH JOIN function call without conditions
     * 
     * @param field
     * @param on
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>> Q fetchJoinOn(EntityField<T, C> field,
            QueryConsumer<E, JpaQueryStream<E>> on) {
        return fetchJoin(field, null);
    }
}
