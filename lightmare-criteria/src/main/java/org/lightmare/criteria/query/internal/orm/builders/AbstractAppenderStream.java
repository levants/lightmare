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

import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.internal.orm.links.Aggregates;
import org.lightmare.criteria.query.internal.orm.links.Clauses;
import org.lightmare.criteria.query.internal.orm.links.Operators;
import org.lightmare.criteria.query.internal.orm.links.Orders;
import org.lightmare.criteria.query.internal.orm.links.Parts;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.query.providers.JpaQueryStream;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Utility class to build JPA query clauses
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
abstract class AbstractAppenderStream<T> extends AbstractORMQueryStream<T> {

    // JPA query parts
    protected final StringBuilder prefix = new StringBuilder();

    protected final StringBuilder count = new StringBuilder();

    protected final StringBuilder from = new StringBuilder();

    protected final StringBuilder columns = new StringBuilder();

    protected final StringBuilder joins = new StringBuilder();

    protected final StringBuilder updateSet = new StringBuilder();

    protected final StringBuilder body = new StringBuilder();

    protected final StringBuilder suffix = new StringBuilder();

    protected final StringBuilder groupBy = new StringBuilder();

    protected final StringBuilder orderBy = new StringBuilder();

    protected final StringBuilder having = new StringBuilder();

    protected final StringBuilder sql = new StringBuilder();

    protected AbstractAppenderStream(final LayerProvider provider, final Class<T> entityType) {
        super(provider, entityType);
    }

    /**
     * Adds new line to parameter
     * 
     * @param buff
     */
    protected static void newLine(StringBuilder buff) {
        buff.append(StringUtils.LINE);
    }

    /**
     * Adds new line to JPA query body
     */
    public void newLine() {

        if (StringUtils.notEndsWith(body, StringUtils.LINE)) {
            newLine(body);
        }
    }

    /**
     * Adds JPA query part to from clause
     * 
     * @param typeName
     * @param alias
     * @param buff
     */
    protected static void appendFromClause(String typeName, String alias, StringBuilder buff) {

        buff.append(Parts.FROM);
        buff.append(typeName);
        buff.append(StringUtils.SPACE);
        buff.append(alias);
        newLine(buff);
    }

    /**
     * Adds JPA query part to from clause
     * 
     * @param type
     * @param alias
     * @param buff
     */
    protected void appendFromClause(Class<?> type, String alias, StringBuilder buff) {

        StringUtils.clear(buff);
        String entityName = getEntityName(type);
        appendFromClause(entityName, alias, buff);
    }

    /**
     * Adds JPA query part to from clause
     * 
     * @param tuple
     */
    protected void appendFieldName(QueryTuple tuple) {
        appendFieldName(tuple.getAlias(), tuple.getFieldName(), body);
    }

    /**
     * Generates unary expression
     * 
     * @param tuple
     * @param expression
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} passed as
     *         parameter
     */
    private QueryTuple operateUnary(QueryTuple tuple, String expression) {

        appendFieldName(tuple, body);
        appendBody(expression);

        return tuple;
    }

    /**
     * Generates binary expression
     * 
     * @param tuple
     * @param expression
     * @param value
     */
    private void operateBinary(QueryTuple tuple, String expression, Object value) {
        operateUnary(tuple, expression);
        oppWithParameter(tuple, value, body);
    }

    /**
     * Resolves and operates on binary expressions
     * 
     * @param field
     * @param expression
     * @param value
     */
    private void resolveAndOperateExpression(Serializable field, String expression, Object value) {
        resolveAndAccept(field, value, (c, v) -> operateBinary(c, expression, v));
    }

    /**
     * Generates JPA query part for field and expression and adds parameter
     * 
     * @param field
     * @param value
     * @param expression
     */
    protected <F> void opp(Serializable field, F value1, F value2, String expression) {
        resolveAndOperateExpression(field, expression, value1);
    }

    /**
     * Generates JPA query part for filed, expressions and values
     * 
     * @param field
     * @param expression1
     * @param value1
     * @param expression2
     * @param value2
     */
    protected <F, E> void opp(Serializable field, String expression1, F value1, String expression2, E value2) {

        resolveAndOperateExpression(field, expression1, value1);
        appendBody(StringUtils.SPACE);
        appendBody(expression2).appendBody(value2);
    }

