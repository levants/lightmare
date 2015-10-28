package org.lightmare.linq.query;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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

    private final StringBuilder suffix;

    private final StringBuilder sql;

    private int alias;

    private Set<ParameterTuple<?>> parameters;

    private boolean verbose;

    private static final String DEFAULT_ALIAS = "c";

    private static final char NEW_LINE = '\n';

    private static final int START = 0;

    private QueryStream(final EntityManager em, final Class<T> entityType) {
	this.em = em;
	this.entityType = entityType;
	prefix = new StringBuilder();
	suffix = new StringBuilder();
	sql = new StringBuilder();
	parameters = new HashSet<>();
    }

    protected void setAlias(QueryTuple tuple) {

	if (tuple.hasNoAlias()) {
	    tuple.setAlias(alias);
	    alias++;
	}
    }

    private <F> QueryTuple compose(FieldCaller<F> field) throws IOException {

	QueryTuple tuple;

	SerializedLambda lambda = Replacements.getReplacement(field);
	String key = lambda.toString();
	tuple = QueryCache.getQuery(key);
	if (tuple == null) {
	    tuple = FieldResolver.resolve(lambda, verbose);
	    tuple.setAlias(DEFAULT_ALIAS);
	}

	return tuple;
    }

    private <F> QueryTuple opp(FieldCaller<F> field) throws IOException {
	QueryTuple tuple = compose(field);
	return tuple;
    }

    private <F> void opp(FieldCaller<F> field, F value, String expression) throws IOException {

	QueryTuple tuple = opp(field);
	String column = tuple.getField();
	suffix.append(tuple.getAlias()).append(QueryParts.COLUMN_PREFIX).append(column).append(expression)
		.append(QueryParts.PARAM_PREFIX).append(column);
	ParameterTuple<F> parameter = new ParameterTuple<F>(column, value);
	parameters.add(parameter);
    }

    private <F> void oppLine(FieldCaller<F> field, F value, String expression) throws IOException {
	opp(field, value, expression);
	suffix.append(NEW_LINE);
    }

    public <F> QueryStream<T> eq(FieldCaller<F> field, F value) throws IOException {
	oppLine(field, value, Operators.EQ);
	return this;
    }

    public <F> QueryStream<T> more(FieldCaller<F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE);
	return this;
    }

    public <F> QueryStream<T> less(FieldCaller<F> field, F value) throws IOException {
	oppLine(field, value, Operators.LESS);
	return this;
    }

    public <F> QueryStream<T> moreOrEq(FieldCaller<F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE_OR_EQ);
	return this;
    }

    public <F> QueryStream<T> lessOrEq(FieldCaller<F> field, F value) throws IOException {
	oppLine(field, value, Operators.LESS_OR_EQ);
	return this;
    }

    public <F> QueryStream<T> contains(FieldCaller<F> field, F value) throws IOException {
	oppLine(field, value, Operators.CONTAINS);
	return this;
    }

    public QueryStream<T> startsWith(FieldCaller<String> field, String value) throws IOException {
	oppLine(field, value.concat(Filters.LIKE), Operators.STARTS_WITH);
	return this;
    }

    public QueryStream<T> like(FieldCaller<String> field, String value) throws IOException {
	return startsWith(field, value);
    }

    public QueryStream<T> endsWith(FieldCaller<String> field, String value) throws IOException {
	oppLine(field, Filters.LIKE.concat(value), Operators.STARTS_WITH);
	return this;
    }

    public QueryStream<T> contains(FieldCaller<String> field, String value) throws IOException {
	oppLine(field, Filters.LIKE.concat(value).concat(Filters.LIKE), Operators.STARTS_WITH);
	return this;
    }

    public static <T> QueryStream<T> select(final EntityManager em, final Class<T> entityType,
	    final String entityAlias) {

	QueryStream<T> stream = new QueryStream<T>(em, entityType);

	stream.prefix.append(Filters.SELECT).append(entityAlias).append(Filters.FROM).append(entityType.getName())
		.append(Filters.AS).append(entityAlias).append(NEW_LINE);

	return stream;
    }

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
	suffix.append(Clauses.WHERE);
	return this;
    }

    public QueryStream<T> and() {
	suffix.append(Clauses.AND);
	return this;
    }

    public QueryStream<T> or() {
	suffix.append(Clauses.OR);
	return this;
    }

    public QueryStream<T> openBracket() {
	suffix.append(Operators.OPEN_BRACKET);
	return this;
    }

    public QueryStream<T> closeBracket() {
	suffix.append(Operators.CLOSE_BRACKET);
	return this;
    }

    public String sql() {
	sql.delete(START, sql.length()).append(prefix).append(suffix);
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
