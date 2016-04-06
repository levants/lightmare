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
package org.lightmare.criteria.query.internal.orm.subqueries;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.TemporalType;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.builders.AbstractQueryStream;
import org.lightmare.criteria.query.internal.orm.builders.EntityQueryStream;
import org.lightmare.criteria.query.internal.orm.links.Aggregates;
import org.lightmare.criteria.tuples.Couple;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Main class to operate on sub queries and generate query clauses
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public abstract class AbstractSubQueryStream<S, T, Q extends QueryStream<S, ? super Q>, O extends QueryStream<Object[], ? super O>>
        extends EntityQueryStream<S, Q, O> implements SubQueryStream<S, T, Q> {

    // Parent entity alias
    protected final String parentAlias;

    protected final AbstractQueryStream<T, ?, ?> parent;

    private SubSelectStream<?, ?> subSelect;

    private boolean preparedState = Boolean.TRUE;

    protected AbstractSubQueryStream(final AbstractQueryStream<T, ?, ?> parent, String alias, Class<S> entityType) {
        super(parent.getLayerProvider(), entityType);
        this.alias = alias;
        this.parentAlias = parent.getAlias();
        this.setCounterTuple(parent.getCounterTuple());
        this.parent = parent;
    }

    protected AbstractSubQueryStream(final AbstractQueryStream<T, ?, ?> parent, Class<S> entityType) {
        this(parent, parent.generateSubAlias(), entityType);
    }

    /**
     * Generates
     * {@link org.lightmare.criteria.query.internal.orm.subqueries.SubSelectStream}
     * instance for {@link Class} parameter
     * 
     * @param type
     * @return
     */
    private <K, L extends QueryStream<K, ?>> L generateSubSelectStream(Class<K> type) {

        L stream;

        SubSelectStream<S, K> subSelectStream = new SubSelectStream<S, K>(this, type);
        subSelect = subSelectStream;
        stream = ObjectUtils.cast(subSelectStream);

        return stream;
    }

    /**
     * Processes sub select statement for sub queries
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     *         for instant field
     */
    protected <F, L extends QueryStream<F, ?>> L subSelectOne(EntityField<S, F> field) {

        L stream;

        Class<F> fieldType = getFieldType(field);
        stream = generateSubSelectStream(fieldType);

        return stream;
    }

    /**
     * Generates SELECT clause for sub query
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         with {@link Object} array
     */
    protected O subSelectAll(Serializable field) {

        O stream;

        generateSelectClause(Object[].class, Collections.singletonList(field));
        stream = generateSubSelectStream(Object[].class);

        return stream;
    }

    /**
     * Generates aggregate {@link org.lightmare.criteria.query.QueryStream}
     * implementation for instant type
     * 
     * @param field
     * @param function
     * @param type
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         with aggregate type
     */
    protected <F, R extends Number, L extends QueryStream<R, ? super L>> L subAggregate(EntityField<S, F> field,
            Aggregates function, Class<R> type) {

        L stream = super.aggregate(field, function, type);

        Class<R> selectType = stream.getEntityType();
        generateSubSelectStream(selectType);

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
    public <F> void addParameter(Couple<String, Integer> key, QueryTuple tuple, F value) {
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
    protected void appendToParent() {

        startsSelect();
        String query = sql();
        appendToParent(query);
    }

    /**
     * Switches state from prepared mode to called
     */
    protected void switchState() {
        preparedState = Boolean.FALSE;
    }

    /**
     * Checks if state is in prepare mode and calls statement by appending
     * passed {@link Character} to parent
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
     * Checks if state is in prepare mode and calls statement
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
