package org.lightmare.criteria.query;

import java.io.Serializable;

import org.lightmare.criteria.query.jpa.AbstractQueryStream;

/**
 * Implementation of {@link SubQueryStream} to process embedded field statements
 * 
 * @author Levan Tsinadze
 *
 * @param <S>
 *            embedded type parameter
 * @param <T>
 *            query type parameter
 */
public class EntityEmbeddedStream<S extends Serializable, T extends Serializable> extends EntitySubQueryStream<S, T> {

    protected EntityEmbeddedStream(AbstractQueryStream<T> parent, Class<S> type) {
	super(parent, parent.getAlias(), type);
    }

    @Override
    public boolean validateOperator() {
	return parent.validateOperator();
    }

    @Override
    public String sql() {

	String value;

	sql.append(body);
	value = sql.toString();

	return value;
    }
}
