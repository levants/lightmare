package org.lightmare.linq.query;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.EntityManager;

import org.lightmare.linq.lambda.FieldGetter;
import org.lightmare.linq.links.Filters;
import org.lightmare.linq.links.Operators;

/**
 * Query builder from getter method references
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
abstract class GetterQueryStream<T extends Serializable> extends AbstractQueryStream<T> {

    protected GetterQueryStream(final EntityManager em, final Class<T> entityType) {
	super(em, entityType);
    }

    public <F> QueryStream<T> eq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.EQ);
	return this;
    }

    public <F> QueryStream<T> notEq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.NOT_EQ);
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

    public QueryStream<T> startsWith(FieldGetter<String> field, String value) throws IOException {
	String enrich = value.concat(Filters.LIKE_SIGN);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    public QueryStream<T> like(FieldGetter<String> field, String value) throws IOException {
	return startsWith(field, value);
    }

    public QueryStream<T> endsWith(FieldGetter<String> field, String value) throws IOException {
	String enrich = Filters.LIKE_SIGN.concat(value);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    public QueryStream<T> contains(FieldGetter<String> field, String value) throws IOException {
	String enrich = Filters.LIKE_SIGN.concat(value).concat(Filters.LIKE_SIGN);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    public <F> QueryStream<T> isNull(FieldGetter<F> field) throws IOException {
	oppLine(field, Operators.IS_NULL);
	return this;
    }

    public <F> QueryStream<T> notNull(FieldGetter<F> field) throws IOException {
	oppLine(field, Operators.NOT_NULL);
	return this;
    }
}
