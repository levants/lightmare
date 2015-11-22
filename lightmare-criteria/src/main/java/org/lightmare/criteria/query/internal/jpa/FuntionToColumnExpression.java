package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
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
interface FuntionToColumnExpression<T> {

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