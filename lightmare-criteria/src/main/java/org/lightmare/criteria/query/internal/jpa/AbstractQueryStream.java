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
package org.lightmare.criteria.query.internal.jpa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.lambda.LambdaUtils;
import org.lightmare.criteria.links.Clauses;
import org.lightmare.criteria.links.Filters;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.links.Orders;
import org.lightmare.criteria.links.Parts;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.tuples.AliasTuple;
import org.lightmare.criteria.tuples.ParameterTuple;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Abstract class for lambda expression analyze and JPA query generator
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 */
public abstract class AbstractQueryStream<T> extends AbstractJPAQueryWrapper<T> implements QueryStream<T> {

    protected final EntityManager em;

    protected final Class<T> entityType;

    protected final String alias;

    protected final StringBuilder prefix = new StringBuilder();

    protected final StringBuilder count = new StringBuilder();

    protected final StringBuilder columns = new StringBuilder();

    protected final StringBuilder joins = new StringBuilder();

    protected final StringBuilder updateSet = new StringBuilder();

    protected final StringBuilder body = new StringBuilder();

    protected final StringBuilder suffix = new StringBuilder();

    protected final StringBuilder orderBy = new StringBuilder();

    protected final StringBuilder sql = new StringBuilder();

    protected int alias_suffix;

    private int parameter_counter;

    private int alias_counter = -1;

    protected final Set<ParameterTuple> parameters = new HashSet<>();

    protected AbstractQueryStream(final EntityManager em, final Class<T> entityType, final String alias) {
        this.em = em;
        this.entityType = entityType;
        this.alias = alias;
    }

    /**
     * Generates SELECT statement prefix
     * 
     * @param stream
     */
    protected static <T> void appendSelect(AbstractQueryStream<T> stream) {
        stream.appendPrefix(Filters.SELECT).appendPrefix(stream.alias).appendPrefix(Filters.FROM);
    }

    /**
     * Appends entity and alias part to stream
     * 
     * @param stream
     */
    protected static <T> void appendEntityPart(AbstractQueryStream<T> stream) {

        stream.appendPrefix(stream.entityType.getName());
        stream.appendPrefix(Filters.AS);
        stream.appendPrefix(stream.alias);
        stream.appendPrefix(StringUtils.NEWLINE);
    }

