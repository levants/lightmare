package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;

/**
 * Numeric functions methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface NumericFunction<T> {

    /**
     * Create an expression that returns the arithmetic negation of its
     * argument.
     *
     * @param x
     * @return arithmetic negation
     */
    <N extends Number> QueryStream<T> neg(EntityField<T, N> x);

    /**
     * Create an expression that returns the absolute value of its argument.
     *
     * @param x
     * @return absolute value
     */
    <N extends Number> QueryStream<T> abs(EntityField<T, N> x);

    /**
     * Create an expression that returns the sum of its arguments.
     *
     * @param x
     * @param y
     * @return sum
     */
    <N extends Number> QueryStream<T> sum(EntityField<T, ? extends N> x, EntityField<T, ? extends N> y);

    /**
     * Create an expression that returns the sum of its arguments.
     *
     * @param x
     * @param y
     * @return sum
     */
    <N extends Number> QueryStream<T> sum(EntityField<T, ? extends N> x, N y);

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
    <N extends Number> QueryStream<T> sum(N x, EntityField<T, ? extends N> y);

    /**
     * Create an expression that returns the product of its arguments.
     *
     * @param x
     * @param y
     * @return product
     */
    <N extends Number> QueryStream<T> prod(EntityField<T, ? extends N> x, EntityField<T, ? extends N> y);

    /**
     * Create an expression that returns the product of its arguments.
     *
     * @param x
     * @param y
     * @return product
     */
    <N extends Number> QueryStream<T> prod(EntityField<T, ? extends N> x, N y);

    /**
     * Create an expression that returns the product of its arguments.
     *
     * @param x
     * @param y
     * @return product
     */
    <N extends Number> QueryStream<T> prod(N x, EntityField<T, ? extends N> y);

    /**
     * Create an expression that returns the difference between its arguments.
     *
     * @param x
     * @param y
     * @return difference
     */
    <N extends Number> QueryStream<T> diff(EntityField<T, ? extends N> x, Expression<? extends N> y);

    /**
     * Create an expression that returns the difference between its arguments.
     *
     * @param x
     * @param y
     * @return difference
     */
    <N extends Number> QueryStream<T> diff(EntityField<T, ? extends N> x, N y);

    /**
     * Create an expression that returns the difference between its arguments.
     *
     * @param x
     * @param y
     * @return difference
     */
    <N extends Number> QueryStream<T> diff(N x, EntityField<T, ? extends N> y);

    /**
     * Create an expression that returns the quotient of its arguments.
     *
     * @param x
     * @param y
     * @return quotient
     */
    QueryStream<T> quot(EntityField<T, ? extends Number> x, EntityField<T, ? extends Number> y);

    /**
     * Create an expression that returns the quotient of its arguments.
     *
     * @param x
     * @param y
     * @return quotient
     */
    QueryStream<T> quot(EntityField<T, ? extends Number> x, Number y);

    /**
     * Create an expression that returns the quotient of its arguments.
     *
     * @param x
     * @param y
     * @return quotient
     */
    QueryStream<T> quot(Number x, EntityField<T, ? extends Number> y);

    /**
     * Create an expression that returns the modulus of its arguments.
     *
     * @param x
     * @param y
     * @return modulus
     */
    QueryStream<T> mod(QueryStream<T> x, EntityField<T, Integer> y);

    /**
     * Create an expression that returns the modulus of its arguments.
     *
     * @param x
     * @param y
     * @return modulus
     */
    QueryStream<T> mod(EntityField<T, Integer> x, Integer y);

    /**
     * Create an expression that returns the modulus of its arguments.
     *
     * @param x
     * @param y
     * @return modulus
     */
    QueryStream<T> mod(Integer x, EntityField<T, Integer> y);

    /**
     * Create an expression that returns the square root of its argument.
     *
     * @param x
     * @return square root
     */
    QueryStream<T> sqrt(EntityField<T, ? extends Number> x);

    // typecasts:

    /**
     * Typecast. Returns same expression object.
     *
     * @param number
     * @return QueryStream&#060;Long&#062;
     */
    QueryStream<Long> toLong(EntityField<T, ? extends Number> number);

    /**
     * Typecast. Returns same expression object.
     *
     * @param number
     * @return QueryStream&#060;Integer&#062;
     */
    QueryStream<T> toInteger(EntityField<T, ? extends Number> number);

    /**
     * Typecast. Returns same expression object.
     *
     * @param number
     * @return QueryStream&#060;Float&#062;
     */
    QueryStream<T> toFloat(EntityField<T, ? extends Number> number);

    /**
     * Typecast. Returns same expression object.
     *
     * @param number
     * @return QueryStream&#060;Double&#062;
     */
    QueryStream<T> toDouble(EntityField<T, ? extends Number> number);

    /**
     * Typecast. Returns same expression object.
     *
     * @param number
     * @return QueryStream&#060;BigDecimal&#062;
     */
    QueryStream<T> toBigDecimal(EntityField<T, ? extends Number> number);

    /**
     * Typecast. Returns same expression object.
     *
     * @param number
     * @return QueryStream&#060;BigInteger&#062;
     */
    QueryStream<T> toBigInteger(EntityField<T, ? extends Number> number);

    /**
     * Typecast. Returns same expression object.
     *
     * @param character
     * @return QueryStream&#060;String&#062;
     */
    QueryStream<T> toString(EntityField<T, Character> character);

}
