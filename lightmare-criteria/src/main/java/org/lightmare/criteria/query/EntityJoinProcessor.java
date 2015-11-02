package org.lightmare.criteria.query;

import java.io.Serializable;

import org.lightmare.criteria.query.jpa.AbstractQueryStream;

/**
 * Utility class to process JOIN statements
 * 
 * @author Levan Tsiadze
 *
 * @param <S>
 *            join entity type for generated query
 * @param <T>
 *            entity type for generated query
 */
class EntityJoinProcessor<S extends Serializable, T extends Serializable> extends EntitySubQueryStream<S, T> {

    protected EntityJoinProcessor(AbstractQueryStream<T> parent, Class<S> entityType) {
	super(parent, entityType);
    }

    @Override
    public SubQueryStream<S, T> where() {
	return this;
    }

    @Override
    public String sql() {

	String value;

	sql.append(body);
	value = sql.toString();

	return value;
    }
}
