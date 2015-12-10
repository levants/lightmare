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

import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Query field and entity type container class
 * 
 * @author Levan Tsinadze
 *
 */
public class QueryTuple implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private final String entityName;

    private final String methodName;

    private final String[] arguments;

    private final String fieldName;

    private Class<?> entityType;

    private Method method;

    private Field field;

    private TemporalType temporalType;

    private Class<?> genericType;

    private String alias;

    private static final String ALIAS_PREFIX = "c";

    private static final String FORMATTER = "%s %s %s";

    protected QueryTuple(final String entityName, final String methodName, final String[] arguments,
            final String fieldName) {
        this.entityName = entityName;
        this.methodName = methodName;
        this.arguments = arguments;
        this.fieldName = fieldName;
    }

    /**
     * Initializes {@link QueryTuple} by method description
     * 
     * @param entityName
     * @param methodName
     * @param arguments
     * @param fieldName
     * @return {@link QueryTuple} instance
     */
    public static QueryTuple of(final String entityName, final String methodName, final String[] arguments,
            final String fieldName) {
        return new QueryTuple(entityName, methodName, arguments, fieldName);
    }

    /**
     * Initializes {@link QueryTuple} by field name
     * 
     * @param fieldName
     * @return {@link QueryTuple} instance
     */
    public static QueryTuple of(final String fieldName) {
        return new QueryTuple(null, null, null, fieldName);
    }

    public String getEntityName() {
        return entityName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getArguments() {
        return arguments;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<?> entityType) {
        this.entityType = entityType;
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
    }

    public <F> Class<F> getFieldType() {

        Class<F> fieldType;

        Class<?> raw = getGenericType();
        fieldType = ObjectUtils.cast(raw);

        return fieldType;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public void setTemporalType(TemporalType temporalType) {
        this.temporalType = temporalType;
    }

    public void setTemporalType(Temporal temporal) {
        setTemporalType(temporal.value());
    }

    public Class<?> getGenericType() {

        if (genericType == null) {
            genericType = field.getType();
        }

        return genericType;
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
        return (this.alias == null || this.alias.isEmpty());
    }

    public void setAlias(int index) {

        if (this.alias == null || this.alias.isEmpty()) {
            this.alias = ALIAS_PREFIX.concat(String.valueOf(index));
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Clones {@link QueryTuple} without throwing an exception
     * 
     * @return {@link QueryTuple} clone
     */
    public QueryTuple cloneType() {

        QueryTuple tupleClone;

        try {
            Object raw = clone();
            tupleClone = ObjectUtils.cast(raw);
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }

        return tupleClone;
    }

    @Override
    public String toString() {
        return String.format(FORMATTER, entityName, methodName, fieldName);
    }
}
