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
package org.lightmare.criteria.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.lightmare.criteria.cache.QueryCache;
import org.lightmare.criteria.lambda.LambdaData;
import org.lightmare.criteria.lambda.LambdaReplacements;
import org.lightmare.criteria.links.Clauses;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.links.QueryParts;
import org.lightmare.criteria.resolvers.FieldResolver;
import org.lightmare.criteria.tuples.ParameterTuple;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.verbose.VerboseUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Abstract class for lambda expression analyze and JPA query generator
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
abstract class AbstractQueryStream<T extends Serializable> implements QueryStream<T> {

    protected final EntityManager em;

    protected final Class<T> entityType;

    protected final String alias;

    protected final StringBuilder prefix = new StringBuilder();

    protected final StringBuilder body = new StringBuilder();

    protected final StringBuilder suffix = new StringBuilder();

    protected final StringBuilder sql = new StringBuilder();

    protected int alias_suffix;

    protected final Set<ParameterTuple> parameters = new HashSet<>();

    protected boolean verbose;

    private static final Logger LOG = Logger.getLogger(FullQueryStream.class);

    protected AbstractQueryStream(final EntityManager em, final Class<T> entityType, final String alias) {
	this.em = em;
	this.entityType = entityType;
	this.alias = alias;
    }

    public T instance() throws IOException {
	return ClassUtils.instantiate(entityType);
    }

    protected void setAlias(QueryTuple tuple) {

	if (tuple.hasNoAlias()) {
	    tuple.setAlias(alias_suffix);
	    alias_suffix++;
	}
    }

    protected QueryTuple compose(Object field) throws IOException {

	QueryTuple tuple;

	LambdaData lambda = LambdaReplacements.getReplacement(field);
	tuple = QueryCache.getQuery(lambda);
	if (tuple == null) {
	    tuple = FieldResolver.resolve(lambda);
	    tuple.setAlias(DEFAULT_ALIAS);
	    QueryCache.putQuery(lambda, tuple);
	    VerboseUtils.apply(verbose, c -> LOG.info(String.format("Key %s is not bound to cache", lambda)));
	}

	return tuple;
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
    private <F> void addParameter(QueryTuple tuple, F value) {

	String column = tuple.getField();
	TemporalType temporalType = tuple.getTemporalType();
	addParameter(column, value, temporalType);
    }

    protected QueryTuple opp(Object field, String expression) throws IOException {

	QueryTuple tuple = compose(field);

	String column = tuple.getField();
	body.append(tuple.getAlias()).append(QueryParts.COLUMN_PREFIX);
	body.append(column).append(expression);

	return tuple;
    }

    protected <F> void opp(Object field, F value, String expression) throws IOException {

	QueryTuple tuple = opp(field, expression);
	body.append(QueryParts.PARAM_PREFIX).append(tuple.getField());
	addParameter(tuple, value);
    }

    protected void oppLine(Object field, String expression) throws IOException {
	opp(field, expression);
	body.append(NEW_LINE);
    }

    protected <F> void oppLine(Object field, F value, String expression) throws IOException {
	opp(field, value, expression);
	body.append(NEW_LINE);
    }

    private void setParameters(Query query) {
	parameters.forEach(c -> query.setParameter(c.getName(), c.getValue()));
    }

    /**
     * Creates {@link TypedQuery} from generated SQL for SELECT statements
     * 
     * @return {@link TypedQuery} for entity type
     */
    private TypedQuery<T> initTypedQuery() {

	TypedQuery<T> query;

	String sqlText = sql();
	query = em.createQuery(sqlText, entityType);
	setParameters(query);

	return query;
    }

    /**
     * Creates {@link Query} from generated SQL for UPDATE or DELETE statements
     * 
     * @return for bulk modification
     */
    private Query initBulkQuery() {

	Query query;

	String sqlText = sql();
	query = em.createQuery(sqlText);
	setParameters(query);

	return query;
    }

    @Override
    public QueryStream<T> where() {
	body.append(Clauses.WHERE);
	return this;
    }

    @Override
    public QueryStream<T> and() {
	body.append(Clauses.AND);
	return this;
    }

    @Override
    public QueryStream<T> or() {
	body.append(Clauses.OR);
	return this;
    }

    @Override
    public QueryStream<T> openBracket() {
	body.append(Operators.OPEN_BRACKET);
	return this;
    }

    @Override
    public QueryStream<T> closeBracket() {
	body.append(Operators.CLOSE_BRACKET);
	return this;
    }

    @Override
    public QueryStream<T> appendPrefix(Object clause) {
	prefix.append(clause);
	return this;
    }

    @Override
    public QueryStream<T> appendBody(Object clause) {
	body.append(clause);
	return this;
    }

    @Override
    public String sql() {

	sql.delete(START, sql.length());
	sql.append(prefix);
	sql.append(body);
	sql.append(suffix);

	return sql.toString();
    }

    @Override
    public List<T> toList() {

	List<T> results;

	TypedQuery<T> query = initTypedQuery();
	results = query.getResultList();

	return results;
    }

    @Override
    public T get() {

	T result;

	TypedQuery<T> query = initTypedQuery();
	result = query.getSingleResult();

	return result;
    }

    @Override
    public int execute() {

	int result;

	Query query = initBulkQuery();
	result = query.executeUpdate();

	return result;
    }

    @Override
    public Set<ParameterTuple> getParameters() {
	return parameters;
    }

    @Override
    public void setWerbose(boolean verbose) {
	this.verbose = verbose;
    }

    @Override
    public String toString() {
	return sql.toString();
    }
}
