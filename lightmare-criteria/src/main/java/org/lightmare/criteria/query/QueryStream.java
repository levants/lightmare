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

import java.util.Arrays;
import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.links.Clauses;
import org.lightmare.criteria.links.Filters;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.query.internal.SubQueryStream;
import org.lightmare.criteria.query.internal.jpa.JPAQueryWrapper;
import org.lightmare.criteria.query.internal.jpa.JoinQueryStream;
import org.lightmare.criteria.query.internal.jpa.ResultStream;
import org.lightmare.criteria.query.internal.jpa.SelectStatements;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Main interface with query construction methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 */
public interface QueryStream<T> extends JPAQueryWrapper<T>, SelectStatements<T>, JoinQueryStream<T>, ResultStream<T> {

    /**
     * Gets wrapped entity {@link Class} instance
     * 
     * @return {@link Class} of entity type T
     */
    Class<T> getEntityType();

    // ========================= Entity method composers ====================//

    <F> QueryStream<T> operate(EntityField<T, F> field, String operator);

    <F> QueryStream<T> operate(EntityField<T, F> field, F value, String operator);

    default <F> QueryStream<T> equals(EntityField<T, F> field, F value) {
        return operate(field, value, Operators.EQ);
    }

    default <F> QueryStream<T> notEquals(EntityField<T, F> field, F value) {
        return operate(field, value, Operators.NOT_EQ);
    }

    default <F> QueryStream<T> more(EntityField<T, F> field, F value) {
        return operate(field, value, Operators.MORE);
    }

    default <F> QueryStream<T> less(EntityField<T, F> field, F value) {
        return operate(field, value, Operators.LESS);
    }

    default <F> QueryStream<T> moreOrEquals(EntityField<T, F> field, F value) {
        return operate(field, value, Operators.MORE_OR_EQ);
    }

    default <F> QueryStream<T> lessOrEquals(EntityField<T, F> field, F value) {
        return operate(field, value, Operators.LESS_OR_EQ);
    }

    default QueryStream<T> startsWith(EntityField<T, String> field, String value) {
        String enrich = StringUtils.concat(value, Filters.LIKE_SIGN);
        return operate(field, enrich, Operators.LIKE);
    }

    default QueryStream<T> like(EntityField<T, String> field, String value) {
        return startsWith(field, value);
    }

    default QueryStream<T> endsWith(EntityField<T, String> field, String value) {
        String enrich = Filters.LIKE_SIGN.concat(value);
        return operate(field, enrich, Operators.LIKE);
    }

    default QueryStream<T> contains(EntityField<T, String> field, String value) {
        String enrich = StringUtils.concat(Filters.LIKE_SIGN, value, Filters.LIKE_SIGN);
        return operate(field, enrich, Operators.LIKE);
    }

    default QueryStream<T> notContains(EntityField<T, String> field, String value) {
        openBracket().appendBody(Operators.NO);
        return contains(field, value).closeBracket();
    }

    <F> QueryStream<T> operateCollection(EntityField<T, F> field, Collection<F> values, String operator);

    default <F> QueryStream<T> in(EntityField<T, F> field, Collection<F> values) {
        return operateCollection(field, values, Operators.IN);
    }

    default <F> QueryStream<T> notIn(EntityField<T, F> field, Collection<F> values) {
        return operateCollection(field, values, Operators.NOT_IN);
    }

    default QueryStream<T> isNull(EntityField<T, ?> field) {
        return operate(field, Operators.IS_NULL);
    }

    default QueryStream<T> notNull(EntityField<T, ?> field) {
        return operate(field, Operators.NOT_NULL);
    }

    // ========================= Entity self method composers ===============//

    <F> QueryStream<T> operate(EntityField<T, F> field1, EntityField<T, F> field2, String operator);

    default <F> QueryStream<T> equals(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operate(field1, field2, Operators.EQ);
    }

    default <F> QueryStream<T> notEquals(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operate(field1, field2, Operators.NOT_EQ);
    }

