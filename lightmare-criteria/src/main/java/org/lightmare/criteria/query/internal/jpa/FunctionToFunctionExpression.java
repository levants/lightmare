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
 * Functional expression with other column functional expression
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface FunctionToFunctionExpression<T> {

    QueryStream<T> operateFunctions(FunctionConsumer<T> function1, FunctionConsumer<T> function2, String operator);

    default <F> QueryStream<T> equal(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.EQ);
    }

    default QueryStream<T> gtFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.GREATER);
    }

    default QueryStream<T> greaterThenFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.GREATER);
    }

    default QueryStream<T> ltFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.LESS);
    }

    default QueryStream<T> lessThenFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.LESS);
    }

    default QueryStream<T> geFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.GREATER_OR_EQ);
    }

    default QueryStream<T> greaterThenOrEqualToFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.GREATER_OR_EQ);
    }

    default QueryStream<T> leFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.LESS_OR_EQ);
    }

    default QueryStream<T> lessThenOrEqualToFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.EQ);
    }
}
