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

import java.util.Arrays;
import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Operators;
import org.lightmare.criteria.query.internal.jpa.links.Parts;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Query stream for entity fields
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface Expression<T> {

    /**
     * Opens bracket in query body
     * 
     * @return {@link QueryStream} current instance
     */
    QueryStream<T> openBracket();

    // ========================= Entity method composers ====================//

    /**
     * Generates query part for instant field and operator
     * 
     * @param field
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> operate(EntityField<T, F> field, String operator);

    /**
     * Generates query part for instant field with parameter and operator
     * 
     * @param field
     * @param value
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> operate(EntityField<T, ? extends F> field, Object value, String operator);

    /**
     * Generates query part for instant field with parameters and operator
     * 
     * @param field
     * @param value1
     * @param value2
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> operate(EntityField<T, ? extends F> field, Object value1, Object value2, String operator);

    /**
     * Generates query part for instant field with parameters and operators
     * 
     * @param field
     * @param operator1
     * @param value1
     * @param operator2
     * @param value2
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> operate(EntityField<T, ? extends F> field, String operator1, Object value1, String operator2,
            Object value2);

    default <F> QueryStream<T> equal(EntityField<T, F> field, Object value) {
        return operate(field, value, Operators.EQ);
    }

    default <F> QueryStream<T> notEqual(EntityField<T, F> field, Object value) {
        return operate(field, value, Operators.NOT_EQ);
    }

    default <F extends Comparable<? super F>> QueryStream<T> gt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, Operators.GREATER);
    }

    default <F extends Comparable<? super F>> QueryStream<T> greaterThen(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return gt(field, value);
    }

    default <F extends Comparable<? super F>> QueryStream<T> lt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, Operators.LESS);
    }

    default <F extends Comparable<? super F>> QueryStream<T> lessThen(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return lt(field, value);
    }

    default <F extends Comparable<? super F>> QueryStream<T> ge(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, Operators.GREATER_OR_EQ);
    }

    default <F extends Comparable<? super F>> QueryStream<T> greaterThenOrEqualTo(
            EntityField<T, Comparable<? super F>> field, Comparable<? super F> value) {
        return ge(field, value);
    }

    default <F extends Comparable<? super F>> QueryStream<T> le(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, Operators.LESS_OR_EQ);
    }

    default <F extends Comparable<? super F>> QueryStream<T> lessThenOrEqualTo(
            EntityField<T, Comparable<? super F>> field, Comparable<? super F> value) {
        return le(field, value);
    }

    default <F extends Comparable<? super F>> QueryStream<T> between(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value1, Comparable<? super F> value2) {
        return operate(field, value1, value2, Operators.BETWEEN);
    }

    // =============================LIKE=clause==============================//

    default <F extends Comparable<? super F>> QueryStream<T> notBetween(EntityField<T, Comparable<? super F>> field,
            F value1, F value2) {
        return operate(field, value1, value2, Operators.NOT_BETWEEN);
    }

    default QueryStream<T> like(EntityField<T, String> field, String value) {
        return operate(field, value, Operators.LIKE);
    }

    default QueryStream<T> notLike(EntityField<T, String> field, String value) {
        return operate(field, value, Operators.NOT_LIKE);
    }

    default QueryStream<T> like(EntityField<T, String> field, String value, char escape) {
        return operate(field, Operators.LIKE, value, Operators.ESCAPE, StringUtils.quote(escape));
    }

    default QueryStream<T> notLike(EntityField<T, String> field, String value, char escape) {
        return operate(field, Operators.NOT_LIKE, value, Operators.ESCAPE, StringUtils.quote(escape));
    }

    // =========================Implementations=of=LIKE=clause================//

    default QueryStream<T> startsWith(EntityField<T, String> field, String value) {
        String enrich = StringUtils.concat(value, Parts.LIKE_SIGN);
        return operate(field, enrich, Operators.LIKE);
    }

    default QueryStream<T> notStartsWith(EntityField<T, String> field, String value) {
        String enrich = StringUtils.concat(value, Parts.LIKE_SIGN);
        return operate(field, enrich, Operators.NOT_LIKE);
    }

    default QueryStream<T> endsWith(EntityField<T, String> field, String value) {
        String enrich = StringUtils.concat(Parts.LIKE_SIGN, value);
        return operate(field, enrich, Operators.LIKE);
    }

    default QueryStream<T> notEndsWith(EntityField<T, String> field, String value) {
        String enrich = StringUtils.concat(Parts.LIKE_SIGN, value);
        return operate(field, enrich, Operators.NOT_LIKE);
    }

    default QueryStream<T> contains(EntityField<T, String> field, String value) {
        String enrich = StringUtils.concat(Parts.LIKE_SIGN, value, Parts.LIKE_SIGN);
        return operate(field, enrich, Operators.LIKE);
    }

    default QueryStream<T> notContains(EntityField<T, String> field, String value) {
        String enrich = StringUtils.concat(Parts.LIKE_SIGN, value, Parts.LIKE_SIGN);
        return operate(field, enrich, Operators.NOT_LIKE);
    }

    // =========================Implementations=of=LIKE=clause================//

    /**
     * Generates query part for instant field with {@link Collection} parameter
     * and operator
     * 
     * @param field
     * @param values
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> operateCollection(EntityField<T, F> field, Collection<F> values, String operator);

    /**
     * Generates query part for instant field with {@link Collection} parameter
     * and operator
     * 
     * @param object
     * @param field
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <S, F> QueryStream<T> operateCollection(Object value, EntityField<S, Collection<F>> field, String operator);

    default <F> QueryStream<T> in(EntityField<T, F> field, Collection<F> values) {
        return operateCollection(field, values, Operators.IN);
    }

    default <F> QueryStream<T> notIn(EntityField<T, F> field, Collection<F> values) {
        return operateCollection(field, values, Operators.NOT_IN);
    }

    default <F> QueryStream<T> in(EntityField<T, F> field, F[] values) {
        return this.in(field, Arrays.asList(values));
    }

    default <F> QueryStream<T> notIn(EntityField<T, F> field, F[] values) {
        return this.notIn(field, Arrays.asList(values));
    }

    default <F, S> QueryStream<T> isMember(Object value, EntityField<S, Collection<F>> field) {
        return operateCollection(value, field, Operators.MEMBER);
    }

    default <F, S> QueryStream<T> isNotMember(Object value, EntityField<T, Collection<F>> field) {
        return operateCollection(value, field, Operators.NOT_MEMBER);
    }

    default <F> QueryStream<T> isNull(EntityField<T, F> field) {
        return operate(field, Operators.IS_NULL);
    }

    default <F> QueryStream<T> isNotNull(EntityField<T, F> field) {
        return operate(field, Operators.NOT_NULL);
    }
}
