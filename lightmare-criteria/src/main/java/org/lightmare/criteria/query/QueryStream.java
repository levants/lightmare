package org.lightmare.criteria.query;

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;

/**
 * Query stream for abstract data base layers
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <S>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 */
public interface QueryStream<T, S extends QueryStream<T, ? super S>> extends LambdaStream<T, S> {

    /**
     * Generates query part for instant field and operator
     * 
     * @param field
     * @param operator
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    <F> S operate(EntityField<T, F> field, String operator);

    /**
     * Generates query part for instant field with parameter and operator
     * 
     * @param field
     * @param value
     * @param operator
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    <F> S operate(EntityField<T, ? extends F> field, Object value, String operator);

    @Override
    default <F> S equal(EntityField<T, F> field, Object value) {
        return operate(field, value, getLayerProvider().equal());
    }

    @Override
    default <F> S notEqual(EntityField<T, F> field, Object value) {
        return operate(field, value, getLayerProvider().notEqual());
    }

    @Override
    default <F extends Comparable<? super F>> S gt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, getLayerProvider().greaterThen());
    }

    @Override
    default <F extends Comparable<? super F>> S greaterThen(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return gt(field, value);
    }

    @Override
    default <F extends Comparable<? super F>> S lt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, getLayerProvider().lessThen());
    }

    @Override
    default <F extends Comparable<? super F>> S lessThen(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return lt(field, value);
    }

    @Override
    default <F extends Comparable<? super F>> S ge(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, getLayerProvider().greaterThenOrEqual());
    }

    @Override
    default <F extends Comparable<? super F>> S greaterThenOrEqualTo(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return ge(field, value);
    }

    @Override
    default <F extends Comparable<? super F>> S le(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, getLayerProvider().lessThenOrEqual());
    }

    @Override
    default <F extends Comparable<? super F>> S lessThenOrEqualTo(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return le(field, value);
    }

    // =============================LIKE=clause==============================//

    @Override
    default S like(EntityField<T, String> field, String value) {
        return operate(field, value, getLayerProvider().like());
    }

    @Override
    default S notLike(EntityField<T, String> field, String value) {
        return operate(field, value, getLayerProvider().notLike());
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
    <F> S operateCollection(EntityField<T, F> field, Collection<F> values, String operator);

    @Override
    default <F> S in(EntityField<T, F> field, Collection<F> values) {
        return operateCollection(field, values, getLayerProvider().in());
    }

    @Override
    default <F> S notIn(EntityField<T, F> field, Collection<F> values) {
        return operateCollection(field, values, getLayerProvider().notIn());
    }

    // =============================NULL=check===============================//

    @Override
    default <F> S isNull(EntityField<T, F> field) {
        return operate(field, getLayerProvider().isNull());
    }

    @Override
    default <F> S isNotNull(EntityField<T, F> field) {
        return operate(field, getLayerProvider().isNotNull());
    }

    // ======================================================================//

    /**
     * Creates query part in brackets
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    S brackets(QueryConsumer<T, S> consumer);
}
