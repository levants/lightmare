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

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.orm.links.Operators;
import org.lightmare.criteria.query.providers.jpa.JpaQueryStream;

/**
 * Processes sub queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * 
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
interface JdbcSubQueryProcessor<T, Q extends QueryStream<T, ? super Q>> extends JdbcTypedSubQueryProcessor<T, Q>,
        JdbcTypedToObjectSubQueryProcessor<T, Q>, JdbcTypedToFunctionSubQueryProcessor<T, Q> {

    Class<T> getEntityType();

    /**
     * Generates {@link org.lightmare.criteria.query.providers.jpa.JpaQueryStream}
     * for S type without conditions
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     *         similar stream for sub query
     */
    default Q subQuery(QueryConsumer<T, JpaQueryStream<T>> consumer) {
        return operateSubQuery(getEntityType(), consumer);
    }

    /**
     * Generates sub query for IN clause
     * 
     * @param field
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <F, S> Q in(EntityField<T, F> field, Class<S> type, QueryConsumer<S, JdbcQueryStream<S>> consumer) {
        return operateSubQuery(field, Operators.IN, type, consumer);
    }

    /**
     * Generates sub query part for NOT IN clause
     * 
     * @param field
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <F, S> Q notIn(EntityField<T, F> field, Class<S> type, QueryConsumer<S, JdbcQueryStream<S>> consumer) {
        return operateSubQuery(field, Operators.NOT_IN, type, consumer);
    }

    default <F, S> Q in(EntityField<T, F> field, Class<S> type) {
        return in(field, type, null);
    }

    default <F, S> Q notIn(EntityField<T, F> field, Class<S> type) {
        return notIn(field, type, null);
    }

    default <F> Q in(EntityField<T, F> field, QueryConsumer<T, JdbcQueryStream<T>> consumer) {
        return in(field, getEntityType(), consumer);
    }

    default <F> Q notIn(EntityField<T, F> field, QueryConsumer<T, JdbcQueryStream<T>> consumer) {
        return notIn(field, getEntityType(), consumer);
    }

    default <F> Q in(EntityField<T, F> field) {
        QueryConsumer<T, JdbcQueryStream<T>> consumer = null;
        return in(field, consumer);
    }

    default <F> Q notIn(EntityField<T, F> field) {
        QueryConsumer<T, JdbcQueryStream<T>> consumer = null;
        return notIn(field, consumer);
    }

    /**
     * Generates sub query part for EXISTS clause
     * 
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <F, S> Q exists(Class<S> type, QueryConsumer<S, JdbcQueryStream<S>> consumer) {
        return operateSubQuery(Operators.EXISTS, type, consumer);
    }

    /**
     * Generates sub query part for NOT EXISTS clause
     * 
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <F, S> Q notExists(Class<S> type, QueryConsumer<S, JdbcQueryStream<S>> consumer) {
        return operateSubQuery(Operators.NOT_EXISTS, type, consumer);
    }

    default <F, S> Q exists(Class<S> type) {
        return exists(type, null);
    }

    default <F, S> Q notExists(Class<S> type) {
        return notExists(type, null);
    }

    default <F> Q exists(QueryConsumer<T, JdbcQueryStream<T>> consumer) {
        return exists(getEntityType(), consumer);
    }

    default <F> Q notExists(QueryConsumer<T, JdbcQueryStream<T>> consumer) {
        return notExists(getEntityType(), consumer);
    }
}
