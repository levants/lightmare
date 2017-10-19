package org.lightmare.criteria.query.providers.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.orm.subqueries.AbstractSubQueryStream;
import org.lightmare.criteria.query.orm.subqueries.SubSelectStream;

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
class JpaSubSelectStream<T, E> extends SubSelectStream<T, E, JpaQueryStream<E>, JpaQueryStream<Object[]>>
        implements JpaQueryStream<E> {

    private static final long serialVersionUID = 1L;

    protected JpaSubSelectStream(AbstractSubQueryStream<T, ?, ?, ?> stream, Class<E> type) {
        super(stream, type);
    }

    @Override
    public <F> JpaQueryStream<E> embedded(EntityField<E, F> field, QueryConsumer<F, JpaQueryStream<F>> consumer) {
        return this;
    }
}
