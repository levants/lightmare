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

import java.util.Arrays;
import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;

/**
 * Query stream for abstract data base layers
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <S>
 *            {@link org.lightmare.criteria.query.LambdaStream} implementation
 */
public interface LambdaStream<T, S extends LambdaStream<T, ? super S>> extends LayerStream<T> {

    <F> S equal(EntityField<T, F> field, Object value);

    <F> S notEqual(EntityField<T, F> field, Object value);

    <F extends Comparable<? super F>> S gt(EntityField<T, Comparable<? super F>> field, Comparable<? super F> value);

    <F extends Comparable<? super F>> S greaterThan(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value);

    <F extends Comparable<? super F>> S lt(EntityField<T, Comparable<? super F>> field, Comparable<? super F> value);

    <F extends Comparable<? super F>> S lessThan(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value);

    <F extends Comparable<? super F>> S ge(EntityField<T, Comparable<? super F>> field, Comparable<? super F> value);

    <F extends Comparable<? super F>> S greaterThanOrEqualTo(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value);

    <F extends Comparable<? super F>> S le(EntityField<T, Comparable<? super F>> field, Comparable<? super F> value);

    <F extends Comparable<? super F>> S lessThanOrEqualTo(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value);

    // =============================LIKE=clause==============================//

    S like(EntityField<T, String> field, String value);

    S notLike(EntityField<T, String> field, String value);

    // ======================================================================//

    <F> S in(EntityField<T, F> field, Collection<F> values);

    <F> S notIn(EntityField<T, F> field, Collection<F> values);

    default <F> S in(EntityField<T, F> field, F[] values) {
        return this.in(field, Arrays.asList(values));
    }

    default <F> S notIn(EntityField<T, F> field, F[] values) {
        return this.notIn(field, Arrays.asList(values));
    }

    // =============================NULL=check===============================//

    <F> S isNull(EntityField<T, F> field);

    <F> S isNotNull(EntityField<T, F> field);

    // ======================================================================//

    /**
     * AND logical operator
     * 
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    S and();

    /**
     * OR logical operator
     * 
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    S or();
}
