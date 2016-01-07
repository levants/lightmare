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
package org.lightmare.criteria.query.internal.jpa.subqueries;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.TemporalType;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.EntityQueryStream;
import org.lightmare.criteria.query.internal.jpa.builders.AbstractQueryStream;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.CollectionUtils;

/**
 * Main class to operate on sub queries and generate query clauses
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public abstract class AbstractSubQueryStream<S, T> extends EntityQueryStream<S> implements SubQueryStream<S, T> {

    // Parent entity alias
    protected final String parentAlias;

    protected final AbstractQueryStream<T> parent;

    private SubSelectStream<?, ?> subSelect;

    private boolean preparedState = Boolean.TRUE;

    protected AbstractSubQueryStream(final AbstractQueryStream<T> parent, String alias, Class<S> entityType) {
        super(parent.getEntityManager(), entityType, alias);
        parentAlias = parent.getAlias();
        this.parent = parent;
    }

    protected AbstractSubQueryStream(final AbstractQueryStream<T> parent, Class<S> entityType) {
        this(parent, parent.getAliasTuple().generate(), entityType);
    }

    private <K> SubSelectStream<S, K> generetaSubSelectStream(Class<K> type) {

        SubSelectStream<S, K> stream = new SubSelectStream<>(this, type);
        subSelect = stream;

        return stream;
    }

    /**
     * Processes sub select statement for sub queries
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.QueryStream} for instant field
     */
    protected <F> QueryStream<F> subSelectOne(EntityField<S, F> field) {

        SubSelectStream<S, F> stream;

        Class<F> fieldType = getFieldType(field);
        stream = generetaSubSelectStream(fieldType);

        return stream;
    }

    /**
     * Generates SELECT clause for sub query
     * 
     * @param fields
     * @return {@link org.lightmare.criteria.query.QueryStream} with {@link Object} array
     */
    protected final QueryStream<Object[]> subSelectAll(Serializable field) {

        SubSelectStream<S, Object[]> stream;

        generateSelectClause(Object[].class, Collections.singletonList(field));
        stream = generetaSubSelectStream(Object[].class);

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
    @Override
    protected void appendColumn(QueryTuple tuple) {

        if (tuple.getEntityType().equals(parent.getEntityType())) {
            super.appendColumn(parentAlias, tuple);
        } else {
            super.appendColumn(tuple);
        }
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

    @Override
    public void close() {
        // Swallows close method
    }
}
