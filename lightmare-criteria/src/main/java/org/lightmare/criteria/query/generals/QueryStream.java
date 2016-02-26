package org.lightmare.criteria.query.generals;

import java.util.Arrays;
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
 *            {@link org.lightmare.criteria.query.generals.QueryStream}
 *            implementation
 */
public interface QueryStream<T, S extends QueryStream<T, ? super S>> extends LayerStream<T> {

    /**
     * Generates query part for instant field and operator
     * 
     * @param field
     * @param operator
     * @return {@link org.lightmare.criteria.query.generals.QueryStream}
     *         implementation
     */
    <F> S operate(EntityField<T, F> field, String operator);

    /**
     * Generates query part for instant field with parameter and operator
     * 
     * @param field
     * @param value
     * @param operator
     * @return {@link org.lightmare.criteria.query.generals.QueryStream}
     *         implementation
     */
    <F> S operate(EntityField<T, ? extends F> field, Object value, String operator);

    default <F> S equal(EntityField<T, F> field, Object value) {
        return operate(field, value, getLayerProvider().equal());
    }

    default <F> S notEqual(EntityField<T, F> field, Object value) {
        return operate(field, value, getLayerProvider().notEqual());
    }

    default <F extends Comparable<? super F>> S gt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, getLayerProvider().greaterThen());
    }

    default <F extends Comparable<? super F>> S greaterThen(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return gt(field, value);
    }

    default <F extends Comparable<? super F>> S lt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, getLayerProvider().lessThen());
    }

    default <F extends Comparable<? super F>> S lessThen(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return lt(field, value);
    }

    default <F extends Comparable<? super F>> S ge(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, getLayerProvider().greaterThenOrEqual());
    }

    default <F extends Comparable<? super F>> S greaterThenOrEqualTo(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return ge(field, value);
    }

    default <F extends Comparable<? super F>> S le(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, getLayerProvider().lessThenOrEqual());
    }

    default <F extends Comparable<? super F>> S lessThenOrEqualTo(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return le(field, value);
    }

    // ======================================================================//

    /**
     * Generates query part for instant field with {@link java.util.Collection}
     * parameter and operator
     * 
     * @param field
     * @param values
     * @param operator
     * @return {@link org.lightmare.criteria.query.generals.QueryStream}
     *         implementation
     */
    <F> S operateCollection(EntityField<T, F> field, Collection<F> values, String operator);

    default <F> S in(EntityField<T, F> field, Collection<F> values) {
        return operateCollection(field, values, getLayerProvider().in());
    }

    default <F> S notIn(EntityField<T, F> field, Collection<F> values) {
        return operateCollection(field, values, getLayerProvider().notIn());
    }

    default <F> S in(EntityField<T, F> field, F[] values) {
        return this.in(field, Arrays.asList(values));
    }

    default <F> S notIn(EntityField<T, F> field, F[] values) {
        return this.notIn(field, Arrays.asList(values));
    }

    // ========================================================================//

    default <F> S isNull(EntityField<T, F> field) {
        return operate(field, getLayerProvider().isNull());
    }

    default <F> S isNotNull(EntityField<T, F> field) {
        return operate(field, getLayerProvider().isNotNull());
    }

    // ======================================================================//

    /**
     * AND logical operator
     * 
     * @return {@link org.lightmare.criteria.query.generals.QueryStream}
     *         implementation
     */
    S and();

    /**
     * OR logical operator
     * 
     * @return {@link org.lightmare.criteria.query.generals.QueryStream}
     *         implementation
     */
    S or();

    /**
     * Creates query part in brackets
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.generals.QueryStream}
     *         implementation
     */
    S brackets(QueryConsumer<T> consumer);

    /**
     * Gets generated query
     * 
     * @return {@link String} query
     */
    String sql();
}
