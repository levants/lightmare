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
        return operate(field, value, getLayerProvider().greaterThan());
    }

    @Override
    default <F extends Comparable<? super F>> S greaterThan(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return gt(field, value);
    }

    @Override
    default <F extends Comparable<? super F>> S lt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, getLayerProvider().lessThan());
    }

    @Override
    default <F extends Comparable<? super F>> S lessThan(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return lt(field, value);
    }

    @Override
    default <F extends Comparable<? super F>> S ge(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, getLayerProvider().greaterThanOrEqual());
    }

    @Override
    default <F extends Comparable<? super F>> S greaterThanOrEqualTo(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return ge(field, value);
    }

    @Override
    default <F extends Comparable<? super F>> S le(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, getLayerProvider().lessThanOrEqual());
    }

    @Override
    default <F extends Comparable<? super F>> S lessThanOrEqualTo(EntityField<T, Comparable<? super F>> field,
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
