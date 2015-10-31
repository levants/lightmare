/*
 * Lightmare-criteria, JPA-QL query generator using lambda expressions
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.criteria.query;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.EntityManager;

import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.links.Parts;
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
	body.append(parentAlias).append(Parts.COLUMN_PREFIX);
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
