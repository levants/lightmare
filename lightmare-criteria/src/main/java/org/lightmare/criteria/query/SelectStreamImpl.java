package org.lightmare.criteria.query;

import java.io.Serializable;

/**
 * Utility class to construct SELECT by fields
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
class SelectStreamImpl<T extends Serializable> extends FullQueryStream<Object[]> {

    // Real entity type before select statement
    private final Class<?> realEntityType;

    protected SelectStreamImpl(AbstractQueryStream<T> stream) {
	super(stream.getEntityManager(), Object[].class, stream.getAlias());
	this.realEntityType = stream.entityType;
	this.columns.append(stream.columns);
	this.body.append(stream.body);
	this.orderBy.append(stream.orderBy);
	this.parameters.addAll(stream.parameters);
    }

    @Override
    public String sql() {

	String value;

	sql.delete(START, sql.length());
	appendFromClause(realEntityType, alias, columns);
	generateBody(columns);
	sql.append(orderBy);
	sql.append(suffix);
	value = sql.toString();

	return value;
    }
}
