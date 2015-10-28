package org.lightmare.linq.query;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.lightmare.linq.cache.QueryCache;
import org.lightmare.linq.io.Replacements;
import org.lightmare.linq.resolvers.FieldResolver;
import org.lightmare.linq.tuples.ParameterTuple;
import org.lightmare.linq.tuples.QueryTuple;

/**
 * Utility class for lambda expression analyze and JPA-QL query generator
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public class QueryStream<T> {

    private final EntityManager em;

    protected final Class<T> entityType;

    private final StringBuilder prefix;

    private final StringBuilder body;

    private final StringBuilder suffix;

    private final StringBuilder sql;

    private int alias;

    private Set<ParameterTuple<?>> parameters;

    private boolean verbose;

    private static final String DEFAULT_ALIAS = "c";

    private static final char NEW_LINE = '\n';

    private static final int START = 0;

    private static final Logger LOG = Logger.getLogger(QueryStream.class);

    private QueryStream(final EntityManager em, final Class<T> entityType) {
	this.em = em;
	this.entityType = entityType;
	prefix = new StringBuilder();
	body = new StringBuilder();
	this.suffix = new StringBuilder();
	sql = new StringBuilder();
	parameters = new HashSet<>();
    }

    protected void setAlias(QueryTuple tuple) {

	if (tuple.hasNoAlias()) {
	    tuple.setAlias(alias);
	    alias++;
	}
    }

    private <F> QueryTuple compose(FieldGetter<F> field) throws IOException {

	QueryTuple tuple;

	SerializedLambda lambda = Replacements.getReplacement(field);
	tuple = QueryCache.getQuery(lambda);
	if (tuple == null) {
	    tuple = FieldResolver.resolve(lambda, verbose);
	    tuple.setAlias(DEFAULT_ALIAS);
	    QueryCache.putQuery(lambda, tuple);
	    FieldResolver.printVerbose(verbose, c -> LOG.info(String.format("Key %s is not bound to cache", lambda)));
	}

	return tuple;
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
	ParameterTuple<F> parameter = new ParameterTuple<F>(column, value, temporalType);
	parameters.add(parameter);
    }

    private <F> void opp(FieldGetter<F> field, F value, String expression) throws IOException {

	QueryTuple tuple = compose(field);
	String column = tuple.getField();
	body.append(tuple.getAlias()).append(QueryParts.COLUMN_PREFIX);
	body.append(column).append(expression);
	body.append(QueryParts.PARAM_PREFIX).append(column);
	addParameter(tuple, value);
    }

    private <F> void oppLine(FieldGetter<F> field, F value, String expression) throws IOException {
	opp(field, value, expression);
	body.append(NEW_LINE);
    }

    public <F> QueryStream<T> eq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.EQ);
	return this;
    }

    public <F> QueryStream<T> more(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE);
	return this;
    }

    public <F> QueryStream<T> less(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.LESS);
	return this;
    }

    public <F> QueryStream<T> moreOrEq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE_OR_EQ);
	return this;
    }

    public <F> QueryStream<T> lessOrEq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.LESS_OR_EQ);
	return this;
    }

    public <F> QueryStream<T> contains(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.CONTAINS);
	return this;
    }

    public QueryStream<T> startsWith(FieldGetter<String> field, String value) throws IOException {
	oppLine(field, value.concat(Filters.LIKE), Operators.STARTS_WITH);
	return this;
    }

    public QueryStream<T> like(FieldGetter<String> field, String value) throws IOException {
	return startsWith(field, value);
    }

    public QueryStream<T> endsWith(FieldGetter<String> field, String value) throws IOException {
	oppLine(field, Filters.LIKE.concat(value), Operators.STARTS_WITH);
	return this;
    }

    public QueryStream<T> contains(FieldGetter<String> field, String value) throws IOException {
	oppLine(field, Filters.LIKE.concat(value).concat(Filters.LIKE), Operators.STARTS_WITH);
	return this;
    }

    /**
     * Generates select statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link QueryStream} with select statement
     */
    public static <T> QueryStream<T> select(final EntityManager em, final Class<T> entityType,
	    final String entityAlias) {

	QueryStream<T> stream = new QueryStream<T>(em, entityType);

	stream.appendPrefix(Filters.SELECT).appendPrefix(entityAlias).appendPrefix(Filters.FROM);
	stream.appendPrefix(entityType.getName()).appendPrefix(Filters.AS).appendPrefix(entityAlias);
	stream.appendPrefix(NEW_LINE);

	return stream;
    }

    /**
     * Generates select statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link QueryStream} with select statement
     */
    public static <T> QueryStream<T> select(final EntityManager em, Class<T> entityType) {
	return select(em, entityType, DEFAULT_ALIAS);
    }

    private TypedQuery<T> initQuery() {

	TypedQuery<T> query = em.createQuery(sql(), entityType);
	parameters.forEach(c -> query.setParameter(c.getName(), c.getValue()));

	return query;
    }

    public List<T> toList() {

	List<T> results;

	TypedQuery<T> query = initQuery();
	results = query.getResultList();

	return results;
    }

    public T get() {

	T result;

	TypedQuery<T> query = initQuery();
	result = query.getSingleResult();

	return result;
    }

    public QueryStream<T> where() {
	body.append(Clauses.WHERE);
	return this;
    }

    public QueryStream<T> and() {
	body.append(Clauses.AND);
	return this;
    }

    public QueryStream<T> or() {
	body.append(Clauses.OR);
	return this;
    }

    public QueryStream<T> openBracket() {
	body.append(Operators.OPEN_BRACKET);
	return this;
    }

    public QueryStream<T> closeBracket() {
	body.append(Operators.CLOSE_BRACKET);
	return this;
    }

    public QueryStream<T> appendPrefix(Object clause) {
	prefix.append(clause);
	return this;
    }

    public QueryStream<T> appendBody(Object clause) {
	body.append(clause);
	return this;
    }

    public String sql() {

	sql.delete(START, sql.length());
	sql.append(prefix);
	sql.append(body);
	sql.append(suffix);

	return sql.toString();
    }

    public Set<ParameterTuple<?>> getParameters() {
	return parameters;
    }

    public void setWerbose(boolean verbose) {
	this.verbose = verbose;
    }

    @Override
    public String toString() {
	return sql.toString();
    }
}
