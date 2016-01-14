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
package org.lightmare.criteria.query.internal.jpa.builders;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.lambda.LambdaUtils;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Clauses;
import org.lightmare.criteria.query.internal.jpa.links.Operators;
import org.lightmare.criteria.query.internal.jpa.links.Parts;
import org.lightmare.criteria.tuples.AliasTuple;
import org.lightmare.criteria.tuples.ParameterTuple;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.tuples.SuffixTuple;
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
abstract class AbstractJPAQueryStream<T> extends AbstractJPAQueryWrapper<T> {

    // Immutable data
    protected final EntityManager em;

    protected final Class<T> entityType;

    protected final String alias;

    // Incremental suffix for JPA query parameters
    private SuffixTuple parameterSuffix;

    // Incremental suffix for JPA entity aliases
    private SuffixTuple aliasSuffix;

    // JPA query parameters
    protected final Set<ParameterTuple> parameters = new HashSet<>();

    protected AbstractJPAQueryStream(final EntityManager em, final Class<T> entityType, final String alias) {
        this.em = em;
        this.entityType = entityType;
        this.alias = alias;
    }

    /**
     * Generates SELECT statement prefix
     * 
     * @param stream
     */
    protected static <T> void appendSelect(QueryStream<T> stream) {
        stream.appendPrefix(Clauses.SELECT).appendPrefix(stream.getAlias());
    }

    /**
     * Creates SELECT statement prefix
     * 
     * @param stream
     */
    protected static <T> void startsSelect(QueryStream<T> stream) {
        appendSelect(stream);
        appendEntityPart(stream);
    }

    /**
     * Gets entity name from {@link Class} or {@link javax.persistence.Entity}
     * annotation
     * 
     * @param type
     * @return {@link String} entity name
     */
    public static String getEntityName(Class<?> type) {

        String name;

        Entity entity = type.getAnnotation(Entity.class);
        if (entity == null) {
            name = type.getName();
        } else {
            name = StringUtils.thisOrDefault(entity.name(), () -> type.getName());
        }

        return name;
    }

    /**
     * Appends entity and alias part to stream
     * 
     * @param stream
     */
    protected static <T> void appendEntityPart(QueryStream<T> stream) {

        String entityName = getEntityName(stream.getEntityType());
        String alias = stream.getAlias();
        stream.appendFrom(Parts.FROM);
        stream.appendFrom(entityName);
        stream.appendFrom(Parts.AS);
        stream.appendFrom(alias);
        stream.appendFrom(StringUtils.LINE);
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
     * Gets appropriated {@link org.lightmare.criteria.tuples.QueryTuple} from
     * cache or generates from compiled class
     * 
     * @param field
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for passed
     *         lambda function
     */
    protected QueryTuple resolve(Serializable field) {

        QueryTuple tuple;

        tuple = LambdaUtils.getOrInit(field);
        tuple.setAlias(alias);

        return tuple;
    }

    /**
     * Gets appropriated {@link org.lightmare.criteria.tuples.QueryTuple} from
     * cache or generates from compiled class with generic parameters
     * 
     * @param field
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for passed
     *         lambda function
     */
    protected QueryTuple compose(Serializable field) {

        QueryTuple tuple = resolve(field);
        LambdaUtils.setGenericIfValid(entityType, tuple);

        return tuple;
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
        fieldType = tuple.getFieldType();

        return fieldType;
    }

    public SuffixTuple getParameterCounter() {
        return parameterSuffix;
    }

    /**
     * Sets parameter name suffix counter
     * 
     * @param parameter_counter
     */
    protected void setParameterCounter(SuffixTuple parameterSuffix) {
        this.parameterSuffix = parameterSuffix;
    }

    private SuffixTuple getOrInitParameterCounter() {
        return ObjectUtils.thisOrDefault(parameterSuffix, SuffixTuple::get, this::setParameterCounter);
    }

    /**
     * Generates parameter name for JPA query
     * 
     * @param tuple
     * @return {@link String} parameter name
     */
    private String generateParameterName(QueryTuple tuple) {
        return StringUtils.concat(tuple.getFieldName(), getOrInitParameterCounter().getAndIncrement());
    }

    @Override
    public void addParameter(String key, Object value, TemporalType temporalType) {
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
    public <F> void addParameter(String key, QueryTuple tuple, F value) {
        TemporalType temporalType = tuple.getTemporalType();
        addParameter(key, value, temporalType);
    }

    /**
     * Generates query part by tuple and adds appropriated parameter
     * 
     * @param tuple
     * @param value
     * @param buffer
     */
    public void oppWithParameter(QueryTuple tuple, Object value, StringBuilder buffer) {

        String parameterName = generateParameterName(tuple);
        buffer.append(Parts.PARAM_PREFIX).append(parameterName);
        addParameter(parameterName, tuple, value);
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

        String parameterName = generateParameterName(tuple);
        buffer.append(Operators.OPEN_BRACKET);
        buffer.append(Parts.PARAM_PREFIX).append(parameterName);
        buffer.append(Operators.Brackets.CLOSE);
        addParameter(parameterName, tuple, value);
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
     * Sets date parameter to query
     * 
     * @param parameter
     * @param query
     */
    private void setDateParameter(ParameterTuple parameter, Query query) {

        String name = parameter.getName();
        Object value = parameter.getValue();
        TemporalType temporalType = parameter.getTemporalType();
        if (value instanceof Calendar) {
            ObjectUtils.cast(value, Calendar.class, c -> query.setParameter(name, c, temporalType));
        } else if (value instanceof Date) {
            ObjectUtils.cast(value, Date.class, c -> query.setParameter(name, c, temporalType));
        }
    }

    /**
     * Sets parameter to query
     * 
     * @param parameter
     * @param query
     */
    private void setParameter(ParameterTuple parameter, Query query) {

        String name = parameter.getName();
        Object value = parameter.getValue();
        TemporalType temporalType = parameter.getTemporalType();
        if (temporalType == null) {
            query.setParameter(name, value);
        } else {
            setDateParameter(parameter, query);
        }
    }

    /**
     * Adds all gathered parameters to generated {@link javax.persistence.Query}
     * instance
     * 
     * @param query
     */
    protected void setParameters(Query query) {
        setJPAConfiguration(query);
        parameters.forEach(parameter -> setParameter(parameter, query));
    }

    // ============================= JPA Elements ===========================//

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Class<T> getEntityType() {
        return entityType;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    protected void setAliasSuffix(SuffixTuple aliasSuffix) {
        this.aliasSuffix = aliasSuffix;
    }

    /**
     * Gets or initializes {@link org.lightmare.criteria.tuples.SuffixTuple}
     * instance with initial counter
     * 
     * @return {@link org.lightmare.criteria.tuples.SuffixTuple} with initial
     *         counter
     */
    public SuffixTuple getAliasSuffix() {
        return ObjectUtils.thisOrDefault(aliasSuffix, SuffixTuple::get, this::setAliasSuffix);
    }

    /**
     * Generates {@link org.lightmare.criteria.tuples.AliasTuple} instance with
     * incremented counter for sub queries
     * 
     * @return {@link org.lightmare.criteria.tuples.AliasTuple} with incremented
     *         counter
     */
    public AliasTuple getAliasTuple() {
        return AliasTuple.of(alias, getAliasSuffix());
    }
}