    /**
     * Creates SELECT statement prefix
     * 
     * @param stream
     */
    protected static <T> void startsSelect(AbstractQueryStream<T> stream) {
        appendSelect(stream);
        appendEntityPart(stream);
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
    protected void newLine() {
        newLine(body);
    }

    protected static void appendFromClause(String typeName, String alias, StringBuilder buff) {

        buff.append(Filters.FROM);
        buff.append(typeName);
        buff.append(Filters.AS);
        buff.append(alias);
        newLine(buff);
    }

    protected static void appendFromClause(Class<?> type, String alias, StringBuilder buff) {
        appendFromClause(type.getName(), alias, buff);
    }

    protected static void appendFieldName(QueryTuple tuple, StringBuilder buffer) {
        appendFieldName(tuple.getAlias(), tuple.getFieldName(), buffer);
    }

    protected void setAlias(QueryTuple tuple) {

        if (tuple.hasNoAlias()) {
            tuple.setAlias(alias_suffix);
            alias_suffix++;
        }
    }

    /**
     * Gets appropriated {@link QueryTuple} from cache or generates from
     * compiled class
     * 
     * @param field
     * @return {@link QueryTuple} for passed lambda function
     */
    protected QueryTuple compose(Object field) {

        QueryTuple tuple = LambdaUtils.getOrInit(field);
        tuple.setAlias(alias);

        return tuple;
    }

    /**
     * Gets appropriated entity {@link Class} for {@link EntityField} lambda
     * expression
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

    /**
     * Increments parameters counter
     */
    private void incrementParameterCounter() {
        parameter_counter++;
    }

    private String generateParameterName(String column) {
        return column.concat(String.valueOf(parameter_counter));
    }

    private String generateParameterName(QueryTuple tuple) {

        String parameterName;

        String column = tuple.getFieldName();
        parameterName = generateParameterName(column);

        return parameterName;
    }

    @Override
    public void addParameter(String key, Object value, TemporalType temporalType) {

        ParameterTuple parameter = new ParameterTuple(key, value, temporalType);
        parameters.add(parameter);
    }

    @Override
    public void addParameter(String key, Object value) {
        addParameter(key, value, null);
    }

    @Override
    public void addParameters(Map<String, Object> parameters) {

        if (CollectionUtils.valid(parameters)) {
            parameters.forEach((key, value) -> addParameter(key, value));
        }
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
        incrementParameterCounter();
    }

    protected void oppWithParameter(QueryTuple tuple, Object value, StringBuilder sqlPart) {

        String parameterName = generateParameterName(tuple);
        sqlPart.append(Parts.PARAM_PREFIX).append(parameterName);
        addParameter(parameterName, tuple, value);
    }

    protected QueryTuple opp(Object field, String expression) {

        QueryTuple tuple = compose(field);

        appendFieldName(tuple, body);
        appendBody(expression);

        return tuple;
    }

    protected <F> void opp(Object field, F value, String expression) {
        QueryTuple tuple = opp(field, expression);
        oppWithParameter(tuple, value, body);
    }

    protected QueryTuple appSubQuery(Object field, String expression) {

        QueryTuple tuple = opp(field, expression);
        openBracket();

        return tuple;
    }

    protected void oppCollection(Object field, Collection<?> value, String expression) {

        QueryTuple tuple = appSubQuery(field, expression);
        oppWithParameter(tuple, value, body);
        closeBracket();
        newLine();
    }

    protected void oppCollection(Object field, Collection<?> values) {
        oppCollection(field, values, Operators.IN);
    }

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
     * Append default boolean operator
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
    protected void oppField(Object field1, Object field2, String expression) {

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
    protected void oppCollectionField(Object field1, Object field2, String expression) {

        opp(field1, expression);
        QueryTuple tuple = compose(field2);
        openBracket();
        appendColumn(tuple);
        closeBracket();
        newLine();
    }

    private void appendSetClause() {

        if (StringUtils.valid(updateSet)) {
            updateSet.append(Parts.COMMA);
            newLine(updateSet);
            updateSet.append(Clauses.SET_SPACE);
        } else {
            updateSet.append(Clauses.SET);
        }
    }

    private void appendSetClause(QueryTuple tuple) {
        appendFieldName(tuple, updateSet);
        updateSet.append(Operators.EQ);
    }

    protected <F> void setOpp(Object field, F value) {

        QueryTuple tuple = compose(field);
        appendSetClause();
        appendSetClause(tuple);
        oppWithParameter(tuple, value, updateSet);
    }

    private void appendSelect(EntityField<T, ?>[] fields) {

        int length = fields.length - CollectionUtils.SINGLTON_LENGTH;
        EntityField<T, ?> field;
        QueryTuple tuple;
        for (int i = CollectionUtils.FIRST_INDEX; i <= length; i++) {
            field = fields[i];
            tuple = compose(field);
            appendFieldName(tuple, columns);
            if (i < length) {
                columns.append(Parts.COMMA);
                columns.append(StringUtils.SPACE);
            }
        }
    }

    @SafeVarargs
    protected final void oppSelect(EntityField<T, ?>... fields) {

        if (CollectionUtils.valid(fields)) {
            columns.append(Filters.SELECT);
            appendSelect(fields);
        }
    }

    private void prepareOrderBy() {

        if (StringUtils.valid(orderBy)) {
            orderBy.append(Parts.COMMA);
            orderBy.append(StringUtils.SPACE);
        } else {
            orderBy.append(Orders.ORDER);
        }
    }

    private void appendOrderBy(QueryTuple tuple, String dir) {

        orderBy.append(alias);
        orderBy.append(Parts.COLUMN_PREFIX).append(tuple.getFieldName());
        if (StringUtils.valid(dir)) {
            orderBy.append(StringUtils.SPACE).append(dir);
        }
    }

    private void iterateAndAppendOrders(String dir, Object[] fields) {

        Object field;
        QueryTuple tuple;
        int length = fields.length - CollectionUtils.SINGLTON_LENGTH;
        for (int i = CollectionUtils.FIRST_INDEX; i <= length; i++) {
            field = fields[i];
            tuple = compose(field);
            appendOrderBy(tuple, dir);
            if (i < length) {
                orderBy.append(Parts.COMMA).append(StringUtils.SPACE);
            }
        }
    }

    protected void setOrder(String dir, Object[] fields) {

        if (CollectionUtils.valid(fields)) {
            prepareOrderBy();
            iterateAndAppendOrders(dir, fields);
        }
    }

    protected void setOrder(Object[] fields) {
        setOrder(null, fields);
    }

    protected void removeNewLine() {

        int last = body.length();
        int first = last - CollectionUtils.SINGLTON_LENGTH;
        if (body.charAt(first) == StringUtils.LINE) {
            body.delete(first, last);
        }
    }

    protected void oppLine(Object field, String expression) {
        opp(field, expression);
        newLine();
    }

    protected <F> void oppLine(Object field, F value, String expression) {
        opp(field, value, expression);
        newLine();
    }

    private void setParameters(Query query) {
        setJPAConfiguration(query);
        parameters.forEach(c -> query.setParameter(c.getName(), c.getValue()));
    }

    /**
     * Creates {@link TypedQuery} from generated SQL for SELECT statements
     * 
     * @return {@link TypedQuery} for entity type
     */
    protected TypedQuery<T> initTypedQuery() {

        TypedQuery<T> query;

        String sqlText = sql();
        query = em.createQuery(sqlText, entityType);
        setParameters(query);

        return query;
    }

    /**
     * Generates {@link TypedQuery} for COUNT JPA-QL statement
     * 
     * @return {@link TypedQuery} with {@link Long} type for element count
     */
    protected TypedQuery<Long> initCountQuery() {

        TypedQuery<Long> query;

        String sqlText = countSql();
        query = em.createQuery(sqlText, Long.class);
        setParameters(query);

        return query;
    }

    /**
     * Creates {@link Query} from generated SQL for UPDATE or DELETE statements
     * 
     * @return for bulk modification
     */
    protected Query initBulkQuery() {

        Query query;

        String sqlText = sql();
        query = em.createQuery(sqlText);
        setParameters(query);

        return query;
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

    protected void generateBody(CharSequence startSql) {

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

    private void countBody() {
        appendFromClause(entityType, alias, count);
    }

    private void countPrefix() {

        StringUtils.clear(count);
        count.append(Filters.SELECT);
        count.append(Filters.COUNT);
        count.append(alias);
        count.append(Filters.CLOSE_COUNT);
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

    /**
     * Generates {@link AliasTuple} instance with incremented counter for sub
     * queries
     * 
     * @return {@link AliasTuple} with incremented counter
     */
    public AliasTuple getAliasTuple() {

        AliasTuple tuple;

        alias_counter++;
        tuple = new AliasTuple(alias, alias_counter);

        return tuple;
    }
}