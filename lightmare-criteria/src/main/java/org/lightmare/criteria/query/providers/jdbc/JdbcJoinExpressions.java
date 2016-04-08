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
package org.lightmare.criteria.query.providers.jdbc;

import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.JoinOperator;

/**
 * Interface for [INNER, LEFT, RIGHT, FULL, CROSS] JOIN implementations
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
interface JdbcJoinExpressions<T, Q extends QueryStream<T, ? super Q>> extends JoinOperator<T, Q> {

    /**
     * Method for INNER JOIN function call on other entity
     * 
     * @param joinType
     * @param on
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E> Q innerJoin(Class<E> joinType, QueryConsumer<E, JdbcQueryStream<E>> on,
            QueryConsumer<E, JdbcQueryStream<E>> consumer) {
        Q stream = procesJoin(joinType, JdbcJoins.INNER, on, consumer);
        return stream;
    }

    /**
     * Method for INNER JOIN function call on other entity without conditions
     * 
     * @param joinType
     * @param on
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E> Q innerJoin(Class<E> joinType, QueryConsumer<E, JdbcQueryStream<E>> on) {
        return innerJoin(joinType, on, null);
    }

    /**
     * Method for LEFT OUTER JOIN function call on other entity
     * 
     * @param joinType
     * @param on
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E> Q leftJoin(Class<E> joinType, QueryConsumer<E, JdbcQueryStream<E>> on,
            QueryConsumer<E, JdbcQueryStream<E>> consumer) {
        Q stream = procesJoin(joinType, JdbcJoins.LEFT, on, consumer);
        return stream;
    }

    /**
     * Method for LEFT OUTER JOIN function call on other entity without
     * conditions
     * 
     * @param joinType
     * @param on
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E> Q leftJoin(Class<E> joinType, QueryConsumer<E, JdbcQueryStream<E>> on) {
        return leftJoin(joinType, on, null);
    }

    /**
     * Method for RIGHT OUTER JOIN function call on other entity
     * 
     * @param joinType
     * @param on
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E> Q rightJoin(Class<E> joinType, QueryConsumer<E, JdbcQueryStream<E>> on,
            QueryConsumer<E, JdbcQueryStream<E>> consumer) {
        Q stream = procesJoin(joinType, JdbcJoins.RIGHT, on, consumer);
        return stream;
    }

    /**
     * Method for RIGHT OUTER JOIN function call on other entity without
     * conditions
     * 
     * @param joinType
     * @param on
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E> Q rightJoin(Class<E> joinType, QueryConsumer<E, JdbcQueryStream<E>> on) {
        return rightJoin(joinType, on, null);
    }

    /**
     * Method for FULL OUTER JOIN function call on other entity
     * 
     * @param joinType
     * @param on
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E> Q fullJoin(Class<E> joinType, QueryConsumer<E, JdbcQueryStream<E>> on,
            QueryConsumer<E, JdbcQueryStream<E>> consumer) {
        Q stream = procesJoin(joinType, JdbcJoins.FULL, on, consumer);
        return stream;
    }

    /**
     * Method for FULL OUTER JOIN function call on other entity without
     * conditions
     * 
     * @param joinType
     * @param on
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E> Q fullJoin(Class<E> joinType, QueryConsumer<E, JdbcQueryStream<E>> on) {
        return fullJoin(joinType, on, null);
    }

    /**
     * Method for CROSS JOIN function call on other entity without conditions
     * 
     * @param joinType
     * @param on
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E> Q crossJoin(Class<E> joinType, QueryConsumer<E, JdbcQueryStream<E>> on) {
        Q stream = procesJoin(joinType, JdbcJoins.CROSS, null);
        return stream;
    }
}
