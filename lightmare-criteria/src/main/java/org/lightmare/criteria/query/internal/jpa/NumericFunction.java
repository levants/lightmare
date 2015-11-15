package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.GroupByConsumer;
import org.lightmare.criteria.query.QueryStream;

/**
 * Aggregate functions for JPA query language
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface NumericFunction<T> {

    QueryStream<T> count();

    <F> QueryStream<Object[]> count(EntityField<T, F> field, GroupByConsumer<T> consumer);
}
