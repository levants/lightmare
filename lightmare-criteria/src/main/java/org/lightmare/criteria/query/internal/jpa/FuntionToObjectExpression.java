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
 * Functional expression for JPA query
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface FuntionToObjectExpression<T> {

    /**
     * Operates with functional expression and parameter
     * 
     * @param function
     * @param operator
     * @param value
     * @return {@link QueryStream} for current entity type
     */
    QueryStream<T> operateFunction(FunctionConsumer<T> function, String operator, Object value);

    default <F> QueryStream<T> equal(FunctionConsumer<T> function, Object value) {
        return operateFunction(function, Operators.EQ, value);
    }

    default <F> QueryStream<T> notEqual(FunctionConsumer<T> function, Object value) {
        return operateFunction(function, Operators.NOT_EQ, value);
    }

    default <F extends Comparable<? super F>> QueryStream<T> gtParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.GREATER, value);
    }

    default <F extends Comparable<? super F>> QueryStream<T> greaterThenParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.GREATER, value);
    }

    default <F extends Comparable<? super F>> QueryStream<T> ltParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.LESS, value);
    }

    default <F extends Comparable<? super F>> QueryStream<T> lessThenParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.LESS, value);
    }

    default <F extends Comparable<? super F>> QueryStream<T> geParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.GREATER_OR_EQ, value);
    }

    default <F extends Comparable<? super F>> QueryStream<T> greaterThenOrEqualToParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.GREATER_OR_EQ, value);
    }

    default <F extends Comparable<? super F>> QueryStream<T> leParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.LESS_OR_EQ, value);
    }

    default <F extends Comparable<? super F>> QueryStream<T> lessThenOrEqualToParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.EQ, value);
    }
}
