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
package org.lightmare.criteria.query.internal.orm;

import org.lightmare.criteria.query.JpaQueryStream;
import org.lightmare.criteria.query.internal.orm.links.Operators;

/**
 * Sub query processor for ALL clause and arbitrary object or
 * {@link java.util.Comparator} implementations
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface TypedToObjectSubQueryProcessor<T> extends SubQueryOperator<T> {

    /**
     * Provides method to process sub queries with ALL clause
     * 
     * @param value
     * @param operator
     * @param stream
     * @return {@link org.lightmare.criteria.query.JpaQueryStream} current instance
     */
    default <F, S> JpaQueryStream<T> operateSubQuery(Object value, String operator, SubQueryType<S> stream) {
        String composed = stream.getOperator(operator);
        return operateSubQuery(value, composed, stream.getType(), stream.getConsumer());
    }

    default <F, S> JpaQueryStream<T> equal(Object value, SubQueryType<S> stream) {
        return operateSubQuery(value, Operators.EQ, stream);
    }

    default <F, S> JpaQueryStream<T> notEqual(Object value, SubQueryType<S> stream) {
        return operateSubQuery(value, Operators.NOT_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> JpaQueryStream<T> gt(Comparable<? super F> value,
            SubQueryType<S> stream) {
        return operateSubQuery(value, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> JpaQueryStream<T> greaterThen(Comparable<? super F> value,
            SubQueryType<S> stream) {
        return operateSubQuery(value, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> JpaQueryStream<T> lt(Comparable<? super F> value,
            SubQueryType<S> stream) {
        return operateSubQuery(value, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> JpaQueryStream<T> lessThen(Comparable<? super F> value,
            SubQueryType<S> stream) {
        return operateSubQuery(value, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> JpaQueryStream<T> ge(Comparable<? super F> value,
            SubQueryType<S> stream) {
        return operateSubQuery(value, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> JpaQueryStream<T> greaterThenOrEqualTo(Comparable<? super F> value,
            SubQueryType<S> stream) {
        return operateSubQuery(value, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> JpaQueryStream<T> le(Comparable<? super F> value,
            SubQueryType<S> stream) {
        return operateSubQuery(value, Operators.LESS_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> JpaQueryStream<T> lessThenOrEqualTo(Comparable<? super F> value,
            SubQueryType<S> stream) {
        return operateSubQuery(value, Operators.LESS_OR_EQ, stream);
    }
}
