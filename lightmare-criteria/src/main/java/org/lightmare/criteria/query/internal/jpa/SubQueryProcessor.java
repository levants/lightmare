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
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Operators;

/**
 * Processes sub queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface SubQueryProcessor<T> extends AnySubQueryProcessor<T>, AllSubQueryProcessor<T>,
        AllToObjectSubQueryProcessor<T>, AllToFunctionSubQueryProcessor<T>, SomeSubQueryProcessor<T> {

    Class<T> getEntityType();

    // =========================sub=queries==================================//

    /**
     * Generates {@link QueryStream} for S type
     * 
     * @param subType
     * @param consumer
     * @return {@link QueryStream} similar stream for sub query
     */
    <S> QueryStream<T> operateSubQuery(Class<S> subType, QueryConsumer<S> consumer);

    /**
     * Generates {@link QueryStream} for S type without conditions
     * 
     * @param consumer
     * @return {@link QueryStream} similar stream for sub query
     */
    default QueryStream<T> subQuery(QueryConsumer<T> consumer) {
        return operateSubQuery(getEntityType(), consumer);
    }

    <F, S> QueryStream<T> operateSubQuery(String operator, Class<S> subType, QueryConsumer<S> consumer);

    /**
     * Generates sub query for IN clause
     * 
     * @param field
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    default <F, S> QueryStream<T> in(EntityField<T, F> field, Class<S> subType, QueryConsumer<S> consumer) {
        return operateSubQuery(field, Operators.IN, subType, consumer);
    }

    /**
     * Generates sub query part for NOT IN clause
     * 
     * @param field
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    default <F, S> QueryStream<T> notIn(EntityField<T, F> field, Class<S> subType, QueryConsumer<S> consumer) {
        return operateSubQuery(field, Operators.NOT_IN, subType, consumer);
    }

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
    default <F, S> QueryStream<T> exists(Class<S> subType, QueryConsumer<S> consumer) {
        return operateSubQuery(Operators.EXISTS, subType, consumer);
    }

    /**
     * Generates sub query part for NOT EXISTS clause
     * 
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    default <F, S> QueryStream<T> notExists(Class<S> subType, QueryConsumer<S> consumer) {
        return operateSubQuery(Operators.NOT_EXISTS, subType, consumer);
    }

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
