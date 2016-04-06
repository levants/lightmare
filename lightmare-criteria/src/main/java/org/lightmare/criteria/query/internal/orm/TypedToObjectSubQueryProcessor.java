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

import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.links.Operators;
import org.lightmare.criteria.query.internal.orm.links.SubQuery.SubQueryType;
import org.lightmare.criteria.query.providers.JpaQueryStream;

/**
 * Sub query processor for ALL clause and arbitrary object or
 * {@link java.util.Comparator} implementations
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
interface TypedToObjectSubQueryProcessor<T, Q extends QueryStream<T, ? super Q>> extends SubQueryOperator<T, Q> {

    /**
     * Provides method to process sub queries with ALL clause
     * 
     * @param value
     * @param operator
     * @param stream
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <F, S> Q operateSubQuery(Object value, String operator, SubQueryType<S, JpaQueryStream<S>> stream) {
        String composed = stream.getOperator(operator);
        return operateSubQuery(value, composed, stream.getType(), stream.getConsumer());
    }

    default <F, S> Q equal(Object value, SubQueryType<S, JpaQueryStream<S>> stream) {
        return operateSubQuery(value, Operators.EQ, stream);
    }

    default <F, S> Q notEqual(Object value, SubQueryType<S, JpaQueryStream<S>> stream) {
        return operateSubQuery(value, Operators.NOT_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> Q gt(Comparable<? super F> value,
            SubQueryType<S, JpaQueryStream<S>> stream) {
        return operateSubQuery(value, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> Q greaterThan(Comparable<? super F> value,
            SubQueryType<S, JpaQueryStream<S>> stream) {
        return operateSubQuery(value, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> Q lt(Comparable<? super F> value,
            SubQueryType<S, JpaQueryStream<S>> stream) {
        return operateSubQuery(value, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> Q lessThan(Comparable<? super F> value,
            SubQueryType<S, JpaQueryStream<S>> stream) {
        return operateSubQuery(value, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> Q ge(Comparable<? super F> value,
            SubQueryType<S, JpaQueryStream<S>> stream) {
        return operateSubQuery(value, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> Q greaterThanOrEqualTo(Comparable<? super F> value,
            SubQueryType<S, JpaQueryStream<S>> stream) {
        return operateSubQuery(value, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> Q le(Comparable<? super F> value,
            SubQueryType<S, JpaQueryStream<S>> stream) {
        return operateSubQuery(value, Operators.LESS_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> Q lessThanOrEqualTo(Comparable<? super F> value,
            SubQueryType<S, JpaQueryStream<S>> stream) {
        return operateSubQuery(value, Operators.LESS_OR_EQ, stream);
    }
}
