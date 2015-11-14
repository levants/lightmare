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

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Operators;

/**
 * Query stream for entity fields and appropriated values
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface ColumnExpression<T> {

    // ========================= Entity self method composers ===============//

    /**
     * Generates query part for instant fields with and operator
     * 
     * @param field1
     * @param field2
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> operate(EntityField<T, ? extends F> field1, EntityField<S, ? extends F> field2,
            String operator);

    /**
     * Generates query part for fields and operator
     * 
     * @param field1
     * @param field2
     * @param field3
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> operate(EntityField<T, ? extends F> field1, EntityField<S, ? extends F> field2,
            EntityField<S, ? extends F> field3, String operator);

    default <F, S> QueryStream<T> equal(EntityField<T, F> field1, EntityField<S, F> field2) {
        return operate(field1, field2, Operators.EQ);
    }

    default <F, S> QueryStream<T> notEqual(EntityField<T, F> field1, EntityField<S, F> field2) {
        return operate(field1, field2, Operators.NOT_EQ);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> gt(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return operate(field1, field2, Operators.GREATER);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> greaterThen(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return gt(field1, field2);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lt(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return operate(field1, field2, Operators.LESS);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lowerThen(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return lt(field1, field2);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> ge(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return operate(field1, field2, Operators.GREATER_OR_EQ);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> greaterThenOrEqual(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return ge(field1, field2);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> le(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return operate(field1, field2, Operators.LESS_OR_EQ);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lowerThenOrEqual(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return le(field1, field2);
    }

    default <F extends Comparable<? super F>> QueryStream<T> between(EntityField<T, ? extends F> field1,
            EntityField<T, ? extends F> field2, EntityField<T, F> field3) {
        return operate(field1, field2, field3, Operators.BETWEEN);
    }

    default <F> QueryStream<T> notBetween(EntityField<T, ? extends F> field1, EntityField<T, ? extends F> field2,
            EntityField<T, F> field3) {
        return operate(field1, field2, field3, Operators.NOT_BETWEEN);
    }

    default QueryStream<T> like(EntityField<T, String> field1, EntityField<T, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    /**
     * Generates query part for instant fields with {@link Collection} types
     * 
     * @param field1
     * @param field2
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> operateCollection(EntityField<T, F> field1, EntityField<S, Collection<F>> field2,
            String operator);

    default <F, S> QueryStream<T> in(EntityField<T, F> field1, EntityField<S, Collection<F>> field2) {
        return operateCollection(field1, field2, Operators.IN);
    }

    default <F, S> QueryStream<T> notIn(EntityField<T, F> field1, EntityField<S, Collection<F>> field2) {
        return operateCollection(field1, field2, Operators.NOT_IN);
    }
}
