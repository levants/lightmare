package org.lightmare.criteria.query.jpa.joins;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.query.EntityQueryStream;
import org.lightmare.criteria.query.QueryStream;

/**
 * Implementation for JOIN clause query generation
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
abstract class AbstractJoinStream<T extends Serializable> extends EntityQueryStream<T> implements JoinQueryStream<T> {

    protected AbstractJoinStream(EntityManager em, Class<T> entityType, String alias) {
	super(em, entityType, alias);
    }

    @Override
    public <E extends Serializable, C extends Collection<E>> QueryStream<T> join(EntityField<T, C> field,
	    SubQueryConsumer<T, E> consumer) throws IOException {
	return this;
    }

    @Override
    public <E extends Serializable, C extends Collection<E>> QueryStream<T> leftJoin(EntityField<T, C> field,
	    SubQueryConsumer<T, E> consumer) throws IOException {
	return this;
    }

    @Override
    public <E extends Serializable, C extends Collection<E>> QueryStream<T> fetchJoin(EntityField<T, C> field,
	    SubQueryConsumer<T, E> consumer) throws IOException {
	return this;
    }
}
