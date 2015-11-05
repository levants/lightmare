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
package org.lightmare.criteria.query.jpa.subqueries;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.persistence.TemporalType;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.links.Parts;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.jpa.AbstractQueryStream;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Main class to operate on sub queries and generate query clauses
 * 
 * @author Levan Tsinadze
 *
 * @param <S>
 *            entity type for generated (sub) query
 * @param <T>
 *            entity type for generated query
 */
public abstract class AbstractSubQueryStream<S extends Serializable, T extends Serializable>
	extends DirectctSubQueryStream<S, T> {

    // Parent entity alias
    protected final String parentAlias;

    protected final AbstractQueryStream<T> parent;

    private SubSelectStream<S> subSelect;

    private boolean preparedState = Boolean.TRUE;

    protected AbstractSubQueryStream(final AbstractQueryStream<T> parent, Class<S> entityType) {
	super(parent.getEntityManager(), entityType, parent.getAliasTuple().generate());
	parentAlias = parent.getAlias();
	this.parent = parent;
    }

    @SafeVarargs
    protected final QueryStream<Object[]> subSelectAll(EntityField<S, ?>... fields) {

	SubSelectStream<S> stream;

	oppSelect(fields);
	stream = new SubSelectStream<>(this);
	subSelect = stream;

	return stream;
    }

    @Override
    public void addParameter(String key, Object value) {
	parent.addParameter(key, value);
    }

    @Override
    public void addParameter(String key, Object value, TemporalType temporalType) {
	parent.addParameter(key, value, temporalType);
    }

    @Override
    public <F> void addParameter(String key, QueryTuple tuple, F value) {
	parent.addParameter(key, tuple, value);
    }

    /**
     * Appends column (field) name with parent alias
     * 
     * @param tuple
     */
    private void appendColumn(QueryTuple tuple) {

	appendBody(parentAlias);
	appendBody(Parts.COLUMN_PREFIX);
	appendBody(tuple.getFieldName());
    }

    /**
     * Processes sub query part
     * 
     * @param sfield
     * @param field
     * @param expression
     */
    protected void oppSubQuery(Object sfield, Object field, String expression) {

	opp(sfield, expression);
	QueryTuple tuple = compose(field);
	appendColumn(tuple);
	body.append(NEW_LINE);
    }

    protected void oppSubQueryCollection(Object sfield, Object field, String expression) {

	opp(sfield, expression);
	QueryTuple tuple = compose(field);
	openBracket();
	appendColumn(tuple);
	closeBracket();
	appendBody(NEW_LINE);
    }

    /**
     * Processes sub query part for IN {@link Collection} clause
     * 
     * @param sfield
     * @param field
     */
    protected void oppSubQueryCollection(Object sfield, Object field) {
	oppSubQueryCollection(sfield, field, Operators.IN);
    }

    /**
     * Appends passed clause to parent stream
     * 
     * @param clause
     */
    protected void appendToParent(CharSequence clause) {
	parent.appendBody(clause);
    }

    /**
     * Generates sub query and appends to parent
     */
    private void appendToParent() {
	startsSelect(this);
	String query = sql();
	appendToParent(query);
    }

    /**
     * Switches prepared state to called
     */
    protected void switchState() {
	preparedState = Boolean.FALSE;
    }

    /**
     * Checks if state in prepare mode and calls statement by appending passed
     * {@link Character} to parent
     * 
     * @param clause
     */
    private void callState(CharSequence clause) {

	if (preparedState) {
	    appendToParent(clause);
	    switchState();
	}
    }

    /**
     * Checks if state in prepare mode and calls statement
     */
    private void callState() {

	if (preparedState) {
	    appendToParent();
	    switchState();
	}
    }

    @Override
    public List<S> toList() {
	callState();
	return null;
    }

    @Override
    public S get() {
	toList();
	return null;
    }

    @Override
    public Long count() {
	String query = countSql();
	callState(query);
	return null;
    }

    @Override
    public int execute() {
	get();
	return CollectionUtils.EMPTY;
    }

    /**
     * Checks state and if state in prepared mode calls appropriated query
     * method
     */
    private void chectStateAndCall() {

	if (Objects.nonNull(subSelect)) {
	    subSelect.get();
	} else {
	    get();
	}
    }

    @Override
    public void call() {

	if (preparedState) {
	    chectStateAndCall();
	    switchState();
	}
    }
}
