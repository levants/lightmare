package org.lightmare.criteria.query;

import java.io.Serializable;

public class SelectStreamImpl<T extends Serializable> extends FullQueryStream<Object[]> {

    protected SelectStreamImpl(QueryStream<T> query) {
	super(query.getEntityManager(), Object[].class, query.getAlias());
    }
}
