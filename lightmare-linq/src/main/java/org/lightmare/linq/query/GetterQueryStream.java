package org.lightmare.linq.query;

import java.io.IOException;

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
public abstract class GetterQueryStream<T> extends AbstractQueryStream<T> {

    protected GetterQueryStream(final EntityManager em, final Class<T> entityType) {
	super(em, entityType);
    }

    public <F> AbstractQueryStream<T> eq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.EQ);
	return this;
    }

    public <F> AbstractQueryStream<T> notEq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.NOT_EQ);
	return this;
    }

    public <F> AbstractQueryStream<T> more(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE);
	return this;
    }

    public <F> AbstractQueryStream<T> less(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.LESS);
	return this;
    }

    public <F> AbstractQueryStream<T> moreOrEq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE_OR_EQ);
	return this;
    }

    public <F> AbstractQueryStream<T> lessOrEq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.LESS_OR_EQ);
	return this;
    }

    public AbstractQueryStream<T> startsWith(FieldGetter<String> field, String value) throws IOException {
	String enrich = value.concat(Filters.LIKE_SIGN);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    public AbstractQueryStream<T> like(FieldGetter<String> field, String value) throws IOException {
	return startsWith(field, value);
    }

    public AbstractQueryStream<T> endsWith(FieldGetter<String> field, String value) throws IOException {
	String enrich = Filters.LIKE_SIGN.concat(value);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    public AbstractQueryStream<T> contains(FieldGetter<String> field, String value) throws IOException {
	String enrich = Filters.LIKE_SIGN.concat(value).concat(Filters.LIKE_SIGN);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    public <F> AbstractQueryStream<T> isNull(FieldGetter<F> field) throws IOException {
	oppLine(field, Operators.IS_NULL);
	return this;
    }

    public <F> AbstractQueryStream<T> notNull(FieldGetter<F> field) throws IOException {
	oppLine(field, Operators.NOT_NULL);
	return this;
    }
}
