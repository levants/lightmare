package org.lightmare.criteria.query.jpa;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.links.Joins;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.utils.ObjectUtils;

/**
 * Implementation for JOIN clause query generation
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
abstract class AbstractJoinStream<T extends Serializable> extends AbstractQueryStream<T> {

    protected AbstractJoinStream(EntityManager em, Class<T> entityType, String alias) {
	super(em, entityType, alias);
    }

    private <C extends Collection<?>> QueryTuple oppJoin(EntityField<T, C> field, String expression)
	    throws IOException {
	appendPrefix(expression);
	return compose(field);
    }

    @Override
    public <E extends Serializable, C extends Collection<E>> QueryStream<T> join(EntityField<T, C> field,
	    SubQueryConsumer<E, T> consumer) throws IOException {

	QueryTuple tuple = oppJoin(field, Joins.JOIN);
	@SuppressWarnings("unused")
	Class<E> subType = ObjectUtils.cast(tuple.getGenericType());

	return this;
    }

    @Override
    public <E extends Serializable, C extends Collection<E>> QueryStream<T> leftJoin(EntityField<T, C> field,
	    SubQueryConsumer<E, T> consumer) throws IOException {
	return this;
    }

    @Override
    public <E extends Serializable, C extends Collection<E>> QueryStream<T> fetchJoin(EntityField<T, C> field,
	    SubQueryConsumer<E, T> consumer) throws IOException {
	return this;
    }
}
