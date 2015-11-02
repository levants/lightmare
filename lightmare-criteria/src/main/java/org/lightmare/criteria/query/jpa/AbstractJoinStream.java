package org.lightmare.criteria.query.jpa;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.tuples.QueryTuple;

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

    protected <C extends Collection<?>> QueryTuple oppJoin(EntityField<T, C> field, String expression)
	    throws IOException {

	QueryTuple tuple;

	appendJoin(expression);
	appendJoin(NEW_LINE);
	tuple = compose(field);

	return tuple;
    }
}
