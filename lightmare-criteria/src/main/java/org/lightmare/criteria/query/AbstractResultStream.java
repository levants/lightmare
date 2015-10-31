package org.lightmare.criteria.query;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * Abstract class for generated JPA query result
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
abstract class AbstractResultStream<T extends Serializable> extends AbstractQueryStream<T> {

    protected AbstractResultStream(EntityManager em, Class<T> entityType, String alias) {
	super(em, entityType, alias);
    }

    @Override
    public Long count() {

	Long result;

	TypedQuery<Long> query = initCountQuery();
	result = query.getSingleResult();

	return result;
    }

    @Override
    public List<T> toList() {

	List<T> results;

	TypedQuery<T> query = initTypedQuery();
	results = query.getResultList();

	return results;
    }

    @Override
    public T get() {

	T result;

	TypedQuery<T> query = initTypedQuery();
	result = query.getSingleResult();

	return result;
    }

    @Override
    public int execute() {

	int result;

	Query query = initBulkQuery();
	result = query.executeUpdate();

	return result;
    }
}
