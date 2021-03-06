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
package org.lightmare.criteria.query.orm;

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.query.orm.links.Operators;

/**
 * Query stream for entity fields and appropriated values
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.LambdaStream} implementation
 *            parameter
 */
interface ColumnExpression<T, Q extends LambdaStream<T, ? super Q>> {

    /**
     * Generates query part for instant fields with and operator
     * 
     * @param field1
     * @param field2
     * @param operator
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F, S> Q operate(EntityField<T, ? extends F> field1, EntityField<S, ? extends F> field2, String operator);

    /**
     * Generates query part for instant fields with and operators
     * 
     * @param operator1
     * @param field1
     * @param operator2
     * @param field2
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F, E, S, L> Q operate(EntityField<T, ? extends F> field1, String operator1, EntityField<S, ? extends F> field2,
            String operator2, EntityField<L, E> field3);

    /**
     * Generates query part for fields and operator
     * 
     * @param field1
     * @param field2
     * @param field3
     * @param operator
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F, S> Q operate(EntityField<T, ? extends F> field1, EntityField<S, ? extends F> field2,
            EntityField<S, ? extends F> field3, String operator);

    default <F, S> Q equal(EntityField<T, F> field1, EntityField<S, F> field2) {
        return operate(field1, field2, Operators.EQ);
    }

    default <F, S> Q notEqual(EntityField<T, F> field1, EntityField<S, F> field2) {
        return operate(field1, field2, Operators.NOT_EQ);
    }

    default <F extends Comparable<? super F>, S> Q gt(EntityField<T, Comparable<? super F>> field1,
            EntityField<S, Comparable<? super F>> field2) {
        return operate(field1, field2, Operators.GREATER);
    }

    default <F extends Comparable<? super F>, S> Q greaterThan(EntityField<T, Comparable<? super F>> field1,
            EntityField<S, Comparable<? super F>> field2) {
        return gt(field1, field2);
    }

    default <F extends Comparable<? super F>, S> Q lt(EntityField<T, Comparable<? super F>> field1,
            EntityField<S, Comparable<? super F>> field2) {
        return operate(field1, field2, Operators.LESS);
    }

    default <F extends Comparable<? super F>, S> Q lessThan(EntityField<T, Comparable<? super F>> field1,
            EntityField<S, Comparable<? super F>> field2) {
        return lt(field1, field2);
    }

    default <F extends Comparable<? super F>, S> Q ge(EntityField<T, Comparable<? super F>> field1,
            EntityField<S, Comparable<? super F>> field2) {
        return operate(field1, field2, Operators.GREATER_OR_EQ);
    }

    default <F extends Comparable<? super F>, S> Q greaterThanOrEqualTo(EntityField<T, Comparable<? super F>> field1,
            EntityField<S, Comparable<? super F>> field2) {
        return ge(field1, field2);
    }

    default <F extends Comparable<? super F>, S> Q le(EntityField<T, Comparable<? super F>> field1,
            EntityField<S, Comparable<? super F>> field2) {
        return operate(field1, field2, Operators.LESS_OR_EQ);
    }

    default <F extends Comparable<? super F>, S> Q lessThanOrEqualTo(EntityField<T, Comparable<? super F>> field1,
            EntityField<S, Comparable<? super F>> field2) {
        return le(field1, field2);
    }

    default <F extends Comparable<? super F>> Q between(EntityField<T, Comparable<? super F>> field1,
            EntityField<T, Comparable<? super F>> field2, EntityField<T, F> field3) {
        return operate(field1, field2, field3, Operators.BETWEEN);
    }

    default <F> Q notBetween(EntityField<T, Comparable<? super F>> field1, EntityField<T, Comparable<? super F>> field2,
            EntityField<T, F> field3) {
        return operate(field1, field2, field3, Operators.NOT_BETWEEN);
    }

    // =============================LIKE=Operator============================//

    default Q like(EntityField<T, String> field1, EntityField<T, String> pattern) {
        return operate(field1, pattern, Operators.LIKE);
    }

    default Q notLike(EntityField<T, String> field1, EntityField<T, String> pattern) {
        return operate(field1, pattern, Operators.NOT_LIKE);
    }

    default Q like(EntityField<T, String> field1, EntityField<T, String> pattern,
            EntityField<T, Character> escapeChar) {
        return operate(field1, Operators.LIKE, pattern, Operators.ESCAPE, escapeChar);
    }

    default Q notLike(EntityField<T, String> field1, EntityField<T, String> pattern,
            EntityField<T, Character> escapeChar) {
        return operate(field1, Operators.NOT_LIKE, pattern, Operators.ESCAPE, escapeChar);
    }

    /**
     * Generates query part for instant fields with {@link Collection} types
     * 
     * @param field1
     * @param field2
     * @param operator
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F, S> Q operateCollection(EntityField<T, F> field1, EntityField<S, Collection<F>> field2, String operator);

    default <F, S> Q isMember(EntityField<T, F> field1, EntityField<S, Collection<F>> field2) {
        return operateCollection(field1, field2, Operators.MEMBER);
    }

    default <F, S> Q isNotMember(EntityField<T, F> field1, EntityField<S, Collection<F>> field2) {
        return operateCollection(field1, field2, Operators.NOT_MEMBER);
    }
}
