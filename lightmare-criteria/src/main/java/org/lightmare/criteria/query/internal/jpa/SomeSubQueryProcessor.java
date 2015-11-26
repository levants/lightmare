package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Operators;
import org.lightmare.criteria.query.internal.jpa.links.SubQueries;
import org.lightmare.criteria.query.internal.jpa.subqueries.SubQueryProvider.SomeQueryStream;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Sub query processor for SOME clause
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface SomeSubQueryProcessor<T> extends SubQueryOperator<T> {

    default <F, S> QueryStream<T> operateSubQuery(EntityField<T, F> field, String operator, SomeQueryStream<S> stream) {
        String composed = StringUtils.concat(operator, SubQueries.SOME);
        return operateSubQuery(field, composed, stream.getType(), stream.getConsumer());
    }

    default <F, S> QueryStream<T> equal(EntityField<T, F> field, SomeQueryStream<S> stream) {
        return operateSubQuery(field, Operators.EQ, stream);
    }

    default <F, S> QueryStream<T> notEqual(EntityField<T, F> field, SomeQueryStream<S> stream) {
        return operateSubQuery(field, Operators.NOT_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> gt(EntityField<T, Comparable<? super F>> field,
            SomeQueryStream<S> stream) {
        return operateSubQuery(field, Operators.GREATER, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> greaterThen(EntityField<T, Comparable<? super F>> field,
            SomeQueryStream<S> stream) {
        return gt(field, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lt(EntityField<T, Comparable<? super F>> field,
            SomeQueryStream<S> stream) {
        return operateSubQuery(field, Operators.LESS, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lessThen(EntityField<T, Comparable<? super F>> field,
            SomeQueryStream<S> stream) {
        return lt(field, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> ge(EntityField<T, Comparable<? super F>> field,
            SomeQueryStream<S> stream) {
        return operateSubQuery(field, Operators.GREATER_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> greaterThenOrEqualTo(
            EntityField<T, Comparable<? super F>> field, SomeQueryStream<S> stream) {
        return ge(field, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> le(EntityField<T, Comparable<? super F>> field,
            SomeQueryStream<S> stream) {
        return operateSubQuery(field, Operators.LESS_OR_EQ, stream);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lessThenOrEqualTo(
            EntityField<T, Comparable<? super F>> field, SomeQueryStream<S> stream) {
        return le(field, stream);
    }
}
