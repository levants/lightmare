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

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.query.internal.orm.links.Operators;
import org.lightmare.criteria.query.providers.JpaQueryStream;

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
     * @return org.lightmare.criteria.query.QueryStream} current instance
     */
    <F> JpaQueryStream<T> operateColumn(FunctionConsumer<T> function, String operator, EntityField<T, F> field);

    default <F> JpaQueryStream<T> equal(FunctionConsumer<T> function, EntityField<T, F> field) {
        return operateColumn(function, Operators.EQ, field);
    }

    default <F> JpaQueryStream<T> notEqual(FunctionConsumer<T> function, EntityField<T, F> field) {
        return operateColumn(function, Operators.NOT_EQ, field);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> gtColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.GREATER, field);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> greaterThanColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.GREATER, field);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> ltColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.LESS, field);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> lessThanColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.LESS, field);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> geColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.GREATER_OR_EQ, field);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> greaterThanOrEqualToColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.GREATER_OR_EQ, field);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> leColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.LESS_OR_EQ, field);
    }

    default <F extends Comparable<? super F>> JpaQueryStream<T> lessThanOrEqualToColumn(FunctionConsumer<T> function,
            EntityField<T, Comparable<? super F>> field) {
        return operateColumn(function, Operators.EQ, field);
    }
}
