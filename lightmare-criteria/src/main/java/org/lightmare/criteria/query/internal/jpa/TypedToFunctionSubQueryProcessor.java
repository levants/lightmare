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

import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Operators;

/**
 * Sub query processor for ALL clause and functional expressions
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface TypedToFunctionSubQueryProcessor<T> extends SubQueryOperator<T> {

    /**
     * Provides method to process sub queries with ALL clause
     * 
     * @param value
     * @param operator
     * @param stream
     * @return {@link QueryStream} current instance
     */
    default <F, S> QueryStream<T> operateFunctionWthSubQuery(FunctionConsumer<T> consumer, String operator,
            SubQueryType<S> stream) {
        String composed = stream.getOperator(operator);
        return operateFunctionWithSubQuery(consumer, composed, stream.getType(), stream.getConsumer());
    }

    default <F, S> QueryStream<T> equalSubQuery(FunctionConsumer<T> consumer, SubQueryType<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.EQ, stream);
    }

    default <F, S> QueryStream<T> notEqualSubQuery(FunctionConsumer<T> consumer, SubQueryType<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.NOT_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> gtSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> greaterThenSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> ltSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lessThenSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> geSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> greaterThenOrEqualToSubQuery(
            FunctionConsumer<T> consumer, SubQueryType<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> leSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.LESS_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lessThenOrEqualToSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.LESS_OR_EQ, stream);
    }
}
