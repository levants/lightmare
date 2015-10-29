package org.lightmare.linq.query;

import java.io.IOException;

import javax.persistence.EntityManager;

import org.lightmare.linq.lambda.FieldSetter;
import org.lightmare.linq.links.Filters;
import org.lightmare.linq.links.Operators;

/**
 * Query builder from setter method references
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public abstract class SetterQueryStream<T> extends GetterQueryStream<T> {

    protected SetterQueryStream(EntityManager em, Class<T> entityType) {
	super(em, entityType);
    }

    @Override
    public <F> QueryStream<T> eq(FieldSetter<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.EQ);
	return this;
    }

    @Override
    public <F> QueryStream<T> notEq(FieldSetter<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.NOT_EQ);
	return this;
    }

    @Override
    public <F> QueryStream<T> more(FieldSetter<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE);
	return this;
    }

    @Override
    public <F> QueryStream<T> less(FieldSetter<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.LESS);
	return this;
    }

    @Override
    public <F> QueryStream<T> moreOrEq(FieldSetter<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE_OR_EQ);
	return this;
    }

    @Override
    public <F> QueryStream<T> lessOrEq(FieldSetter<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.LESS_OR_EQ);
	return this;
    }

    @Override
    public QueryStream<T> startsWith(FieldSetter<T, String> field, String value) throws IOException {
	String enrich = value.concat(Filters.LIKE_SIGN);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    @Override
    public QueryStream<T> like(FieldSetter<T, String> field, String value) throws IOException {
	return startsWith(field, value);
    }

    @Override
    public QueryStream<T> endsWith(FieldSetter<T, String> field, String value) throws IOException {
	String enrich = Filters.LIKE_SIGN.concat(value);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    @Override
    public QueryStream<T> contains(FieldSetter<T, String> field, String value) throws IOException {
	String enrich = Filters.LIKE_SIGN.concat(value).concat(Filters.LIKE_SIGN);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    @Override
    public QueryStream<T> isNull(FieldSetter<T, ?> field) throws IOException {
	oppLine(field, Operators.IS_NULL);
	return this;
    }

    @Override
    public QueryStream<T> notNull(FieldSetter<T, ?> field) throws IOException {
	oppLine(field, Operators.NOT_NULL);
	return this;
    }
}
