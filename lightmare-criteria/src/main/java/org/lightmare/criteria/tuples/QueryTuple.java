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
package org.lightmare.criteria.tuples;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.lightmare.criteria.query.orm.links.Parts;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Query field and entity type container class
 * 
 * @author Levan Tsinadze
 *
 */
public class QueryTuple implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private String entityName;

    private final String methodName;

    private String fieldName;

    private String paramName;

    private Class<?> entityType;

    private Method method;

    private Field field;

    private Class<?> fieldType;

    private TemporalType temporalType;

    private Class<?> genericType;

    private String alias;

    private static final String ALIAS_PREFIX = "c";

    private static final String FORMATTER = "%s %s %s";

    protected QueryTuple(final String entityName, final String methodName, final String fieldName) {
        this.entityName = entityName;
        this.methodName = methodName;
        this.setFieldName(fieldName);
    }

    /**
     * Initializes {@link org.lightmare.criteria.tuples.QueryTuple} by method
     * description
     * 
     * @param entityName
     * @param methodName
     * @param fieldName
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} instance
     */
    public static QueryTuple of(final String entityName, final String methodName, final String fieldName) {
        return new QueryTuple(entityName, methodName, fieldName);
    }

    /**
     * Initializes {@link org.lightmare.criteria.tuples.QueryTuple} by field
     * name
     * 
     * @param fieldName
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} instance
     */
    public static QueryTuple of(final String fieldName) {
        return new QueryTuple(null, null, fieldName);
    }

    public String getEntityName() {
        return entityName;
    }

    private void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets field and parameter names
     * 
     * @param fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
        this.paramName = Parts.refineName(fieldName);
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getParamName() {
        return paramName;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<?> entityType) {
        this.entityType = entityType;
    }

    public void setTypeAndName(Class<?> entityType) {
        setEntityType(entityType);
        setEntityName(entityType.getName());
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
        this.fieldType = ObjectUtils.ifIsNotNull(field, Field::getType);
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public void setTemporalType(TemporalType temporalType) {
        this.temporalType = temporalType;
    }

    public void setTemporalType(Temporal temporal) {
        ObjectUtils.nonNull(temporal, c -> setTemporalType(c.value()));
    }

    public Class<?> getGenericType() {
        return ObjectUtils.thisOrDefault(genericType, field::getType, this::setGenericType);
    }

    public void setGenericType(Class<?> genericType) {
        this.genericType = genericType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean hasNoAlias() {
        return StringUtils.isEmpty(alias);
    }

    public void setAlias(int index) {
        this.alias = StringUtils.thisOrDefault(alias, () -> ALIAS_PREFIX.concat(String.valueOf(index)));
    }

    public <F> Class<F> getCollectionType() {
        return ObjectUtils.getAndCast(this::getGenericType);
    }

    public <F> Class<F> getFieldGenericType() {
        return ObjectUtils.getAndCast(this::getFieldType);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return String.format(FORMATTER, entityName, methodName, fieldName);
    }
}