    /**
     * Resolves and operates on unary operators
     * 
     * @param field
     * @param operator
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} resolved from
     *         field
     */
    private QueryTuple resolveAndOperateExpression(Serializable field, String operator) {
        return resolveAndApply(field, c -> operateUnary(c, operator));
    }

    /**
     * Generates sub query part with appropriated expression
     * 
     * @param field
     * @param expression
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for sub query
     *         field
     */
    protected QueryTuple appSubQuery(Serializable field, String expression) {

        QueryTuple tuple = resolveAndOperateExpression(field, expression);
        openBracket();

        return tuple;
    }

    /**
     * Generates query part for {@link java.util.Collection} parameter
     * 
     * @param field
     * @param value
     * @param expression
     */
    protected void oppCollection(Serializable field, Collection<?> value, String expression) {

        QueryTuple tuple = resolveAndOperateExpression(field, expression);
        oppWithCollectionParameter(tuple, value, body);
        newLine();
    }

    /**
     * Generates query part for {@link java.util.Collection} field
     * 
     * @param value
     * @param field
     * @param operator
     */
    protected void oppCollection(Object value, Serializable field, String operator) {

        appendBody(value).appendBody(operator);
        QueryTuple tuple = resolve(field);
        appendColumn(tuple);
    }

    /**
     * Generates query part for {@link java.util.Collection} parameter
     * 
     * @param field
     * @param values
     */
    protected void oppCollection(Serializable field, Collection<?> values) {
        oppCollection(field, values, Operators.IN);
    }

    /**
     * Validates if body needs boolean operator before clause
     * 
     * @param operators
     * @return <code>boolean</code> validation result
     */
    private boolean validForOperator(String... operators) {
        return (StringUtils.valid(body) && StringUtils.notEndsWithAll(body, operators));
    }

    /**
     * Validates query body to be append by logical operators
     * 
     * @return <code>boolean</code> validation result
     */
    public boolean validateOperator() {
        return validForOperator(Clauses.VALIDS);
    }

    /**
     * Appends default boolean operator to passed buffer
     */
    protected void appendOperator() {

        if (validateOperator()) {
            and();
        }
    }

    /**
     * Appends column (field) name with alias
     * 
     * @param columnAlias
     * @param tuple
     */
    protected void appendColumn(String columnAlias, QueryTuple tuple) {

        appendBody(columnAlias);
        appendBody(Parts.COLUMN_PREFIX);
        appendBody(tuple.getFieldName());
    }

    /**
     * Appends column (field) name with alias
     * 
     * @param tuple
     */
    protected void appendColumn(QueryTuple tuple) {
        appendColumn(tuple.getAlias(), tuple);
    }

    /**
     * Processes query part with other field
     * 
     * @param field1
     * @param field2
     * @param expression
     */
    protected void oppField(Serializable field1, Serializable field2, String expression) {

        resolveAndOperateExpression(field1, expression);
        QueryTuple tuple = resolve(field2);
        appendColumn(tuple);
        newLine();
    }

    /**
     * Processes query part with other fields
     * 
     * @param expression1
     * @param field1
     * @param expression2
     * @param field2
     */
    protected <F> void oppField(Serializable field1, String expression1, Serializable field2, String expression2,
            Serializable field3) {

        resolveAndOperateExpression(field1, expression1);
        resolveAndOperateExpression(field2, expression2);
        QueryTuple tuple = resolve(field3);
        appendColumn(tuple);
        newLine();
    }

    /**
     * Processes query part with other field with {@link java.util.Collection}
     * type
     * 
     * @param field1
     * @param field2
     * @param expression
     */
    protected void oppCollectionField(Serializable field1, Serializable field2, String expression) {

        resolveAndOperateExpression(field1, expression);
        QueryTuple tuple = resolve(field2);
        appendColumn(tuple);
        newLine();
    }

    /**
     * Appends to SET clause for UPDATE query
     */
    private void appendSetClause() {

        if (StringUtils.valid(updateSet)) {
            updateSet.append(Parts.COMMA);
            newLine(updateSet);
            updateSet.append(Parts.SET_SPACE);
        } else {
            updateSet.append(Clauses.SET);
        }
    }

