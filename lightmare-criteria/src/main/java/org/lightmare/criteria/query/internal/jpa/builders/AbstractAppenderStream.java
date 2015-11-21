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
import java.util.Collection;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Aggregates;
import org.lightmare.criteria.query.internal.jpa.links.Clauses;
import org.lightmare.criteria.query.internal.jpa.links.Filters;
import org.lightmare.criteria.query.internal.jpa.links.Operators;
import org.lightmare.criteria.query.internal.jpa.links.Orders;
import org.lightmare.criteria.query.internal.jpa.links.Parts;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Utility class to build JPA query
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
abstract class AbstractAppenderStream<T> extends GeneralQueryStream<T> {

    protected final StringBuilder prefix = new StringBuilder();

    protected final StringBuilder count = new StringBuilder();

    protected final StringBuilder columns = new StringBuilder();

    protected final StringBuilder joins = new StringBuilder();

    protected final StringBuilder updateSet = new StringBuilder();

    protected final StringBuilder body = new StringBuilder();

    protected final StringBuilder suffix = new StringBuilder();

    protected final StringBuilder groupBy = new StringBuilder();

    protected final StringBuilder orderBy = new StringBuilder();

    protected final StringBuilder having = new StringBuilder();

    protected final StringBuilder sql = new StringBuilder();

    protected AbstractAppenderStream(final EntityManager em, final Class<T> entityType, final String alias) {
        super(em, entityType, alias);
    }

    /**
     * Adds new line to parameter
     * 
     * @param buff
     */
    protected static void newLine(StringBuilder buff) {
        buff.append(StringUtils.NEWLINE);
    }

    /**
     * Adds new line to query body
     */
    public void newLine() {

        if (StringUtils.notEndsWith(body, StringUtils.NEWLINE)) {
            newLine(body);
        }
    }

    /**
     * Adds query part to from clause
     * 
     * @param typeName
     * @param alias
     * @param buff
     */
    protected static void appendFromClause(String typeName, String alias, StringBuilder buff) {

        buff.append(Filters.FROM);
        buff.append(typeName);
        buff.append(Filters.AS);
        buff.append(alias);
        newLine(buff);
    }

    /**
     * Adds query part to from clause
     * 
     * @param type
     * @param alias
     * @param buff
     */
    protected static void appendFromClause(Class<?> type, String alias, StringBuilder buff) {
        appendFromClause(type.getName(), alias, buff);
    }

    /**
     * Adds query part to from clause
     * 
     * @param tuple
     */
    protected void appendFieldName(QueryTuple tuple) {
        appendFieldName(tuple.getAlias(), tuple.getFieldName(), body);
    }

    /**
     * Generates query part for field and expression
     * 
     * @param field
     * @param expression
     * @return {@link QueryTuple} for field
     */
    protected QueryTuple opp(Serializable field, String expression) {

        QueryTuple tuple = compose(field);

        appendFieldName(tuple, body);
        appendBody(expression);

        return tuple;
    }

    /**
     * Generates query part for field and expression and adds parameter
     * 
     * @param field
     * @param value
     * @param expression
     */
    protected <F> void opp(Serializable field, F value, String expression) {
        QueryTuple tuple = opp(field, expression);
        oppWithParameter(tuple, value, body);
    }

    /**
     * Generates query part for field and expression and adds parameter
     * 
     * @param field
     * @param value
     * @param expression
     */
    protected <F> void opp(Serializable field, F value1, F value2, String expression) {
        QueryTuple tuple = opp(field, expression);
        oppWithParameter(tuple, value1, body);
    }

    /**
     * Generates sub query part with appropriated expression
     * 
     * @param field
     * @param expression
     * @return {@link QueryTuple} for sub query field
     */
    protected QueryTuple appSubQuery(Serializable field, String expression) {
        QueryTuple tuple = opp(field, expression);
        openBracket();

        return tuple;
    }

    /**
     * Generates query part for {@link Collection} parameter
     * 
     * @param field
     * @param value
     * @param expression
     */
    protected void oppCollection(Serializable field, Collection<?> value, String expression) {

        QueryTuple tuple = appSubQuery(field, expression);
        oppWithParameter(tuple, value, body);
        closeBracket();
        newLine();
    }

    /**
     * Generates query part for {@link Collection} parameter
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
        return validForOperator(Clauses.AND, Clauses.OR, Clauses.WHERE, Operators.OPEN_BRACKET);
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

        opp(field1, expression);
        QueryTuple tuple = compose(field2);
        appendColumn(tuple);
        newLine();
    }

    /**
     * Processes query part with other field with {@link Collection} type
     * 
     * @param field1
     * @param field2
     * @param expression
     */
    protected void oppCollectionField(Serializable field1, Serializable field2, String expression) {

        opp(field1, expression);
        QueryTuple tuple = compose(field2);
        openBracket();
        appendColumn(tuple);
        closeBracket();
        newLine();
    }

