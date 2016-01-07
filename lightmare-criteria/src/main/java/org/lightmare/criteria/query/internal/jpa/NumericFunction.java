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

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.internal.jpa.links.Numerics;

/**
 * Numeric functions methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface NumericFunction<T> {

    /**
     * Creates numeric function expression for JPA query
     * 
     * @param x
     * @param operator
     * @return {@link org.lightmare.criteria.query.internal.jpa.JPAFunction}
     *         current instance
     */
    <S, F> JPAFunction<T> operateNumeric(EntityField<S, F> x, String operator);

    /**
     * Creates numeric function expression for JPA query
     * 
     * @param x
     * @param y
     * @param operator
     * @return {@link org.lightmare.criteria.query.internal.jpa.JPAFunction}
     *         current instance
     */
    JPAFunction<T> operateNumeric(Object x, Object y, String operator);

    /**
     * Create an expression that returns the arithmetic negation of its
     * argument.
     *
     * @param x
     * @return arithmetic negation
     */
    default <S, N extends Number> JPAFunction<T> neg(EntityField<S, N> x) {
        return operateNumeric(x, Numerics.NEG);
    }

    /**
     * Create an expression that returns the absolute value of its argument.
     *
     * @param x
     * @return absolute value
     */
    default <S, N extends Number> JPAFunction<T> abs(EntityField<S, N> x) {
        return operateNumeric(x, Numerics.ABS);
    }

    /**
     * Create an expression that returns the sum of its arguments.
     *
     * @param x
     * @param y
     * @return sum
     */
    default <S, U, N extends Number> JPAFunction<T> sum(EntityField<S, N> x, EntityField<U, N> y) {
        return operateNumeric(x, y, Numerics.SUM);
    }

    /**
     * Create an expression that returns the sum of its arguments.
     *
     * @param x
     * @param y
     * @return sum
     */
    default <S, N extends Number> JPAFunction<T> sum(EntityField<S, N> x, Number y) {
        return operateNumeric(x, y, Numerics.SUM);
    }

    /**
     * Create an expression that returns the sum of its arguments.
     *
     * @param x
     *            value
     * @param y
     *            expression
     *
     * @return sum
     */
    default <S, N extends Number> JPAFunction<T> sum(Number x, EntityField<S, N> y) {
        return operateNumeric(x, y, Numerics.SUM);
    }

    /**
     * Create an expression that returns the product of its arguments.
     *
     * @param x
     * @param y
     * @return product
     */
    default <S, U, N extends Number> JPAFunction<T> prod(EntityField<S, N> x, EntityField<U, N> y) {
        return operateNumeric(x, y, Numerics.PROD);
    }

    /**
     * Create an expression that returns the product of its arguments.
     *
     * @param x
     * @param y
     * @return product
     */
    default <S, N extends Number> JPAFunction<T> prod(EntityField<S, N> x, Number y) {
        return operateNumeric(x, y, Numerics.PROD);
    }

    /**
     * Create an expression that returns the product of its arguments.
     *
     * @param x
     * @param y
     * @return product
     */
    default <S, N extends Number> JPAFunction<T> prod(Number x, EntityField<S, N> y) {
        return operateNumeric(x, y, Numerics.PROD);
    }

    /**
     * Create an expression that returns the difference between its arguments.
     *
     * @param x
     * @param y
     * @return difference
     */
    default <S, U, N extends Number> JPAFunction<T> diff(EntityField<S, N> x, EntityField<U, N> y) {
        return operateNumeric(x, y, Numerics.DIFF);
    }

    /**
     * Create an expression that returns the difference between its arguments.
     *
     * @param x
     * @param y
     * @return difference
     */
    default <S, N extends Number> JPAFunction<T> diff(EntityField<S, N> x, Number y) {
        return operateNumeric(x, y, Numerics.DIFF);
    }

    /**
     * Create an expression that returns the difference between its arguments.
     *
     * @param x
     * @param y
     * @return difference
     */
    default <S, N extends Number> JPAFunction<T> diff(Number x, EntityField<S, N> y) {
        return operateNumeric(x, y, Numerics.DIFF);
    }

    /**
     * Create an expression that returns the quotient of its arguments.
     *
     * @param x
     * @param y
     * @return quotient
     */
    default <S, U, N extends Number> JPAFunction<T> quot(EntityField<S, N> x, EntityField<U, N> y) {
        return operateNumeric(x, y, Numerics.QUOT);
    }

    /**
     * Create an expression that returns the quotient of its arguments.
     *
     * @param x
     * @param y
     * @return quotient
     */
    default <S, N extends Number> JPAFunction<T> quot(EntityField<S, N> x, Number y) {
        return operateNumeric(x, y, Numerics.QUOT);
    }

    /**
     * Create an expression that returns the quotient of its arguments.
     *
     * @param x
     * @param y
     * @return quotient
     */
    default <S, N extends Number> JPAFunction<T> quot(Number x, EntityField<S, N> y) {
        return operateNumeric(x, y, Numerics.QUOT);
    }

    /**
     * Create an expression that returns the modulus of its arguments.
     *
     * @param x
     * @param y
     * @return modulus
     */
    default <S, U> JPAFunction<T> mod(EntityField<S, Integer> x, EntityField<U, Integer> y) {
        return operateNumeric(x, y, Numerics.MOD);
    }

    /**
     * Create an expression that returns the modulus of its arguments.
     *
     * @param x
     * @param y
     * @return modulus
     */
    default <S> JPAFunction<T> mod(EntityField<S, Integer> x, Integer y) {
        return operateNumeric(x, y, Numerics.MOD);
    }

    /**
     * Create an expression that returns the modulus of its arguments.
     *
     * @param x
     * @param y
     * @return modulus
     */
    default <S> JPAFunction<T> mod(Integer x, EntityField<S, Integer> y) {
        return operateNumeric(x, y, Numerics.MOD);
    }

    /**
     * Create an expression that returns the square root of its argument.
     *
     * @param x
     * @return square root
     */
    default <S, N extends Number> JPAFunction<T> sqrt(EntityField<S, N> x) {
        return operateNumeric(x, Numerics.SQRT);
    }
}
