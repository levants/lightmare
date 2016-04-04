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
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Query stream for abstract data base layers
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.LambdaStream} implementation
 */
public interface LambdaStream<T, Q extends LambdaStream<T, ? super Q>> extends LayerStream<T>, QueryResult<T> {

    /**
     * Gets current {@link org.lightmare.criteria.query.LambdaStream}
     * implementation
     * 
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    Q stream();

    /**
     * Equality filter
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F> Q equal(EntityField<T, F> field, Object value);

    /**
     * Inequality filter
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F> Q notEqual(EntityField<T, F> field, Object value);

    /**
     * Greater than expression
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F extends Comparable<? super F>> Q gt(EntityField<T, Comparable<? super F>> field, Comparable<? super F> value);

    /**
     * Greater than expression
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    default <F extends Comparable<? super F>> Q greaterThan(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return this.gt(field, value);
    }

    /**
     * Less than expression
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F extends Comparable<? super F>> Q lt(EntityField<T, Comparable<? super F>> field, Comparable<? super F> value);

    /**
     * Less than expression
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    default <F extends Comparable<? super F>> Q lessThan(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return this.lt(field, value);
    }

    /**
     * Greater than or equals to expression
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F extends Comparable<? super F>> Q ge(EntityField<T, Comparable<? super F>> field, Comparable<? super F> value);

    /**
     * Greater than or equals to expression
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    default <F extends Comparable<? super F>> Q greaterThanOrEqualTo(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return this.ge(field, value);
    }

    /**
     * Less than or equals to expression
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F extends Comparable<? super F>> Q le(EntityField<T, Comparable<? super F>> field, Comparable<? super F> value);

    /**
     * Less than or equals to expression
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    default <F extends Comparable<? super F>> Q lessThanOrEqualTo(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return this.le(field, value);
    }

    // =============================LIKE=clause==============================//
    /**
     * LIKE clause expression
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    Q like(EntityField<T, String> field, String value);

    /**
     * NOT LIKE clause expression
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    Q notLike(EntityField<T, String> field, String value);

    // ======================================================================//

    /**
     * IN clause expression
     * 
     * @param field
     * @param values
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F> Q in(EntityField<T, F> field, Collection<F> values);

    /**
     * NOT IN clause expression
     * 
     * @param field
     * @param values
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F> Q notIn(EntityField<T, F> field, Collection<F> values);

    /**
     * IN clause expression
     * 
     * @param field
     * @param values
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    default <F> Q in(EntityField<T, F> field, F[] values) {
        return this.in(field, Arrays.asList(values));
    }

    /**
     * NOT IN clause expression
     * 
     * @param field
     * @param values
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    default <F> Q notIn(EntityField<T, F> field, F[] values) {
        return this.notIn(field, Arrays.asList(values));
    }

    // =============================NULL=check===============================//

    /**
     * IS NULL clause
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F> Q isNull(EntityField<T, F> field);

    /**
     * IS NOT NULL clause
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    <F> Q isNotNull(EntityField<T, F> field);

    // ======================================================================//

    /**
     * AND logical operator
     * 
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    Q and();

    /**
     * OR logical operator
     * 
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    Q or();

    /**
     * Where clause flag
     * 
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    default Q where() {
        return stream();
    }

    // ======================WHERE=AND=OR=clauses=with=stream================//

    /**
     * Where clause with stream for query in lambda expression manner
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    default Q where(QueryConsumer<T, Q> consumer) {
        return ObjectUtils.acceptAndGet(this::where, consumer);
    }

    /**
     * AND clause in lambda expression manner
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    default Q and(QueryConsumer<T, Q> consumer) {
        return ObjectUtils.acceptAndGet(this::and, consumer);
    }

    /**
     * OR clause in lambda expression manner
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    default Q or(QueryConsumer<T, Q> consumer) {
        return ObjectUtils.acceptAndGet(this::or, consumer);
    }
}
