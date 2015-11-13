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
import org.lightmare.criteria.query.internal.jpa.Expression;
import org.lightmare.criteria.query.internal.jpa.links.Operators;

/**
 * Sub query field types
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            parent query entity type parameter
 * @param <S>
 *            entity type parameter
 */
interface SubExpression<S, T> extends Expression<S> {

    // ========================= Entity method composers ====================//

    @Override
    <F> SubQueryStream<S, T> operate(EntityField<S, F> field, String operator);

    @Override
    <F> SubQueryStream<S, T> operate(EntityField<S, F> field, F value, String operator);

    @Override
    <F> SubQueryStream<S, T> operate(EntityField<S, F> field, F value1, F value2, String operator);

    @Override
    default <F> SubQueryStream<S, T> equal(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> notEqual(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.NOT_EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> gt(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.GREATER);
    }

    @Override
    default <F> SubQueryStream<S, T> greaterThen(EntityField<S, F> field, F value) {
        return gt(field, value);
    }

    @Override
    default <F> SubQueryStream<S, T> lt(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.LESS);
    }

    @Override
    default <F> SubQueryStream<S, T> lessThen(EntityField<S, F> field, F value) {
        return lt(field, value);
    }

    @Override
    default <F> SubQueryStream<S, T> ge(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.GREATER_OR_EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> greaterThenOrEqual(EntityField<S, F> field, F value) {
        return ge(field, value);
    }

    @Override
    default <F> SubQueryStream<S, T> le(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.LESS_OR_EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> lessThenOrEqual(EntityField<S, F> field, F value) {
        return le(field, value);
    }

    @Override
    default <F> SubQueryStream<S, T> between(EntityField<S, F> field, F value1, F value2) {
        return operate(field, value1, value2, Operators.BETWEEN);
    }

    @Override
    default <F> SubQueryStream<S, T> notBetween(EntityField<S, F> field, F value1, F value2) {
        return operate(field, value1, value2, Operators.BETWEEN);
    }

    @Override
    default SubQueryStream<S, T> like(EntityField<S, String> field, String value) {
        return operate(field, Operators.LIKE);
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
}
