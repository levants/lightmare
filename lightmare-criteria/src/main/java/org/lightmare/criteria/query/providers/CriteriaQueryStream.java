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
import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Implementation of {@link org.lightmare.criteria.query.LambdaStream} for JPA
 * criteria queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface CriteriaQueryStream<T> extends LambdaStream<T, CriteriaQueryStream<T>>, CriteriaResolver<T> {

    @Override
    default <F> CriteriaQueryStream<T> equal(EntityField<T, F> field, Object value) {
        applyValue(field, value, getBuilder()::equal);
        return this;
    }

    default <F, V> CriteriaQueryStream<T> equal(EntityField<T, F> field1, EntityField<T, V> field2) {
        applyField(field1, field2, getBuilder()::equal);
        return this;
    }

    @Override
    default <F> CriteriaQueryStream<T> notEqual(EntityField<T, F> field, Object value) {
        applyValue(field, value, getBuilder()::notEqual);
        return this;
    }

    default <F, V> CriteriaQueryStream<T> notEqual(EntityField<T, F> field1, EntityField<T, V> field2) {
        applyField(field1, field2, getBuilder()::notEqual);
        return this;
    }

    @Override
    default <F extends Comparable<? super F>> CriteriaQueryStream<T> gt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        applyNumericValue(field, value, getBuilder()::gt);
        return this;
    }

    default <F extends Comparable<? super F>> CriteriaQueryStream<T> gt(EntityField<T, Comparable<? super F>> field1,
            EntityField<T, Comparable<? super F>> field2) {
        applyNumericField(field1, field2, getBuilder()::gt);
        return this;
    }

    @Override
    default <F extends Comparable<? super F>> CriteriaQueryStream<T> greaterThan(
            EntityField<T, Comparable<? super F>> field, Comparable<? super F> value) {
        applyComparableValue(field, value, getBuilder()::greaterThan);
        return this;
    }

    default <F extends Comparable<? super F>> CriteriaQueryStream<T> greaterThan(
            EntityField<T, Comparable<? super F>> field1, EntityField<T, Comparable<? super F>> field2) {
        applyComparableField(field2, field2, getBuilder()::greaterThan);
        return this;
    }

    @Override
    default <F extends Comparable<? super F>> CriteriaQueryStream<T> lt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        applyNumericValue(field, value, getBuilder()::lt);
        return this;
    }

    default <F extends Comparable<? super F>> CriteriaQueryStream<T> lt(EntityField<T, Comparable<? super F>> field1,
            EntityField<T, Comparable<? super F>> field2) {
        applyNumericField(field1, field2, getBuilder()::lt);
        return this;
    }

    @Override
    default <F extends Comparable<? super F>> CriteriaQueryStream<T> lessThan(
            EntityField<T, Comparable<? super F>> field, Comparable<? super F> value) {
        applyComparableValue(field, value, getBuilder()::lessThan);
        return this;
    }

    default <F extends Comparable<? super F>> CriteriaQueryStream<T> lessThan(
            EntityField<T, Comparable<? super F>> field1, EntityField<T, Comparable<? super F>> field2) {
        applyComparableField(field2, field2, getBuilder()::lessThan);
        return this;
    }

    @Override
    default <F extends Comparable<? super F>> CriteriaQueryStream<T> ge(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        applyNumericValue(field, value, getBuilder()::ge);
        return this;
    }

    default <F extends Comparable<? super F>> CriteriaQueryStream<T> ge(EntityField<T, Comparable<? super F>> field1,
            EntityField<T, Comparable<? super F>> field2) {
        applyNumericField(field1, field2, getBuilder()::ge);
        return this;
    }

    @Override
    default <F extends Comparable<? super F>> CriteriaQueryStream<T> greaterThanOrEqualTo(
            EntityField<T, Comparable<? super F>> field, Comparable<? super F> value) {
        applyComparableValue(field, value, getBuilder()::greaterThanOrEqualTo);
        return this;
    }

    default <F extends Comparable<? super F>> CriteriaQueryStream<T> greaterThanOrEqualTo(
            EntityField<T, Comparable<? super F>> field1, EntityField<T, Comparable<? super F>> field2) {
        applyComparableField(field2, field2, getBuilder()::greaterThanOrEqualTo);
        return this;
    }

    @Override
    default <F extends Comparable<? super F>> CriteriaQueryStream<T> le(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        applyNumericValue(field, value, getBuilder()::le);
        return this;
    }

    default <F extends Comparable<? super F>> CriteriaQueryStream<T> le(EntityField<T, Comparable<? super F>> field1,
            EntityField<T, Comparable<? super F>> field2) {
        applyNumericField(field1, field2, getBuilder()::le);
        return this;
    }

    @Override
    default <F extends Comparable<? super F>> CriteriaQueryStream<T> lessThanOrEqualTo(
            EntityField<T, Comparable<? super F>> field, Comparable<? super F> value) {
        applyComparableValue(field, value, getBuilder()::lessThanOrEqualTo);
        return this;
    }

    default <F extends Comparable<? super F>> CriteriaQueryStream<T> lessThanOrEqualTo(
            EntityField<T, Comparable<? super F>> field1, EntityField<T, Comparable<? super F>> field2) {
        applyComparableField(field2, field2, getBuilder()::lessThanOrEqualTo);
        return this;
    }

    // =============================LIKE=clause==============================//

    @Override
    default CriteriaQueryStream<T> like(EntityField<T, String> field, String value) {
        applyTextValue(field, value, getBuilder()::like);
        return this;
    }

    default CriteriaQueryStream<T> like(EntityField<T, String> field1, EntityField<T, String> field2) {
        applyTextField(field1, field2, getBuilder()::like);
        return this;
    }

    @Override
    default CriteriaQueryStream<T> notLike(EntityField<T, String> field, String value) {
        applyTextValue(field, value, getBuilder()::notLike);
        return this;
    }

    default CriteriaQueryStream<T> notLike(EntityField<T, String> field1, EntityField<T, String> field2) {
        applyTextField(field1, field2, getBuilder()::notLike);
        return this;
    }

    // ======================================================================//

    @Override
    default <F> CriteriaQueryStream<T> in(EntityField<T, F> field, Collection<F> values) {
        applyCollectionValue(field, values);
        return this;
    }

    default <F> CriteriaQueryStream<T> in(EntityField<T, F> field1, EntityField<T, Collection<F>> field2) {
        applyCollectionField(field1, field2);
        return this;
    }

    @Override
    default <F> CriteriaQueryStream<T> notIn(EntityField<T, F> field, Collection<F> values) {
        return this;
    }

    // =============================NULL=check===============================//

    @Override
    default <F> CriteriaQueryStream<T> isNull(EntityField<T, F> field) {
        applyValue(field, getBuilder()::isNull);
        return this;
    }

    @Override
    default <F> CriteriaQueryStream<T> isNotNull(EntityField<T, F> field) {
        applyValue(field, getBuilder()::isNotNull);
        return this;
    }

    // ======================================================================//

    @Override
    default CriteriaQueryStream<T> where(QueryConsumer<T, CriteriaQueryStream<T>> consumer) {
        ObjectUtils.accept(consumer, this);
        return this;
    }
}
