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
import org.lightmare.criteria.links.Filters;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.utils.StringUtils;

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
interface SubFieldStream<T, S> extends GeneralSubQueryStream<T, S> {

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
    default <F> SubQueryStream<S, T> greater(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.MORE);
    }

    @Override
    default <F> SubQueryStream<S, T> less(EntityField<S, F> field, F value) {
        return operate(field, value, Operators.LESS);
    }

    @Override
    default <F> SubQueryStream<S, T> greaterOrEquals(EntityField<S, F> field, F value) {
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

        SubQueryStream<S, T> stream;

        openBracket().appendBody(Operators.NO);
        stream = contains(field, value);
        stream = GeneralSubQueryStream.super.closeBracket();

        return stream;
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
