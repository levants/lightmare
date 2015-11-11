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

import java.util.Arrays;
import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.ParentField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.links.Clauses;
import org.lightmare.criteria.links.Filters;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Implementation of {@link QueryStream} for sub queries and joins
 * 
 * @author Levan Tsinadze
 * @param <S>
 *            entity type for generated (sub) query
 * @param <T>
 *            entity type for generated query
 */
public interface SubQueryStream<S, T> extends QueryStream<S> {

    /**
     * Gets entity alias for custom sub queries
     * 
     * @return {@link String} entity alias
     */
    String getAlias();

    // ========================= Entity method composers ====================//

    @Override
    <F> SubQueryStream<S, T> operate(EntityField<S, F> field, String operator);

    @Override
    <F> SubQueryStream<S, T> operate(EntityField<S, F> field, F value, String operator);

    @Override
    default <F> SubQueryStream<S, T> equals(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> notEquals(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.NOT_EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> more(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.MORE);
    }

    @Override
    default <F> SubQueryStream<S, T> less(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.LESS);
    }

    @Override
    default <F> SubQueryStream<S, T> moreOrEquals(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.MORE_OR_EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> lessOrEquals(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.LESS_OR_EQ);
    }

    @Override
    default SubQueryStream<S, T> startsWith(EntityField<S, String> field, String value) {
        String enrich = StringUtils.concat(value, Filters.LIKE_SIGN);
        return operate(field, enrich, Operators.LIKE);
    }

    @Override
    default SubQueryStream<S, T> like(EntityField<S, String> field, String value) {
        return startsWith(field, value);
    }

    @Override
    default SubQueryStream<S, T> endsWith(EntityField<S, String> field, String value) {
        String enrich = Filters.LIKE_SIGN.concat(value);
        return operate(field, enrich, Operators.LIKE);
    }

    @Override
    default SubQueryStream<S, T> contains(EntityField<S, String> field, String value) {
        String enrich = StringUtils.concat(Filters.LIKE_SIGN, value, Filters.LIKE_SIGN);
        return operate(field, enrich, Operators.LIKE);
    }

    @Override
    default SubQueryStream<S, T> notContains(EntityField<S, String> field, String value) {
        openBracket().appendBody(Operators.NO);
        return contains(field, value).closeBracket();
    }

    @Override
    <F> SubQueryStream<S, T> operateCollection(EntityField<S, F> field, Collection<F> values, String operator);

    @Override
    default <F> SubQueryStream<S, T> in(EntityField<S, F> field, Collection<F> values) {
        return operateCollection(field, values, Operators.IN);
    }

    @Override
    default <F> SubQueryStream<S, T> notIn(EntityField<S, F> field, Collection<F> values) {
        return operateCollection(field, values, Operators.NOT_IN);
    }

    @Override
    default <F> SubQueryStream<S, T> in(EntityField<S, F> field, F[] values) {
        return this.in(field, Arrays.asList(values));
    }

    @Override
    default <F> SubQueryStream<S, T> notIn(EntityField<S, F> field, F[] values) {
        return this.notIn(field, Arrays.asList(values));
    }

    @Override
    default <F> SubQueryStream<S, T> isNull(EntityField<S, F> field) {
        return operate(field, Operators.IS_NULL);
    }

    @Override
    default <F> SubQueryStream<S, T> notNull(EntityField<S, F> field) {
        return operate(field, Operators.NOT_NULL);
    }

    // ========================= Entity self method composers ===============//

    @Override
    <F> SubQueryStream<S, T> operate(EntityField<S, F> field1, EntityField<S, F> field2, String operator);

