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

import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.query.JpaQueryStream;
import org.lightmare.criteria.query.internal.orm.links.Operators;

/**
 * Functional expression with other column functional expression
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface FunctionToFunctionExpression<T> {

    /**
     * Generates query clause with expression between functions
     * 
     * @param function1
     * @param function2
     * @param operator
     * @return {@link org.lightmare.criteria.query.JpaQueryStream} current instance
     */
    JpaQueryStream<T> operateFunctions(FunctionConsumer<T> function1, FunctionConsumer<T> function2, String operator);

    default <F> JpaQueryStream<T> equal(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.EQ);
    }

    default JpaQueryStream<T> gtFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.GREATER);
    }

    default JpaQueryStream<T> greaterThenFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.GREATER);
    }

    default JpaQueryStream<T> ltFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.LESS);
    }

    default JpaQueryStream<T> lessThenFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.LESS);
    }

    default JpaQueryStream<T> geFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.GREATER_OR_EQ);
    }

    default JpaQueryStream<T> greaterThenOrEqualToFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.GREATER_OR_EQ);
    }

    default JpaQueryStream<T> leFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.LESS_OR_EQ);
    }

    default JpaQueryStream<T> lessThenOrEqualToFunction(FunctionConsumer<T> function1, FunctionConsumer<T> function2) {
        return operateFunctions(function1, function2, Operators.EQ);
    }
}
