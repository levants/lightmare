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
package org.lightmare.criteria.query.internal.orm.builders;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.TemporalType;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.internal.layers.JpaJdbcQueryLayer;
import org.lightmare.criteria.query.internal.layers.LayerProvider;
import org.lightmare.criteria.query.internal.orm.links.Clauses;
import org.lightmare.criteria.query.internal.orm.links.Operators;
import org.lightmare.criteria.query.internal.orm.links.Parts;
import org.lightmare.criteria.tuples.CounterTuple;
import org.lightmare.criteria.tuples.CounterTuple.NameCountTuple;
import org.lightmare.criteria.tuples.ParameterTuple;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Abstract utility class for lambda expression analyze and JPA query
 * preparation
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
abstract class AbstractORMQueryStream<T> extends AbstractORMQueryWrapper<T> {

    // Immutable data
    protected final LayerProvider provider;

    protected final Class<T> entityType;

    protected String alias;

    // Incremental suffix for JPA entity aliases and parameters
    private CounterTuple counterTuple;

    // JPA query parameters
    protected final Set<ParameterTuple> parameters = new HashSet<>();

    protected AbstractORMQueryStream(final LayerProvider provider, final Class<T> entityType) {
        this.provider = provider;
        this.entityType = entityType;
        this.alias = provider.alias();
    }

    /**
     * Generates SELECT statement prefix
     */
    protected void appendSelect() {
        String selectType = provider.getSelectType(getAlias());
        appendPrefix(Clauses.SELECT).appendPrefix(selectType);
    }

    /**
     * Creates SELECT statement prefix
     */
    protected void startsSelect() {
        appendSelect();
        appendEntityPart();
    }

    /**
     * Gets entity name from {@link Class} or {@link javax.persistence.Entity}
     * annotation
     * 
     * @param type
     * @return {@link String} entity name
     */
    public String getEntityName(Class<?> type) {
        return provider.getTableName(type);
    }

    /**
     * Appends entity and alias part to stream
     * 
     */
    protected void appendEntityPart() {

        Class<?> type = getEntityType();
        String entityName = getEntityName(type);
        String alias = getAlias();
        appendFrom(Parts.FROM);
        appendFrom(entityName);
        appendFrom(StringUtils.SPACE);
        appendFrom(alias);
        appendFrom(StringUtils.LINE);
    }

    /**
     * Adds field name to query
     * 
     * @param alias
     * @param field
     * @param buffer
     */
    protected static void appendFieldName(String alias, String field, StringBuilder buffer) {
        buffer.append(alias).append(Parts.COLUMN_PREFIX).append(field);
    }

    /**
     * Adds query part to from clause
     * 
     * @param tuple
     * @param buffer
     */
    protected static void appendFieldName(QueryTuple tuple, StringBuilder buffer) {
        appendFieldName(tuple.getAlias(), tuple.getFieldName(), buffer);
    }

    /**
     * Gets appropriated entity {@link Class} for
     * {@link org.lightmare.criteria.functions.EntityField} lambda expression
     * 
     * @param field
     * @return {@link Class} field type for entity
     */
    protected <F> Class<F> getFieldType(EntityField<T, F> field) {

        Class<F> fieldType;

        QueryTuple tuple = compose(field);
        fieldType = tuple.getFieldGenericType();

        return fieldType;
    }

    /**
     * Generates parameter name for JPA query
     * 
     * @param tuple
     * @return {@link org.lightmare.criteria.tuples.CounterTuple.NameCountTuple}
     *         parameter name
     */
    private NameCountTuple generateParameterName(QueryTuple tuple) {
        return getCounterTuple().getAndIncrement(tuple.getFieldName());
    }

    @Override
    public void addParameter(String key, Object value, TemporalType temporalType) {
        ParameterTuple parameter = ParameterTuple.of(key, value, temporalType);
        parameters.add(parameter);
    }

    private void addParameter(NameCountTuple key, Object value, TemporalType temporalType) {
        ParameterTuple parameter = ParameterTuple.of(key, value, temporalType);
        parameters.add(parameter);
    }

    @Override
    public void addParameter(String key, Object value) {
        addParameter(key, value, null);
    }

    @Override
    public void addParameters(Map<String, Object> parameters) {
        CollectionUtils.valid(parameters, c -> c.forEach(this::addParameter));
    }

    /**
     * Adds parameter to cache
     * 
     * @param tuple
     * @param value
     */
    public <F> void addParameter(NameCountTuple key, QueryTuple tuple, F value) {
        TemporalType temporalType = tuple.getTemporalType();
        addParameter(key, value, temporalType);
    }

    /**
     * Adds parameter to passed buffer
     * 
     * @param pair
     * @param buffer
     */
    private void appendParameter(NameCountTuple pair, StringBuilder buffer) {
        buffer.append(Parts.PARAM_PREFIX).append(pair.getName());
    }

    /**
     * Generates query part by tuple and adds appropriated parameter
     * 
     * @param tuple
     * @param value
     * @param buffer
     */
    public void oppWithParameter(QueryTuple tuple, Object value, StringBuilder buffer) {

        NameCountTuple pair = generateParameterName(tuple);
        appendParameter(pair, buffer);
        addParameter(pair, tuple, value);
    }

    /**
     * Generates query part by tuple and adds appropriated
     * {@link java.util.Collection} type parameter
     * 
     * @param tuple
     * @param value
     * @param buffer
     */
    public void oppWithCollectionParameter(QueryTuple tuple, Object value, StringBuilder buffer) {

        NameCountTuple pair = generateParameterName(tuple);
        buffer.append(Operators.OPEN_BRACKET);
        appendParameter(pair, buffer);
        buffer.append(Operators.Brackets.CLOSE);
        addParameter(pair, tuple, value);
    }

    /**
     * Generates query part by tuple and adds appropriated parameters
     * 
     * @param tuple
     * @param value1
     * @param value2
     * @param sqlPart
     */
    protected void oppWithParameter(QueryTuple tuple, Object value1, Object value2, StringBuilder buffer) {

        oppWithParameter(tuple, value1, buffer);
        appendBody(Operators.AND);
        oppWithParameter(tuple, value2, buffer);
    }

    // ============================= Query parameters =======================//

    /**
     * Adds all gathered parameters to generated {@link javax.persistence.Query}
     * instance
     * 
     * @param query
     */
    protected void setParameters(JpaJdbcQueryLayer<?> query) {
        setJPAConfiguration(query);
        parameters.forEach(query::setParameter);
    }

    // ============================= JPA Elements ===========================//

    @Override
    public LayerProvider getLayerProvider() {
        return provider;
    }

    @Override
    public Class<T> getEntityType() {
        return entityType;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    protected void setCounterTuple(CounterTuple counterTuple) {
        this.counterTuple = counterTuple;
    }

    /**
     * Gets or initializes {@link org.lightmare.criteria.tuples.CounterTuple}
     * instance with initial counter
     * 
     * @return {@link org.lightmare.criteria.tuples.CounterTuple} with initial
     *         counter
     */
    public CounterTuple getCounterTuple() {
        return ObjectUtils.thisOrDefault(counterTuple, CounterTuple::get, this::setCounterTuple);
    }

    /**
     * Generates unique alias for sub query
     * 
     * @return {@link String} alias for sub query
     */
    public String generateSubAlias() {
        return StringUtils.concat(alias, getCounterTuple().getAndIncrementAlias());
    }
}
