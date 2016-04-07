package org.lightmare.criteria.query.providers.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.internal.orm.subqueries.AbstractSubQueryStream;
import org.lightmare.criteria.query.internal.orm.subqueries.SubSelectStream;
import org.lightmare.criteria.query.providers.JpaQueryStream;

/**
 * Query builder for JPA sub query SELECT expressions
 * 
 * @author Levan Tsinadze
 *
 * @param <E>
 *            sub select type parameter
 * @param <T>
 *            entity type parameter
 */
public class JpaSubSelectStream<T, E> extends SubSelectStream<T, E, JpaQueryStream<E>, JpaQueryStream<Object[]>>
        implements JpaQueryStream<E> {

    protected JpaSubSelectStream(AbstractSubQueryStream<T, ?, ?, ?> stream, Class<E> type) {
        super(stream, type);
    }

    @Override
    public <F> JpaQueryStream<E> embedded(EntityField<E, F> field, QueryConsumer<F, JpaQueryStream<F>> consumer) {
        return this;
    }
}
