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
import org.lightmare.criteria.query.internal.orm.links.Operators;
import org.lightmare.criteria.query.providers.JpaQueryStream;

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
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for current
     *         entity type
     */
    JpaQueryStream<T> operateFunction(FunctionConsumer<T> function, String operator, Object value);

    default <F> JpaQueryStream<T> equal(FunctionConsumer<T> function, Object value) {
        return operateFunction(function, Operators.EQ, value);
    }

    default <F> JpaQueryStream<T> notEqual(FunctionConsumer<T> function, Object value) {
        return operateFunction(function, Operators.NOT_EQ, value);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> gtParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.GREATER, value);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> greaterThenParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.GREATER, value);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> ltParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.LESS, value);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> lessThenParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.LESS, value);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> geParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.GREATER_OR_EQ, value);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> greaterThenOrEqualToParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.GREATER_OR_EQ, value);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> leParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.LESS_OR_EQ, value);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> lessThenOrEqualToParam(FunctionConsumer<T> function,
            Comparable<? super F> value) {
        return operateFunction(function, Operators.EQ, value);
    }
}