    /**
     * Appends to SET clause for UPDATE query
     * 
     * @param tuple
     */
    private void appendSetClause(QueryTuple tuple) {
        appendFieldName(tuple, updateSet);
        updateSet.append(Operators.EQ);
    }

    /**
     * Processes SET clause
     * 
     * @param field
     * @param value
     */
    protected <F> void setOpp(Serializable field, F value) {

        QueryTuple tuple = compose(field);
        appendSetClause();
        appendSetClause(tuple);
        oppWithParameter(tuple, value, updateSet);
    }

    /**
     * Adds comma to passed query part
     * 
     * @param index
     * @param length
     * @param buff
     */
    private void appendComma(int index, int length, StringBuilder buff) {

        if (index < length) {
            buff.append(Parts.COMMA).append(StringUtils.SPACE);
        }
    }

    /**
     * Appends SELECT expression
     * 
     * @param field
     * @param buffer
     */
    private void addSelectField(Serializable field, StringBuilder buffer) {
        QueryTuple tuple = resolve(field);
        appendFieldName(tuple, buffer);
    }

    /**
     * Appends to SELECT statement
     * 
     * @param fields
     * @param buffer
     */
    protected void appendSelect(Collection<Serializable> fields, StringBuilder buffer) {

        int length = fields.size() - CollectionUtils.SINGLETON;
        CollectionUtils.forEach(fields, (i, field) -> {
            addSelectField(field, buffer);
            appendComma(i, length, buffer);
        });
    }

    /**
     * Prepares query for GROUP BY clauses
     * 
     * @param buffer
     * @param clause
     */
    private static void prepareGroup(StringBuilder buffer, String clause) {

        if (StringUtils.valid(buffer)) {
            buffer.append(Parts.COMMA);
            buffer.append(StringUtils.SPACE);
        } else {
            buffer.append(clause);
        }
    }

    /**
     * Prepares query for ORDER BY clauses
     * 
     * @param clause
     */
    private void prepareOrderBy(String clause) {
        prepareGroup(orderBy, clause);
    }

    private void appendOrderByDir(String dir) {
        orderBy.append(StringUtils.SPACE).append(dir);
    }

    private void appendOrderBy(QueryTuple tuple, String dir) {

        orderBy.append(alias);
        orderBy.append(Parts.COLUMN_PREFIX).append(tuple.getFieldName());
        StringUtils.valid(dir, this::appendOrderByDir);
    }

    private void addOrderByField(String dir, Serializable field) {
        QueryTuple tuple = resolve(field);
        appendOrderBy(tuple, dir);
    }

    private void iterateAndAppendOrders(String dir, Serializable[] fields) {

        int length = fields.length - CollectionUtils.SINGLETON;
        CollectionUtils.forEach(fields, (i, field) -> {
            addOrderByField(dir, field);
            appendComma(i, length, orderBy);
        });
    }

    private void setValidOrder(String dir, Serializable[] fields) {
        prepareOrderBy(Orders.ORDER);
        iterateAndAppendOrders(dir, fields);
    }

    protected void setOrder(String dir, Serializable[] fields) {
        CollectionUtils.valid(fields, c -> setValidOrder(dir, c));
    }

    protected void setOrder(Serializable[] fields) {
        setOrder(null, fields);
    }

    private void appendGroupBy(QueryTuple tuple) {
        groupBy.append(alias);
        groupBy.append(Parts.COLUMN_PREFIX).append(tuple.getFieldName());
    }

    private void prepareGroupBy() {
        prepareGroup(groupBy, Clauses.GROUP);
    }

    private void addGroupByField(Serializable field, int index, int length) {

        QueryTuple tuple = resolve(field);
        appendGroupBy(tuple);
        appendFieldName(tuple, columns);
        appendComma(index, length, columns);
    }

    private void iterateAndAppendGroups(Collection<Serializable> fields) {

        int length = fields.size() - CollectionUtils.SINGLETON;
        CollectionUtils.forEach(fields, (i, field) -> {
            addGroupByField(field, i, length);
            appendComma(i, length, groupBy);
        });
    }

    /**
     * Appends aggregate clauses to JPA query body
     * 
     * @param fields
     */
    private void prepareAndAppendGroups(Collection<Serializable> fields) {
        prepareGroupBy();
        iterateAndAppendGroups(fields);
    }

