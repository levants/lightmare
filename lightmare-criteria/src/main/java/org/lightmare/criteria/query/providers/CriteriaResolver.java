package org.lightmare.criteria.query.providers;

import java.io.Serializable;
import java.util.function.BiFunction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryResolver;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryResolver} for JPA
 * criteria queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface CriteriaResolver<T> extends QueryResolver<T> {

    CriteriaBuilder getBuilder();

    Root<?> getRoot(Class<?> type);

    default <F> Expression<F> get(String name, Class<?> type) {

        Expression<F> expression;

        Root<?> root = getRoot(type);
        expression = root.get(name);

        return expression;
    }

    default <F> Expression<F> get(QueryTuple tuple) {
        return get(tuple.getFieldName(), tuple.getEntityType());
    }

    void addCurrent(Predicate predicate);

    default <F, V> void applyValue(Serializable field, V value, BiFunction<Expression<F>, V, Predicate> function) {
        Predicate condition = resolveAndApply(field, value, (t, v) -> function.apply(get(t), v));
        addCurrent(condition);
    }

    default <F> void applyField(Serializable field1, Serializable field2,
            BiFunction<Expression<F>, Expression<F>, Predicate> function) {

        QueryTuple tuple = resolve(field2);
        Predicate condition = resolveAndApply(field1, field2, (t, v) -> function.apply(get(t), get(tuple)));
        addCurrent(condition);
    }

    default <F extends Comparable<? super F>> void applyNumericValue(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value, BiFunction<Expression<? extends Number>, Number, Predicate> function) {

        Number num = ObjectUtils.cast(value);
        Predicate condition = resolveAndApply(field, num, (t, v) -> function.apply(get(t), v));
        addCurrent(condition);
    }

    default <F extends Comparable<? super F>> void applyNumericField(Serializable field1, Serializable field2,
            BiFunction<Expression<? extends Number>, Expression<? extends Number>, Predicate> function) {

        QueryTuple tuple = resolve(field2);
        Predicate condition = resolveAndApply(field1, field2, (t, v) -> function.apply(get(t), get(tuple)));
        addCurrent(condition);
    }

    default <F extends Comparable<? super F>> void applyComparableValue(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value, BiFunction<Expression<? extends F>, F, Predicate> function) {

        F comp = ObjectUtils.cast(value);
        Predicate condition = resolveAndApply(field, comp, (t, v) -> function.apply(get(t), v));
        addCurrent(condition);
    }

    default <F extends Comparable<? super F>> void applyComparableField(Serializable field1, Serializable field2,
            BiFunction<Expression<? extends F>, Expression<? extends F>, Predicate> function) {

        QueryTuple tuple = resolve(field2);
        Predicate condition = resolveAndApply(field1, field2, (t, v) -> function.apply(get(t), get(tuple)));
        addCurrent(condition);
    }
}
