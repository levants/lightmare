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
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.links.Operators;

/**
 * Processes sub queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface SubQueryProcessor<T>
        extends TypedSubQueryProcessor<T>, TypedToObjectSubQueryProcessor<T>, TypedToFunctionSubQueryProcessor<T> {

    Class<T> getEntityType();

    // =========================sub=queries==================================//

    /**
     * Generates {@link org.lightmare.criteria.query.QueryStream} for S type
     * 
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} similar stream
     *         for sub query
     */
    <S> QueryStream<T> operateSubQuery(Class<S> type, QueryConsumer<S> consumer);

    /**
     * Generates {@link org.lightmare.criteria.query.QueryStream} for S type
     * without conditions
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} similar stream
     *         for sub query
     */
    default QueryStream<T> subQuery(QueryConsumer<T> consumer) {
        return operateSubQuery(getEntityType(), consumer);
    }

    <F, S> QueryStream<T> operateSubQuery(String operator, Class<S> type, QueryConsumer<S> consumer);

    /**
     * Generates sub query for IN clause
     * 
     * @param field
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} current instance
     */
    default <F, S> QueryStream<T> in(EntityField<T, F> field, Class<S> type, QueryConsumer<S> consumer) {
        return operateSubQuery(field, Operators.IN, type, consumer);
    }

    /**
     * Generates sub query part for NOT IN clause
     * 
     * @param field
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} current instance
     */
    default <F, S> QueryStream<T> notIn(EntityField<T, F> field, Class<S> type, QueryConsumer<S> consumer) {
        return operateSubQuery(field, Operators.NOT_IN, type, consumer);
    }

    default <F, S> QueryStream<T> in(EntityField<T, F> field, Class<S> type) {
        return in(field, type, null);
    }

    default <F, S> QueryStream<T> notIn(EntityField<T, F> field, Class<S> type) {
        return notIn(field, type, null);
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
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} current instance
     */
    default <F, S> QueryStream<T> exists(Class<S> type, QueryConsumer<S> consumer) {
        return operateSubQuery(Operators.EXISTS, type, consumer);
    }

    /**
     * Generates sub query part for NOT EXISTS clause
     * 
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} current instance
     */
    default <F, S> QueryStream<T> notExists(Class<S> type, QueryConsumer<S> consumer) {
        return operateSubQuery(Operators.NOT_EXISTS, type, consumer);
    }

    default <F, S> QueryStream<T> exists(Class<S> type) {
        return exists(type, null);
    }

    default <F, S> QueryStream<T> notExists(Class<S> type) {
        return notExists(type, null);
    }

    default <F> QueryStream<T> exists(QueryConsumer<T> consumer) {
        return exists(getEntityType(), consumer);
    }

    default <F> QueryStream<T> notExists(QueryConsumer<T> consumer) {
        return notExists(getEntityType(), consumer);
    }

    // =========================sub=queries==================================//
}
