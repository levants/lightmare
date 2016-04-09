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
package org.lightmare.criteria.query.providers.jdbc;

import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.orm.SubQueryOperator;
import org.lightmare.criteria.query.orm.links.Operators;
import org.lightmare.criteria.query.orm.links.SubQuery.SubQueryType;

/**
 * Sub query processor for ALL, ANY, SOME clause and functional expressions
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
interface JdbcTypedToFunctionSubQueryProcessor<T, Q extends QueryStream<T, ? super Q>> extends SubQueryOperator<T, Q> {

    /**
     * Provides method to process sub queries with ALL clause
     * 
     * @param consumer
     * @param operator
     * @param stream
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <F, S> Q operateFunctionWthSubQuery(FunctionConsumer<T> consumer, String operator,
            SubQueryType<S, JdbcQueryStream<S>> stream) {
        String composed = stream.getOperator(operator);
        return operateFunctionWithSubQuery(consumer, composed, stream.getType(), stream.getConsumer());
    }

    default <F, S> Q equalSubQuery(FunctionConsumer<T> consumer, SubQueryType<S, JdbcQueryStream<S>> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.EQ, stream);
    }

    default <F, S> Q notEqualSubQuery(FunctionConsumer<T> consumer, SubQueryType<S, JdbcQueryStream<S>> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.NOT_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> Q gtSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S, JdbcQueryStream<S>> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> Q greaterThanSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S, JdbcQueryStream<S>> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> Q ltSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S, JdbcQueryStream<S>> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> Q lessThanSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S, JdbcQueryStream<S>> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> Q geSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S, JdbcQueryStream<S>> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> Q greaterThanOrEqualToSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S, JdbcQueryStream<S>> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> Q leSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S, JdbcQueryStream<S>> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.LESS_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> Q lessThanOrEqualToSubQuery(FunctionConsumer<T> consumer,
            SubQueryType<S, JdbcQueryStream<S>> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.LESS_OR_EQ, stream);
    }
}
