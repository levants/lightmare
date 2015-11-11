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
package org.lightmare.criteria.query;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.query.internal.GeneralQueryStream;
import org.lightmare.criteria.query.internal.jpa.FieldStream;
import org.lightmare.criteria.query.internal.jpa.FieldValueStream;
import org.lightmare.criteria.query.internal.jpa.subqueries.SubQueryStream;

/**
 * Main interface with query construction methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 */
public interface QueryStream<T> extends GeneralQueryStream<T>, FieldValueStream<T>, FieldStream<T> {

    /**
     * Generates query part for embedded entity fields
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> embedded(EntityField<T, F> field, SubQueryConsumer<F, T> consumer);

    // =========================sub=queries==================================//

    /**
     * Generates {@link SubQueryStream} for S type
     * 
     * @param subType
     * @param consumer
     * @return {@link SubQueryStream} similar stream for sub query
     */
    <S> QueryStream<T> subQuery(Class<S> subType, SubQueryConsumer<S, T> consumer);

    /**
     * Generates {@link SubQueryStream} for S type without conditions
     * 
     * @param consumer
     * @return {@link SubQueryStream} similar stream for sub query
     */
    default QueryStream<T> subQuery(SubQueryConsumer<T, T> consumer) {
        return subQuery(getEntityType(), consumer);
    }

    /**
     * Generates sub query for IN clause
     * 
     * @param field
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> in(EntityField<T, F> field, Class<S> subType, SubQueryConsumer<S, T> consumer);

    /**
     * Generates sub query part for NOT IN clause
     * 
     * @param field
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> notIn(EntityField<T, F> field, Class<S> subType, SubQueryConsumer<S, T> consumer);

    default <F, S> QueryStream<T> in(EntityField<T, F> field, Class<S> subType) {
        return in(field, subType, null);
    }

    default <F, S> QueryStream<T> notIn(EntityField<T, F> field, Class<S> subType) {
        return notIn(field, subType, null);
    }

    default <F> QueryStream<T> in(EntityField<T, F> field, SubQueryConsumer<T, T> consumer) {
        return in(field, getEntityType(), consumer);
    }

    default <F> QueryStream<T> notIn(EntityField<T, F> field, SubQueryConsumer<T, T> consumer) {
        return notIn(field, getEntityType(), consumer);
    }

    default <F> QueryStream<T> in(EntityField<T, F> field) {
        SubQueryConsumer<T, T> consumer = null;
        return in(field, consumer);
    }

    default <F> QueryStream<T> notIn(EntityField<T, F> field) {
        SubQueryConsumer<T, T> consumer = null;
        return notIn(field, consumer);
    }

    /**
     * Generates sub query part for EXISTS clause
     * 
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> exists(Class<S> subType, SubQueryConsumer<S, T> consumer);

    /**
     * Generates sub query part for NOT EXISTS clause
     * 
     * @param subType
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> notExists(Class<S> subType, SubQueryConsumer<S, T> consumer);

    default <F, S> QueryStream<T> exists(Class<S> subType) {
        return exists(subType, null);
    }

    default <F, S> QueryStream<T> notExists(Class<S> subType) {
        return notExists(subType, null);
    }

    default <F> QueryStream<T> exists(SubQueryConsumer<T, T> consumer) {
        return exists(getEntityType(), consumer);
    }

    default <F> QueryStream<T> notExists(SubQueryConsumer<T, T> consumer) {
        return notExists(getEntityType(), consumer);
    }

    @Override
    default QueryStream<T> where() {
        return this;
    }

    @Override
    default QueryStream<T> openBracket() {
        return GeneralQueryStream.super.openBracket();
    }
}
