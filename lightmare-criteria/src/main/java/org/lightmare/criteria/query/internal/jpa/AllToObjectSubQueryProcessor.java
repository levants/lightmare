package org.lightmare.criteria.query.internal.jpa;

import java.util.Comparator;

import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Operators;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Sub query processor for ALL clause and arbitrary object or {@link Comparator}
 * implementations
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface AllToObjectSubQueryProcessor<T> extends SubQueryOperator<T> {

    /**
     * Provides method to process sub queries with ALL clause
     * 
     * @param value
     * @param operator
     * @param stream
     * @return {@link QueryStream} current instance
     */
    default <F, S> QueryStream<T> operateSubQuery(Object value, String operator, All<S> stream) {
        String composed = StringUtils.concat(operator, Operators.ALL);
        return operateSubQuery(value, composed, stream.getType(), stream.getConsumer());
    }

    default <F, S> QueryStream<T> equal(Object value, All<S> stream) {
        return operateSubQuery(value, Operators.EQ, stream);
    }

    default <F, S> QueryStream<T> notEqual(Object value, All<S> stream) {
        return operateSubQuery(value, Operators.NOT_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> gt(Comparable<? super F> value, All<S> stream) {
        return operateSubQuery(value, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> greaterThen(Comparable<? super F> value,
            All<S> stream) {
        return operateSubQuery(value, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lt(Comparable<? super F> value, All<S> stream) {
        return operateSubQuery(value, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lessThen(Comparable<? super F> value, All<S> stream) {
        return operateSubQuery(value, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> ge(Comparable<? super F> value, All<S> stream) {
        return operateSubQuery(value, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> greaterThenOrEqualTo(Comparable<? super F> value,
            All<S> stream) {
        return operateSubQuery(value, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> le(Comparable<? super F> value, All<S> stream) {
        return operateSubQuery(value, Operators.LESS_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lessThenOrEqualTo(Comparable<? super F> value,
            All<S> stream) {
        return operateSubQuery(value, Operators.LESS_OR_EQ, stream);
    }
}
