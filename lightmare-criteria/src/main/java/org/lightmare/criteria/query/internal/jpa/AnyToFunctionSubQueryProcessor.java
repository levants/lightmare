package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Operators;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Sub query processor for ANY clause and functional expressions
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface AnyToFunctionSubQueryProcessor<T> extends SubQueryOperator<T> {

    /**
     * Provides method to process sub queries with ALL clause
     * 
     * @param value
     * @param operator
     * @param stream
     * @return {@link QueryStream} current instance
     */
    default <F, S> QueryStream<T> operateFunctionWthSubQuery(FunctionConsumer<T> consumer, String operator,
            Any<S> stream) {
        String composed = StringUtils.concat(operator, Operators.ALL);
        return operateFunctionWithSubQuery(consumer, composed, stream.getType(), stream.getConsumer());
    }

    default <F, S> QueryStream<T> equalSubQuery(FunctionConsumer<T> consumer, Any<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.EQ, stream);
    }

    default <F, S> QueryStream<T> notEqualSubQuery(FunctionConsumer<T> consumer, Any<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.NOT_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> gtSubQuery(FunctionConsumer<T> consumer,
            Any<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> greaterThenSubQuery(FunctionConsumer<T> consumer,
            Any<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> ltSubQuery(FunctionConsumer<T> consumer,
            Any<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lessThenSubQuery(FunctionConsumer<T> consumer,
            Any<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> geSubQuery(FunctionConsumer<T> consumer,
            Any<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> greaterThenOrEqualToSubQuery(
            FunctionConsumer<T> consumer, Any<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> leSubQuery(FunctionConsumer<T> consumer,
            Any<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.LESS_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lessThenOrEqualToSubQuery(FunctionConsumer<T> consumer,
            Any<S> stream) {
        return operateFunctionWthSubQuery(consumer, Operators.LESS_OR_EQ, stream);
    }
}
