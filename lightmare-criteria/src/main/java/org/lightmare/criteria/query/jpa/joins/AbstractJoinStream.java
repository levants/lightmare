package org.lightmare.criteria.query.jpa.joins;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.query.EntityQueryStream;
import org.lightmare.criteria.query.jpa.subqueries.AbstractSubQueryStream;

/**
 * Implementation for JOIN clause query generation
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public abstract class AbstractJoinStream<T extends Serializable, E extends Serializable, C extends Collection<E>>
	extends AbstractSubQueryStream<E, T> implements JoinQueryStream<T, E, C> {

    protected AbstractJoinStream(EntityQueryStream<T> parent, Class<E> entityType) {
	super(parent, entityType);
    }

    @Override
    public JoinQueryStream<T, E, C> join(EntityField<T, C> field, SubQueryConsumer<E, T> consumer) throws IOException {
	return this;
    }

    @Override
    public JoinQueryStream<T, E, C> leftJoin(EntityField<T, C> field, SubQueryConsumer<E, T> consumer)
	    throws IOException {
	return this;
    }

    @Override
    public JoinQueryStream<T, E, C> fetchJoin(EntityField<T, C> field, SubQueryConsumer<E, T> consumer)
	    throws IOException {
	return this;
    }
}
