package org.lightmare.criteria.query;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.EntityManager;

import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.links.QueryParts;
import org.lightmare.criteria.tuples.QueryTuple;

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

    // Parent entity alias
    protected String parentAlias;

    protected AbstractSubQueryStream(final EntityManager em, final Class<S> entityType, final AliasTuple alias) {
	super(em, entityType, alias.generate());
	parentAlias = alias.getAlias();
    }

    private void appendColumn(QueryTuple tuple) {
	body.append(parentAlias).append(QueryParts.COLUMN_PREFIX);
	body.append(tuple.getField());
    }

    protected void opSubQuery(Object sfield, Object field, String expression) throws IOException {

	opp(sfield, expression);
	QueryTuple tuple = compose(field);
	appendColumn(tuple);
	body.append(NEW_LINE);
    }

    protected void opSubQueryCollection(Object field, Object sfield) throws IOException {

	opSubQuery(field, sfield, Operators.IN);
	QueryTuple tuple = compose(field);
	openBracket();
	appendColumn(tuple);
	closeBracket();
	body.append(NEW_LINE);
    }
}
