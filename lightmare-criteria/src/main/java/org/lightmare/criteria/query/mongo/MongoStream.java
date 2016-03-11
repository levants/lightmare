package org.lightmare.criteria.query.mongo;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.LambdaStream;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryStream} for
 * MongoDB filters
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface MongoStream<T> extends LambdaStream<T, MongoStream<T>> {

    @Override
    default MongoStream<T> and() {
        return this;
    }

    @Override
    default MongoStream<T> or() {
        return this;
    }

    @Override
    default <F extends Comparable<? super F>> MongoStream<T> greaterThen(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return gt(field, value);
    }

    @Override
    default <F extends Comparable<? super F>> MongoStream<T> lessThen(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return lt(field, value);
    }

    @Override
    default <F extends Comparable<? super F>> MongoStream<T> greaterThenOrEqualTo(
            EntityField<T, Comparable<? super F>> field, Comparable<? super F> value) {
        return ge(field, value);
    }

    @Override
    default <F extends Comparable<? super F>> MongoStream<T> lessThenOrEqualTo(
            EntityField<T, Comparable<? super F>> field, Comparable<? super F> value) {
        return le(field, value);
    }
}