    default <F> QueryStream<T> more(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operate(field1, field2, Operators.MORE);
    }

    default <F> QueryStream<T> less(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operate(field1, field2, Operators.LESS);
    }

    default <F> QueryStream<T> moreOrEquals(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operate(field1, field2, Operators.MORE_OR_EQ);
    }

    default <F> QueryStream<T> lessOrEquals(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operate(field1, field2, Operators.LESS_OR_EQ);
    }

    default QueryStream<T> startsWith(EntityField<T, String> field1, EntityField<T, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    default QueryStream<T> like(EntityField<T, String> field1, EntityField<T, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    default QueryStream<T> endsWith(EntityField<T, String> field1, EntityField<T, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    default QueryStream<T> contains(EntityField<T, String> field1, EntityField<T, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    <F> QueryStream<T> operateCollection(EntityField<T, F> field1, EntityField<T, Collection<F>> field2,
            String operator);

    default <F> QueryStream<T> in(EntityField<T, F> field1, EntityField<T, Collection<F>> field2) {
        return operateCollection(field1, field2, Operators.IN);
    }

    default <F> QueryStream<T> notIn(EntityField<T, F> field1, EntityField<T, Collection<F>> field2) {
        return operateCollection(field1, field2, Operators.NOT_IN);
    }

    default <F> QueryStream<T> in(EntityField<T, F> field, F[] values) {
        return this.in(field, Arrays.asList(values));
    }

    default <F> QueryStream<T> notIn(EntityField<T, F> field, F[] values) {
        return this.notIn(field, Arrays.asList(values));
    }

    // =========================embedded=field=queries=======================//

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

    <F, S> QueryStream<T> in(EntityField<T, F> field, Class<S> subType, SubQueryConsumer<S, T> consumer);

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

    <F, S> QueryStream<T> exists(Class<S> subType, SubQueryConsumer<S, T> consumer);

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

    // =========================order=by=====================================//

    QueryStream<T> orderBy(EntityField<T, ?> field);

    QueryStream<T> orderByDesc(EntityField<T, ?> field);

    // ======================================================================//

    /**
     * Set clause for bulk UPDATE query
     * 
     * @param field
     * @param value
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> set(EntityField<T, F> field, F value);

    // ======================================================================//

    /**
     * WHERE clause appender
     * 
     * @return {@link QueryStream} current instance
     */
    default QueryStream<T> where() {
        return this;
    }

    /**
     * AND part appender
     * 
     * @return {@link QueryStream} current instance
     */
    default QueryStream<T> and() {
        return appendBody(Clauses.AND);
    }

    /**
     * OR part appender
     * 
     * @return {@link QueryStream} current instance
     */
    default QueryStream<T> or() {
        return appendBody(Clauses.OR);
    }

    /**
     * Opens bracket in query body
     * 
     * @return {@link QueryStream} current instance
     */
    default QueryStream<T> openBracket() {
        return appendBody(Operators.OPEN_BRACKET);
    }

    /**
     * Closes bracket in query body
     * 
     * @return {@link QueryStream} current instance
     */
    default QueryStream<T> closeBracket() {
        return appendBody(Operators.CLOSE_BRACKET);
    }

    /**
     * Creates query part in brackets
     * 
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    QueryStream<T> brackets(QueryConsumer<T> consumer);

    // ======================================================================//

    /**
     * Appends to generated query prefix custom clause
     * 
     * @param clause
     * @return {@link QueryStream} current instance
     */
    QueryStream<T> appendPrefix(Object clause);

    /**
     * Appends to generated query body custom clause
     * 
     * @param clause
     * @return {@link QueryStream} current instance
     */
    QueryStream<T> appendBody(Object clause);

    /**
     * Gets generated JPA query
     * 
     * @return {@link String} JPA query
     */
    String sql();

    /**
     * Gets generated JPA query for element count
     * 
     * @return {@link String} JPA query
     */
    String countSql();
}
