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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.links.Clauses;
import org.lightmare.criteria.query.internal.orm.links.Operators.Brackets;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Implementation of
 * {@link org.lightmare.criteria.query.internal.orm.SelectExpression} to
 * generate SELECT for instant fields
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 * @param <O>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
abstract class AbstractSelectStatements<T, Q extends QueryStream<T, ? super Q>, O extends QueryStream<Object[], ? super O>>
        extends AbstractAggregateStream<T, Q, O> {

    // Query parts
    private static final String NEW_OPERATOR = "new ";

    protected AbstractSelectStatements(final LayerProvider provider, Class<T> entityType) {
        super(provider, entityType);
    }

    /**
     * Validates if query needs SELECT clause
     * 
     * @param expression
     * @return <code>boolean</code> validation result
     */
    private boolean needAppendSelect(String expression) {
        return (StringUtils.notStartsWith(expression, Clauses.SELECT)
                && StringUtils.notStartsWith(columns, Clauses.SELECT));
    }

    /**
     * Validates and appends "SELECT" expression to query
     * 
     * @param expression
     */
    private void validateAndAppendSelect(String expression) {

        if (needAppendSelect(expression)) {
            columns.append(Clauses.SELECT);
        }
    }

    /**
     * Validates and appends "SELECT" expression to query
     * 
     * @param expression
     */
    private void validateAndAppendSelect() {

        if (StringUtils.notStartsWith(columns, Clauses.SELECT)) {
            columns.append(Clauses.SELECT);
        }
    }

    /**
     * Generates SELECT clause
     * 
     * @param type
     * @param columnType
     * @param fields
     */
    protected void generateSelectClause(Class<?> type, boolean columnType, Collection<Serializable> fields) {

        StringBuilder buffer = new StringBuilder();
        appendSelect(fields, buffer);
        if (Object[].class.equals(type) || columnType) {
            validateAndAppendSelect();
            columns.append(buffer);
        } else {
            String entityName = getEntityName(type);
            String expression = StringUtils.concat(NEW_OPERATOR, entityName, Brackets.OPEN);
            validateAndAppendSelect(expression);
            columns.append(expression).append(buffer).append(Brackets.CLOSE);
        }
    }

    /**
     * Generates SELECT clause
     * 
     * @param type
     * @param fields
     */
    protected void generateSelectClause(Class<?> type, Collection<Serializable> fields) {
        generateSelectClause(type, Boolean.FALSE, fields);
    }

    /**
     * Generates SELECT clause
     * 
     * @param type
     * @param columnType
     * @param select
     */
    private void generateSelectClause(Class<?> type, boolean columnType, Select select) {
        List<Serializable> fields = select.getFields();
        generateSelectClause(type, columnType, fields);
    }

    /**
     * Generates SELECT clause
     * 
     * @param type
     * @param select
     */
    private void generateSelectClause(Class<?> type, Select select) {
        generateSelectClause(type, Boolean.FALSE, select);
    }

    @Override
    public <F, S extends QueryStream<F, ?>> S selectType(Class<F> type, Select select) {

        S stream;

        generateSelectClause(type, select);
        stream = ObjectUtils.applyAndCast(type, c -> new SelectStream<T, F>(this, c));

        return stream;
    }

    @Override
    public <F, S extends QueryStream<F, ?>> S select(String expression, Class<F> type) {

        S stream;

        validateAndAppendSelect(expression);
        columns.append(expression);
        stream = ObjectUtils.applyAndCast(type, c -> new SelectStream<T, F>(this, c));

        return stream;
    }

    @Override
    public <F, S extends QueryStream<F, ?>> S selectType(EntityField<T, F> field) {

        S stream;

        Class<F> fieldType = getFieldType(field);
        generateSelectClause(fieldType, Boolean.TRUE, Collections.singletonList(field));
        stream = ObjectUtils.applyAndCast(fieldType, c -> new SelectStream<T, F>(this, c));

        return stream;
    }

    @Override
    public <F> O select(EntityField<T, F> field) {

        O stream;

        generateSelectClause(Object[].class, Boolean.FALSE, Collections.singletonList(field));
        stream = ObjectUtils.cast(new SelectStream<T, Object[]>(this, Object[].class));

        return stream;
    }
}
