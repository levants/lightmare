package org.lightmare.criteria.query;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.EntityManager;

import org.lightmare.criteria.lambda.EntityField;
import org.lightmare.criteria.links.Filters;
import org.lightmare.criteria.links.Operators;

/**
 * Query builder from setter method references
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
abstract class EntityQueryStream<T extends Serializable> extends GetterQueryStream<T> {

    protected EntityQueryStream(EntityManager em, Class<T> entityType) {
	super(em, entityType);
    }

    @Override
    public <F> QueryStream<T> eq(EntityField<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.EQ);
	return this;
    }

    @Override
    public <F> QueryStream<T> notEq(EntityField<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.NOT_EQ);
	return this;
    }

    @Override
    public <F> QueryStream<T> more(EntityField<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE);
	return this;
    }

    @Override
    public <F> QueryStream<T> less(EntityField<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.LESS);
	return this;
    }

    @Override
    public <F> QueryStream<T> moreOrEq(EntityField<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE_OR_EQ);
	return this;
    }

    @Override
    public <F> QueryStream<T> lessOrEq(EntityField<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.LESS_OR_EQ);
	return this;
    }

    @Override
    public QueryStream<T> startsWith(EntityField<T, String> field, String value) throws IOException {
	String enrich = value.concat(Filters.LIKE_SIGN);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    @Override
    public QueryStream<T> like(EntityField<T, String> field, String value) throws IOException {
	return startsWith(field, value);
    }

    @Override
    public QueryStream<T> endsWith(EntityField<T, String> field, String value) throws IOException {
	String enrich = Filters.LIKE_SIGN.concat(value);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    @Override
    public QueryStream<T> contains(EntityField<T, String> field, String value) throws IOException {
	String enrich = Filters.LIKE_SIGN.concat(value).concat(Filters.LIKE_SIGN);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    @Override
    public QueryStream<T> isNull(EntityField<T, ?> field) throws IOException {
	oppLine(field, Operators.IS_NULL);
	return this;
    }

    @Override
    public QueryStream<T> notNull(EntityField<T, ?> field) throws IOException {
	oppLine(field, Operators.NOT_NULL);
	return this;
    }
}
