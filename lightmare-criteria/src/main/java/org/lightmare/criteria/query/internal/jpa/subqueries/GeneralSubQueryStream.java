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
package org.lightmare.criteria.query.internal.jpa.subqueries;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.links.Clauses;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.query.QueryStream;

/**
 * general query processor for sub queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            parent query entity type parameter
 * @param <S>
 *            entity type parameter
 */
interface GeneralSubQueryStream<T, S> extends QueryStream<S> {

    // =========================sub=queries==================================//

    /**
     * Generates {@link SubQueryStream} for S type
     * 
     * @param subType
     * @param consumer
     * @return {@link SubQueryStream} similar stream for sub query
     */
    @Override
    <K> SubQueryStream<S, T> subQuery(Class<K> subType, SubQueryConsumer<K, S> consumer);

    /**
     * Generates {@link SubQueryStream} for S type without conditions
     * 
     * @param consumer
     * @return {@link SubQueryStream} similar stream for sub query
     */
    @Override
    default SubQueryStream<S, T> subQuery(SubQueryConsumer<S, S> consumer) {
        return this.subQuery(getEntityType(), consumer);
    }

    @Override
    <F, K> SubQueryStream<S, T> in(EntityField<S, F> field, Class<K> subType, SubQueryConsumer<K, S> consumer);

    @Override
    <F, K> SubQueryStream<S, T> notIn(EntityField<S, F> field, Class<K> subType, SubQueryConsumer<K, S> consumer);

    @Override
    default <F, K> SubQueryStream<S, T> in(EntityField<S, F> field, Class<K> subType) {
        return this.in(field, subType, null);
    }

    @Override
    default <F, K> SubQueryStream<S, T> notIn(EntityField<S, F> field, Class<K> subType) {
        return this.notIn(field, subType, null);
    }

    @Override
    default <F> SubQueryStream<S, T> in(EntityField<S, F> field, SubQueryConsumer<S, S> consumer) {
        return this.in(field, getEntityType(), consumer);
    }

    @Override
    default <F> SubQueryStream<S, T> notIn(EntityField<S, F> field, SubQueryConsumer<S, S> consumer) {
        return this.notIn(field, getEntityType(), consumer);
    }

    @Override
    default <F> SubQueryStream<S, T> in(EntityField<S, F> field) {
        SubQueryConsumer<S, S> consumer = null;
        return this.in(field, consumer);
    }

    @Override
    default <F> SubQueryStream<S, T> notIn(EntityField<S, F> field) {
        SubQueryConsumer<S, S> consumer = null;
        return this.notIn(field, consumer);
    }

    @Override
    <F, K> SubQueryStream<S, T> exists(Class<K> subType, SubQueryConsumer<K, S> consumer);

    @Override
    <F, K> SubQueryStream<S, T> notExists(Class<K> subType, SubQueryConsumer<K, S> consumer);

    @Override
    default <F, K> SubQueryStream<S, T> exists(Class<K> subType) {
        return this.exists(subType, null);
    }

    @Override
    default <F, K> SubQueryStream<S, T> notExists(Class<K> subType) {
        return this.notExists(subType, null);
    }

    @Override
    default <F> SubQueryStream<S, T> exists(SubQueryConsumer<S, S> consumer) {
        return this.exists(getEntityType(), consumer);
    }

    @Override
    default <F> SubQueryStream<S, T> notExists(SubQueryConsumer<S, S> consumer) {
        return this.notExists(getEntityType(), consumer);
    }

    // =========================sub=queries==================================//

    // =========================order=by=====================================//

    @Override
    <F> SubQueryStream<S, T> orderBy(EntityField<S, F> field);

    @Override
    <F> SubQueryStream<S, T> orderByDesc(EntityField<S, F> field);

    // ======================================================================//

    /**
     * WHERE clause appender
     * 
     * @return {@link SubQueryStream} current instance
     */
    @Override
    SubQueryStream<S, T> where();

    /**
     * AND part appender
     * 
     * @return {@link SubQueryStream} current instance
     */
    @Override
    default SubQueryStream<S, T> and() {
        return appendBody(Clauses.AND);
    }

    /**
     * OR part appender
     * 
     * @return {@link SubQueryStream} current instance
     */
    @Override
    default SubQueryStream<S, T> or() {
        return appendBody(Clauses.OR);
    }

    /**
     * Opens bracket in query body
     * 
     * @return {@link SubQueryStream} current instance
     */
    @Override
    default SubQueryStream<S, T> openBracket() {
        return appendBody(Operators.OPEN_BRACKET);
    }

    /**
     * Closes bracket in query body
     * 
     * @return {@link SubQueryStream} current instance
     */
    @Override
    default SubQueryStream<S, T> closeBracket() {
        return appendBody(Operators.CLOSE_BRACKET);
    }

    /**
     * Creates query part in brackets
     * 
     * @param field
     * @return {@link SubQueryStream} current instance
     */
    @Override
    SubQueryStream<S, T> brackets(QueryConsumer<S> field);

    // ======================================================================//

    /**
     * Appends to generated query prefix custom clause
     * 
     * @param clause
     * @return {@link SubQueryStream} current instance
     */
    @Override
    SubQueryStream<S, T> appendPrefix(Object clause);

    /**
     * Appends to generated query body custom clause
     * 
     * @param clause
     * @return {@link SubQueryStream} current instance
     */
    @Override
    SubQueryStream<S, T> appendBody(Object clause);

    /**
     * Executes {@link AbstractSubQueryStream#get()} method if it is in prepared
     * state
     */
    void call();
}