    /**
     * Prepares GROUP BY clause
     * 
     * @param fields
     */
    protected void oppGroups(Collection<Serializable> fields) {
        CollectionUtils.valid(fields, c -> prepareAndAppendGroups(c));
    }

    /**
     * Removes new line element from query body
     */
    protected void removeNewLine() {
        StringUtils.removeLast(body, StringUtils.LINE);
    }

    /**
     * Appends query appropriated expression and new line
     * 
     * @param field
     * @param expression
     */
    protected void oppLine(Serializable field, String expression) {
        resolveAndOperateExpression(field, expression);
        newLine();
    }

    /**
     * Appends query appropriated expression and new line
     * 
     * @param field
     * @param value
     * @param expression
     */
    protected <F> void oppLine(Serializable field, F value, String expression) {
        resolveAndOperateExpression(field, expression, value);
        newLine();
    }

    /**
     * Appends query appropriated expression and new line
     * 
     * @param field
     * @param value1
     * @param value2
     * @param expression
     */
    protected <F> void oppLine(Serializable field, F value1, F value2, String expression) {
        opp(field, value1, value2, expression);
        newLine();
    }

    /**
     * Appends query appropriated expressions and new line
     * 
     * @param field
     * @param expression1
     * @param value1
     * @param expression2
     * @param value2
     */
    protected <F, E> void oppLine(Serializable field, String expression1, F value1, String expression2, E value2) {
        opp(field, expression1, value1, expression2, value2);
        newLine();
    }

    /**
     * Implements bracket JPA query part by
     * {@link org.lightmare.criteria.functions.QueryConsumer} implementation
     * 
     * @param consumer
     */
    private void consumeWithBrackets(QueryConsumer<T, JpaQueryStream<T>> consumer) {

        appendOperator();
        openBracket();
        consumer.accept(this);
        removeNewLine();
        closeBracket();
        newLine();
    }

    @Override
    public JpaQueryStream<T> brackets(QueryConsumer<T, JpaQueryStream<T>> consumer) {
        ObjectUtils.nonNull(consumer, this::consumeWithBrackets);
        return this;
    }

    @Override
    public JpaQueryStream<T> appendPrefix(Object clause) {
        prefix.append(clause);
        return this;
    }

    @Override
    public JpaQueryStream<T> appendFrom(Object clause) {
        from.append(clause);
        return this;
    }

    /**
     * Appends JOIN clause to JPA query
     * 
     * @param clause
     * @return
     */
    protected JpaQueryStream<T> appendJoin(Object clause) {
        joins.append(clause);
        return this;
    }

    @Override
    public JpaQueryStream<T> appendBody(Object clause) {
        body.append(clause);
        return this;
    }

    private void prepareSetClause() {
        StringUtils.valid(updateSet, AbstractAppenderStream::newLine);
    }

    private void setWhereClause() {
        StringUtils.valid(body, c -> sql.append(Clauses.WHERE));
    }

    /**
     * Clears generated SQL body
     */
    protected void clearSql() {
        StringUtils.clear(sql);
    }

    /**
     * Generates query by passed {@link StringBuilder} as prefix
     * 
     * @param startSql
     */
    protected void generateBody(StringBuilder startSql) {

        clearSql();
        sql.append(startSql);
        sql.append(joins);
        prepareSetClause();
        sql.append(updateSet);
        setWhereClause();
        sql.append(body);
    }

    @Override
    public String sql() {

        String value;

        clearSql();
        StringBuilder start = new StringBuilder(prefix).append(from);
        generateBody(start);
        sql.append(orderBy);
        sql.append(suffix);
        value = sql.toString();

        return value;
    }

    /**
     * Generates COUNT query body
     */
    private void countBody() {
        appendFromClause(entityType, alias, from);
    }

    /**
     * Generates COUNT query prefix
     */
    private void countPrefix() {

        StringUtils.clear(count);
        count.append(Clauses.SELECT);
        String countType = provider.getCountType(alias);
        count.append(Aggregates.COUNT.expression(countType));
        countBody();
        count.append(from);
    }

    @Override
    public String countSql() {

        String value;

        countPrefix();
        clearSql();
        generateBody(count);
        value = sql.toString();

        return value;
    }
}
