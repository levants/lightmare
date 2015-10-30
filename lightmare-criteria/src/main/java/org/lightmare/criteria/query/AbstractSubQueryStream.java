package org.lightmare.criteria.query;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.EntityManager;

import org.lightmare.criteria.links.QueryParts;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.utils.StringUtils;

/**
 * Main class to operate on sub queries and generate query clauses
 * 
 * @author Levan Tsinadze
 *
 * @param <S>entity
 *            type for generated (sub) query
 * @param <T>entity
 *            type for generated query
 */
abstract class AbstractSubQueryStream<S extends Serializable, T extends Serializable> extends AbstractQueryStream<S>
	implements SubQueryStream<S, T> {

    protected String parentAlias;

    protected AbstractSubQueryStream(final EntityManager em, final Class<S> entityType, final AliasTuple alias) {
	super(em, entityType, alias.generate());
	parentAlias = alias.getAlias();
    }

    protected void opSubQuery(Object field, Object sfield, String expression) throws IOException {

	opp(sfield, expression);
	QueryTuple tuple = compose(field);
	body.append(StringUtils.SPACE);
	body.append(tuple.getAlias()).append(QueryParts.COLUMN_PREFIX);
	body.append(tuple.getField());
    }
}
