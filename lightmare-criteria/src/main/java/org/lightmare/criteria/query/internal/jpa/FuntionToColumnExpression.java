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

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Operators;

/**
 * Functional expression and column comparators
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface FuntionToColumnExpression<T> {

    /**
     * Generates query clause with expression between columns
     * 
     * @param function
     * @param operator
     * @param field
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> operateColumn(FunctionConsumer<T> function, String operator, EntityField<T, F> field);

    default <F> QueryStream<T> equal(FunctionConsumer<T> function, EntityField<T, F> field) {
        return operateColumn(function, Operators.EQ, field);
    }

    default <F> QueryStream<T> notEqual(FunctionConsumer<T> function, EntityField<T, F> field) {
        return operateColumn(function, Operators.NOT_EQ, field);
    }

    default <F extends Comparable<? super F>> QueryStream<T> gtColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.GREATER, field);
    }

    default <F extends Comparable<? super F>> QueryStream<T> greaterThenColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.GREATER, field);
    }

    default <F extends Comparable<? super F>> QueryStream<T> ltColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.LESS, field);
    }

    default <F extends Comparable<? super F>> QueryStream<T> lessThenColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.LESS, field);
    }

    default <F extends Comparable<? super F>> QueryStream<T> geColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.GREATER_OR_EQ, field);
    }

    default <F extends Comparable<? super F>> QueryStream<T> greaterThenOrEqualToColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.GREATER_OR_EQ, field);
    }

    default <F extends Comparable<? super F>> QueryStream<T> leColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.LESS_OR_EQ, field);
    }

    default <F extends Comparable<? super F>> QueryStream<T> lessThenOrEqualToColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.EQ, field);
    }
}
