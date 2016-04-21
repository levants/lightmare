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
package org.lightmare.criteria.query.orm;

import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.query.orm.links.Operators;

/**
 * Functional expression with other column functional expression
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.LambdaStream} implementation
 *            parameter
 */
interface FunctionToFunctionExpression<T, Q extends LambdaStream<T, ? super Q>> {

    /**
     * Generates query clause with expression between functions
     * 
     * @param function1
     * @param function2
     * @param operator
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     */
    Q operateFunctions(FunctionConsumer<T> function1, FunctionConsumer<T> function2, String operator);

    default <F> Q eqFn(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.EQ);
    }

    default <F> Q notEqFn(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.NOT_EQ);
    }

    default Q gtFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.GREATER);
    }

    default Q greaterThanFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.GREATER);
    }

    default Q ltFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.LESS);
    }

    default Q lessThanFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.LESS);
    }

    default Q geFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.GREATER_OR_EQ);
    }

    default Q greaterThanOrEqualToFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.GREATER_OR_EQ);
    }

    default Q leFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.LESS_OR_EQ);
    }

    default Q lessThanOrEqualToFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.EQ);
    }
}
