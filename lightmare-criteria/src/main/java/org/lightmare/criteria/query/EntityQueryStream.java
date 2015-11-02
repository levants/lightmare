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
import java.util.Collection;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.links.Filters;
import org.lightmare.criteria.links.Joins;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.links.Orders;
import org.lightmare.criteria.query.jpa.AbstractSelectStatements;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.utils.ObjectUtils;

/**
 * Query builder from setter method references
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public abstract class EntityQueryStream<T extends Serializable> extends AbstractSelectStatements<T> {

    protected EntityQueryStream(EntityManager em, Class<T> entityType, final String alias) {
	super(em, entityType, alias);
    }

    @Override
    public <F> QueryStream<T> equals(EntityField<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.EQ);
	return this;
    }

    @Override
    public <F> QueryStream<T> notEquals(EntityField<T, F> field, F value) throws IOException {
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
    public <F> QueryStream<T> moreOrEquals(EntityField<T, F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE_OR_EQ);
	return this;
    }

    @Override
    public <F> QueryStream<T> lessOrEquals(EntityField<T, F> field, F value) throws IOException {
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
    public <F> QueryStream<T> in(EntityField<T, F> field, Collection<F> values) throws IOException {
	oppCollection(field, values);
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

    // =========================Sub queries ===============//

    public <S extends Serializable> SubQueryStream<S, T> subQuery(Class<S> subType) {
	return new EntitySubQueryStream<S, T>(this, subType);
    }

    public <S extends Serializable> SubQueryStream<S, T> joinStream(Class<S> subType) {
	return new EntityJoinProcessor<S, T>(this, subType);
    }

    public <S extends Serializable> SubQueryStream<S, T> joinStream(QueryTuple tuple) {

	SubQueryStream<S, T> joinStream;

	Class<S> subType = ObjectUtils.cast(tuple.getGenericType());
	joinStream = joinStream(subType);

	return joinStream;
    }

    private <S extends Serializable> SubQueryStream<S, T> initSubQuery(Class<S> subType,
	    SubQueryConsumer<S, T> consumer) throws IOException {

	SubQueryStream<S, T> subQuery = subQuery(subType);

	consumer.accept(subQuery);
	subQuery.call();
	closeBracket();
	newLine();

	return subQuery;
    }

    @Override
    public <S extends Serializable> QueryStream<T> subQuery(Class<S> subType, SubQueryConsumer<S, T> consumer)
	    throws IOException {
	openBracket();
	initSubQuery(subType, consumer);

	return this;
    }

    @Override
    public <F, S extends Serializable> QueryStream<T> in(EntityField<T, F> field, Class<S> subType,
	    SubQueryConsumer<S, T> consumer) throws IOException {
	appSubQuery(field, Operators.IN);
	initSubQuery(subType, consumer);

	return this;
    }

    @Override
    public <F, S extends Serializable> QueryStream<T> exists(Class<S> subType, SubQueryConsumer<S, T> consumer)
	    throws IOException {

	appendBody(Operators.EXISTS);
	openBracket();
	initSubQuery(subType, consumer);

	return this;
    }

    // ===============================Joins==================================//
    private <E extends Serializable, C extends Collection<E>> void procesJoin(EntityField<T, C> field,
	    String expression, SubQueryConsumer<E, T> consumer) throws IOException {

	QueryTuple tuple = oppJoin(field, expression);
	SubQueryStream<E, T> joinQuery = joinStream(tuple);
	appendJoin(joinQuery.getAlias());
	consumer.accept(joinQuery);
	joinQuery.call();
    }

    @Override
    public <E extends Serializable, C extends Collection<E>> QueryStream<T> join(EntityField<T, C> field,
	    SubQueryConsumer<E, T> consumer) throws IOException {
	procesJoin(field, Joins.JOIN, consumer);
	return this;
    }

    @Override
    public <E extends Serializable, C extends Collection<E>> QueryStream<T> leftJoin(EntityField<T, C> field,
	    SubQueryConsumer<E, T> consumer) throws IOException {
	procesJoin(field, Joins.LEFT, consumer);
	return this;
    }

    @Override
    public <E extends Serializable, C extends Collection<E>> QueryStream<T> fetchJoin(EntityField<T, C> field,
	    SubQueryConsumer<E, T> consumer) throws IOException {
	procesJoin(field, Joins.LEFT, consumer);
	return this;
    }

    // =======================================================================//
    @Override
    public <F> QueryStream<T> set(EntityField<T, F> field, F value) throws IOException {
	setOpp(field, value);
	return this;
    }

    @Override
    public QueryStream<T> orderBy(EntityField<T, ?> field) throws IOException {
	setOrder(new EntityField[] { field });
	return this;
    }

    @Override
    public QueryStream<T> orderByDesc(EntityField<T, ?> field) throws IOException {
	setOrder(Orders.DESC, new EntityField[] { field });
	return this;
    }
}
