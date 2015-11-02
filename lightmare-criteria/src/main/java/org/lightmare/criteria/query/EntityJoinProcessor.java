package org.lightmare.criteria.query;

import java.io.Serializable;

import org.lightmare.criteria.query.jpa.AbstractQueryStream;

class EntityJoinProcessor<S extends Serializable, T extends Serializable> extends EntitySubQueryStream<S, T> {

    protected EntityJoinProcessor(AbstractQueryStream<T> parent, Class<S> entityType) {
	super(parent, entityType);
    }

    @Override
    public String sql() {

	String value;

	sql.append(body);
	value = sql.toString();

	return value;
    }
}