    /**
     * Appends to SET clause for UPDATE query
     */
    private void appendSetClause() {

        if (StringUtils.valid(updateSet)) {
            updateSet.append(Parts.COMMA);
            newLine(updateSet);
            updateSet.append(Clauses.SET_SPACE);
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
     * Appends SELECT expression
     * 
     * @param field
     */
    private void addSelectField(Serializable field) {
        QueryTuple tuple = compose(field);
        appendFieldName(tuple, columns);
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
     * Appends to SELECT statement
     * 
     * @param fields
     */
    private void appendSelect(Serializable[] fields) {

        int length = fields.length - CollectionUtils.SINGLTON_LENGTH;
        Serializable field;
        for (int i = CollectionUtils.FIRST_INDEX; i <= length; i++) {
            field = fields[i];
            addSelectField(field);
            appendComma(i, length, columns);
        }
    }

    @SafeVarargs
    protected final void oppSelect(Serializable... fields) {

        if (CollectionUtils.valid(fields)) {
            columns.append(Filters.SELECT);
            appendSelect(fields);
        }
    }

    private static void prepareGroup(StringBuilder buffer, String clause) {

        if (StringUtils.valid(buffer)) {
            buffer.append(Parts.COMMA);
            buffer.append(StringUtils.SPACE);
        } else {
            buffer.append(clause);
        }
    }

    private void prepareOrderBy(String clause) {
        prepareGroup(orderBy, clause);
    }

    private void appendOrderBy(QueryTuple tuple, String dir) {

        orderBy.append(alias);
        orderBy.append(Parts.COLUMN_PREFIX).append(tuple.getFieldName());
        if (StringUtils.valid(dir)) {
            orderBy.append(StringUtils.SPACE).append(dir);
        }
    }

    private void addOrderByField(String dir, Serializable field) {
        QueryTuple tuple = compose(field);
        appendOrderBy(tuple, dir);
    }

    private void iterateAndAppendOrders(String dir, Serializable[] fields) {

        Serializable field;
        int length = fields.length - CollectionUtils.SINGLTON_LENGTH;
        for (int i = CollectionUtils.FIRST_INDEX; i <= length; i++) {
            field = fields[i];
            addOrderByField(dir, field);
            appendComma(i, length, orderBy);
        }
    }

    protected void setOrder(String dir, Serializable[] fields) {

        if (CollectionUtils.valid(fields)) {
            prepareOrderBy(Orders.ORDER);
            iterateAndAppendOrders(dir, fields);
        }
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

        QueryTuple tuple = compose(field);
        appendGroupBy(tuple);
        appendFieldName(tuple, columns);
        appendComma(index, length, columns);
    }

    private void iterateAndAppendGroups(Serializable[] fields) {

        Serializable field;
        int length = fields.length - CollectionUtils.SINGLTON_LENGTH;
        for (int i = CollectionUtils.FIRST_INDEX; i <= length; i++) {
            field = fields[i];
            addGroupByField(field, i, length);
            appendComma(i, length, groupBy);
        }
    }

    /**
     * Prepares GROUP BY clause
     * 
     * @param fields
     */
    @SafeVarargs
    protected final void oppGroups(Serializable... fields) {

        if (CollectionUtils.valid(fields)) {
            prepareGroupBy();
            iterateAndAppendGroups(fields);
        }
    }

    protected void removeNewLine() {

        int last = body.length();
        int first = last - CollectionUtils.SINGLTON_LENGTH;
        if (body.charAt(first) == StringUtils.LINE) {
            body.delete(first, last);
        }
    }

    /**
     * Appends query appropriated expression and new line
     * 
     * @param field
     * @param expression
     */
    protected void oppLine(Serializable field, String expression) {
        opp(field, expression);
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
        opp(field, value, expression);
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

    @Override
    public QueryStream<T> brackets(QueryConsumer<T> consumer) {

        openBracket();
        consumer.accept(this);
        removeNewLine();
        closeBracket();

        return this;
    }

    @Override
    public QueryStream<T> appendPrefix(Object clause) {
        prefix.append(clause);
        return this;
    }

    protected QueryStream<T> appendJoin(Object clause) {
        joins.append(clause);
        return this;
    }

    @Override
    public QueryStream<T> appendBody(Object clause) {
        body.append(clause);
        return this;
    }

    private void prepareSetClause() {

        if (StringUtils.valid(updateSet)) {
            newLine(updateSet);
        }
    }

    private void setWhereClause() {

        if (StringUtils.valid(body)) {
            sql.append(Clauses.WHERE);
        }
    }

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
        generateBody(prefix);
        sql.append(orderBy);
        sql.append(suffix);
        value = sql.toString();

        return value;
    }

    /**
     * Generates COUNT query body
     */
    private void countBody() {
        appendFromClause(entityType, alias, count);
    }

    /**
     * Generates COUNT query prefix
     */
    private void countPrefix() {

        StringUtils.clear(count);
        count.append(Filters.SELECT);
        count.append(Aggregates.COUNT.expression(alias));
        countBody();
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
