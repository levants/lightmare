package org.lightmare.criteria.query.jpa.subqueries;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.lightmare.criteria.lambda.EntityField;
import org.lightmare.criteria.lambda.QueryConsumer;
import org.lightmare.criteria.query.EntityQueryStream;
import org.lightmare.criteria.query.SubQueryStream;

/**
 * Main class to operate on sub queries and generate query clauses
 * 
 * @author Levan Tsinadze
 *
 * @param <S>
 *            entity type for generated (sub) query
 * @param <T>
 *            entity type for generated query
 */
abstract class DirectctSubQueryStream<S extends Serializable, T extends Serializable> extends EntityQueryStream<S>
	implements SubQueryStream<S, T> {

    protected DirectctSubQueryStream(EntityManager em, Class<S> entityType, String alias) {
	super(em, entityType, alias);
    }

    // ================= entity QL methods ===================================//

    @Override
    public <F> SubQueryStream<S, T> equals(EntityField<S, F> field, F value) throws IOException {
	super.equals(field, value);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> notEquals(EntityField<S, F> field, F value) throws IOException {
	super.notEquals(field, value);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> more(EntityField<S, F> field, F value) throws IOException {
	super.more(field, value);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> less(EntityField<S, F> field, F value) throws IOException {
	super.less(field, value);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> moreOrEquals(EntityField<S, F> field, F value) throws IOException {
	super.moreOrEquals(field, value);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> lessOrEquals(EntityField<S, F> field, F value) throws IOException {
	super.lessOrEquals(field, value);
	return this;
    }

    @Override
    public SubQueryStream<S, T> startsWith(EntityField<S, String> field, String value) throws IOException {
	super.startsWith(field, value);
	return this;
    }

    @Override
    public SubQueryStream<S, T> like(EntityField<S, String> field, String value) throws IOException {
	super.like(field, value);
	return this;
    }

    @Override
    public SubQueryStream<S, T> endsWith(EntityField<S, String> field, String value) throws IOException {
	super.endsWith(field, value);
	return this;
    }

    @Override
    public SubQueryStream<S, T> contains(EntityField<S, String> field, String value) throws IOException {
	super.startsWith(field, value);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> in(EntityField<S, F> field, Collection<F> values) throws IOException {
	super.in(field, values);
	return this;
    }

    @Override
    public SubQueryStream<S, T> isNull(EntityField<S, ?> field) throws IOException {
	super.isNull(field);
	return this;
    }

    @Override
    public SubQueryStream<S, T> notNull(EntityField<S, ?> field) throws IOException {
	super.notNull(field);
	return this;
    }
    // ================= entity QL methods ===================================//

    // ================= entity QL Filters ===================================//
    @Override
    public SubQueryStream<S, T> where() {
	super.where();
	return this;
    }

    @Override
    public SubQueryStream<S, T> and() {
	super.and();
	return this;
    }

    @Override
    public SubQueryStream<S, T> or() {
	super.or();
	return this;
    }

    @Override
    public SubQueryStream<S, T> openBracket() {
	super.openBracket();
	return this;
    }

    @Override
    public SubQueryStream<S, T> closeBracket() {
	super.closeBracket();
	return this;
    }

    @Override
    public SubQueryStream<S, T> brackets(QueryConsumer<S> consumer) throws IOException {
	super.brackets(consumer);
	return this;
    }

    // ================= entity QL Filters ===================================//

    @Override
    public SubQueryStream<S, T> appendPrefix(Object clause) {
	super.appendPrefix(clause);
	return this;
    }

    @Override
    public SubQueryStream<S, T> appendBody(Object clause) {
	super.appendBody(clause);
	return this;
    }
}
