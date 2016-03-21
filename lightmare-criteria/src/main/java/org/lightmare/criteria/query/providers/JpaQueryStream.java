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
package org.lightmare.criteria.query.providers;

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.QueryExpression;
import org.lightmare.criteria.query.internal.orm.links.Operators;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Main interface with query construction methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 */
public interface JpaQueryStream<T> extends QueryStream<T, JpaQueryStream<T>>, QueryExpression<T> {

    @Override
    default JpaQueryStream<T> stream() {
        return this;
    }

    /**
     * Generates query part for embedded entity fields
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         current instance
     */
    <F> JpaQueryStream<T> embedded(EntityField<T, F> field, QueryConsumer<F, JpaQueryStream<F>> consumer);

    @Override
    default <F> JpaQueryStream<T> equal(EntityField<T, F> field, Object value) {
        return operate(field, value, Operators.EQ);
    }

    @Override
    default <F> JpaQueryStream<T> notEqual(EntityField<T, F> field, Object value) {
        return operate(field, value, Operators.NOT_EQ);
    }

    @Override
    default <F extends Comparable<? super F>> JpaQueryStream<T> gt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, Operators.GREATER);
    }

    @Override
    default <F extends Comparable<? super F>> JpaQueryStream<T> lt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, Operators.LESS);
    }

    @Override
    default <F extends Comparable<? super F>> JpaQueryStream<T> ge(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, Operators.GREATER_OR_EQ);
    }

    @Override
    default <F extends Comparable<? super F>> JpaQueryStream<T> le(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, Operators.LESS_OR_EQ);
    }

    // =============================LIKE=clause==============================//

    @Override
    default JpaQueryStream<T> like(EntityField<T, String> field, String value) {
        return operate(field, value, Operators.LIKE);
    }

    @Override
    default JpaQueryStream<T> notLike(EntityField<T, String> field, String value) {
        return operate(field, value, Operators.NOT_LIKE);
    }

    // ======================================================================//

    /**
     * Generates query part for instant field with {@link java.util.Collection}
     * parameter and operator
     * 
     * @param field
     * @param values
     * @param operator
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    <F> JpaQueryStream<T> operateCollection(EntityField<T, F> field, Collection<F> values, String operator);

    @Override
    default <F> JpaQueryStream<T> in(EntityField<T, F> field, Collection<F> values) {
        return operateCollection(field, values, Operators.IN);
    }

    @Override
    default <F> JpaQueryStream<T> notIn(EntityField<T, F> field, Collection<F> values) {
        return operateCollection(field, values, Operators.NOT_IN);
    }

    // =============================NULL=check===============================//

    @Override
    default <F> JpaQueryStream<T> isNull(EntityField<T, F> field) {
        return operate(field, Operators.IS_NULL);
    }

    @Override
    default <F> JpaQueryStream<T> isNotNull(EntityField<T, F> field) {
        return operate(field, Operators.NOT_NULL);
    }

    // ======================================================================//

    default JpaQueryStream<T> appendOperator(Object operator) {
        return appendBody(operator);
    }

    @Override
    default JpaQueryStream<T> and() {
        return appendOperator(Operators.AND);
    }

    @Override
    default JpaQueryStream<T> or() {
        return appendOperator(Operators.OR);
    }

    /**
     * Appends query body with operator and value
     * 
     * @param value
     * @param operator
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         current instance
     */
    default JpaQueryStream<T> appendOperator(Object value, Object operator) {
        return appendBody(value).appendBody(StringUtils.SPACE).appendBody(operator);
    }
}