    @Override
    default <F> SubQueryStream<S, T> equals(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operate(field1, field2, Operators.EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> notEquals(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operate(field1, field2, Operators.NOT_EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> more(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operate(field1, field2, Operators.MORE);
    }

    @Override
    default <F> SubQueryStream<S, T> less(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operate(field1, field2, Operators.LESS);
    }

    @Override
    default <F> SubQueryStream<S, T> moreOrEquals(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operate(field1, field2, Operators.MORE_OR_EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> lessOrEquals(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operate(field1, field2, Operators.LESS_OR_EQ);
    }

    @Override
    default SubQueryStream<S, T> startsWith(EntityField<S, String> field1, EntityField<S, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    @Override
    default SubQueryStream<S, T> like(EntityField<S, String> field1, EntityField<S, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    @Override
    default SubQueryStream<S, T> endsWith(EntityField<S, String> field1, EntityField<S, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    @Override
    default SubQueryStream<S, T> contains(EntityField<S, String> field1, EntityField<S, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    @Override
    <F> SubQueryStream<S, T> operateCollection(EntityField<S, F> field1, EntityField<S, Collection<F>> field2,
            String operator);

    @Override
    default <F> SubQueryStream<S, T> in(EntityField<S, F> field1, EntityField<S, Collection<F>> field2) {
        return operateCollection(field1, field2, Operators.IN);
    }

    @Override
    default <F> SubQueryStream<S, T> notIn(EntityField<S, F> field1, EntityField<S, Collection<F>> field2) {
        return operateCollection(field1, field2, Operators.NOT);
    }

    // =========================embedded=field=queries=======================//

    @Override
    <F> SubQueryStream<S, T> embedded(EntityField<S, F> field, SubQueryConsumer<F, S> consumer);

    // ========================= Entity and parent method composers =========//

    /**
     * Generates query part for instant field and parent entity query field with
     * and operator
     * 
     * @param sfield
     * @param field
     * @param operator
     * @return {@link SubQueryStream} current instance
     */
    <F> SubQueryStream<S, T> operate(EntityField<S, F> sfield, ParentField<T, F> field, String operator);

    default <F> SubQueryStream<S, T> equals(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operate(sfield, field, Operators.EQ);
    }

    default <F> SubQueryStream<S, T> notEquals(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operate(sfield, field, Operators.NOT_EQ);
    }

    default <F> SubQueryStream<S, T> more(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operate(sfield, field, Operators.MORE);
    }

    default <F> SubQueryStream<S, T> less(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operate(sfield, field, Operators.LESS);
    }

    default <F> SubQueryStream<S, T> moreOrEquals(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operate(sfield, field, Operators.MORE_OR_EQ);
    }

    default <F> SubQueryStream<S, T> lessOrEquals(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operate(sfield, field, Operators.LESS_OR_EQ);
    }

    default SubQueryStream<S, T> startsWith(EntityField<S, String> sfield, ParentField<T, String> field) {
        return operate(sfield, field, Operators.LIKE);
    }

    default SubQueryStream<S, T> like(EntityField<S, String> sfield, ParentField<T, String> field) {
        return operate(sfield, field, Operators.LIKE);
    }

    default SubQueryStream<S, T> endsWith(EntityField<S, String> sfield, ParentField<T, String> field) {
        return operate(sfield, field, Operators.LIKE);
    }

    default SubQueryStream<S, T> contains(EntityField<S, String> sfield, ParentField<T, String> field) {
        return operate(sfield, field, Operators.LIKE);
    }

    /**
     * Generates query part for instant field and parent entity query field with
     * {@link Collection} type
     * 
     * @param sfield
     * @param field
     * @param operator
     * @return {@link SubQueryStream} current instance
     */
    <F> SubQueryStream<S, T> operateCollection(EntityField<S, F> sfield, ParentField<T, Collection<F>> field,
            String operator);

    default <F> SubQueryStream<S, T> in(EntityField<S, F> sfield, ParentField<T, Collection<F>> field) {
        return operateCollection(sfield, field, Operators.IN);
    }

    default <F> SubQueryStream<S, T> notIn(EntityField<S, F> sfield, ParentField<T, Collection<F>> field) {
        return operateCollection(sfield, field, Operators.NOT_IN);
    }

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
