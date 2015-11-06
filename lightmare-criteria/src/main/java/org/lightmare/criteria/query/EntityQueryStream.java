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

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.links.Filters;
import org.lightmare.criteria.links.Joins;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.links.Orders;
import org.lightmare.criteria.query.jpa.AbstractSelectStatements;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

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
    public <F> QueryStream<T> operate(EntityField<T, F> field, String operator) {
	oppLine(field, operator);
	return this;
    }

    @Override
    public <F> QueryStream<T> operate(EntityField<T, F> field, F value, String operator) {
	appendOperator();
	oppLine(field, value, operator);

	return this;
    }

    @Override
    public <F> QueryStream<T> equals(EntityField<T, F> field, F value) {
	appendOperator();
	operate(field, value, Operators.EQ);

	return this;
    }

    @Override
    public <F> QueryStream<T> notEquals(EntityField<T, F> field, F value) {
	return operate(field, value, Operators.NOT_EQ);
    }

    @Override
    public <F> QueryStream<T> more(EntityField<T, F> field, F value) {
	return operate(field, value, Operators.MORE);
    }

    @Override
    public <F> QueryStream<T> less(EntityField<T, F> field, F value) {
	return operate(field, value, Operators.LESS);
    }

    @Override
    public <F> QueryStream<T> moreOrEquals(EntityField<T, F> field, F value) {
	return operate(field, value, Operators.MORE_OR_EQ);
    }

    @Override
    public <F> QueryStream<T> lessOrEquals(EntityField<T, F> field, F value) {
	return operate(field, value, Operators.LESS_OR_EQ);
    }

    @Override
    public QueryStream<T> startsWith(EntityField<T, String> field, String value) {
	String enrich = StringUtils.concat(value, Filters.LIKE_SIGN);
	return operate(field, enrich, Operators.LIKE);
    }

    @Override
    public QueryStream<T> like(EntityField<T, String> field, String value) {
	return startsWith(field, value);
    }

    @Override
    public QueryStream<T> endsWith(EntityField<T, String> field, String value) {
	String enrich = Filters.LIKE_SIGN.concat(value);
	return operate(field, enrich, Operators.LIKE);
    }

    @Override
    public QueryStream<T> contains(EntityField<T, String> field, String value) {
	String enrich = StringUtils.concat(Filters.LIKE_SIGN, value, Filters.LIKE_SIGN);
	return operate(field, enrich, Operators.LIKE);
    }

    @Override
    public <F> QueryStream<T> operateCollection(EntityField<T, F> field, Collection<F> values, String operator) {
	appendOperator();
	oppCollection(field, values, operator);

	return this;
    }

    @Override
    public <F> QueryStream<T> in(EntityField<T, F> field, Collection<F> values) {
	return operateCollection(field, values, Operators.IN);
    }

    @Override
    public <F> QueryStream<T> notIn(EntityField<T, F> field, Collection<F> values) {
	return operateCollection(field, values, Operators.NOT_IN);
    }

    @Override
    public QueryStream<T> isNull(EntityField<T, ?> field) {
	return operate(field, Operators.IS_NULL);
    }

    @Override
    public QueryStream<T> notNull(EntityField<T, ?> field) {
	return operate(field, Operators.NOT_NULL);
    }

    // ========================= Entity self method composers ===============//

    @Override
    public <F> QueryStream<T> operate(EntityField<T, F> field1, EntityField<T, F> field2, String operator) {
	appendOperator();
	oppField(field1, field2, operator);

	return this;
    }

    @Override
    public <F> QueryStream<T> equals(EntityField<T, F> field1, EntityField<T, F> field2) {
	return operate(field1, field2, Operators.EQ);
    }

    @Override
    public <F> QueryStream<T> notEquals(EntityField<T, F> field1, EntityField<T, F> field2) {
	return operate(field1, field2, Operators.NOT_EQ);
    }

    @Override
    public <F> QueryStream<T> more(EntityField<T, F> field1, EntityField<T, F> field2) {
	return operate(field1, field2, Operators.MORE);
    }

    @Override
    public <F> QueryStream<T> less(EntityField<T, F> field1, EntityField<T, F> field2) {
	return operate(field1, field2, Operators.LESS);
    }

    @Override
    public <F> QueryStream<T> moreOrEquals(EntityField<T, F> field1, EntityField<T, F> field2) {
	return operate(field1, field2, Operators.MORE_OR_EQ);
    }

    @Override
    public <F> QueryStream<T> lessOrEquals(EntityField<T, F> field1, EntityField<T, F> field2) {
	return operate(field1, field2, Operators.LESS_OR_EQ);
    }

    @Override
    public QueryStream<T> startsWith(EntityField<T, String> field1, EntityField<T, String> field2) {
	return operate(field1, field2, Operators.LIKE);
    }

    @Override
    public QueryStream<T> like(EntityField<T, String> field1, EntityField<T, String> field2) {
	return operate(field1, field2, Operators.LIKE);
    }

    @Override
    public QueryStream<T> endsWith(EntityField<T, String> field1, EntityField<T, String> field2) {
	return operate(field1, field2, Operators.LIKE);
    }

    @Override
    public QueryStream<T> contains(EntityField<T, String> field1, EntityField<T, String> field2) {
	return operate(field1, field2, Operators.LIKE);
    }

    @Override
    public <F> QueryStream<T> operateCollection(EntityField<T, F> field1, EntityField<T, Collection<F>> field2,
	    String operator) {
	oppCollectionField(field1, field2, operator);
	return this;
    }

    @Override
    public <F> QueryStream<T> in(EntityField<T, F> field1, EntityField<T, Collection<F>> field2) {
	return operateCollection(field1, field2, Operators.IN);
    }

    @Override
    public <F> QueryStream<T> notIn(EntityField<T, F> field1, EntityField<T, Collection<F>> field2) {
	return operateCollection(field1, field2, Operators.NOT_IN);
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

    /**
     * Validates and calls sub query tream methods
     * 
     * @param consumer
     * @param subQuery
     */
    private <S extends Serializable> void acceptConsumer(SubQueryConsumer<S, T> consumer,
	    SubQueryStream<S, T> subQuery) {

	if (Objects.nonNull(consumer)) {
	    consumer.accept(subQuery);
	}
    }

    private <S extends Serializable> SubQueryStream<S, T> initSubQuery(Class<S> subType,
	    SubQueryConsumer<S, T> consumer) {

	SubQueryStream<S, T> subQuery = subQuery(subType);

	acceptConsumer(consumer, subQuery);
	subQuery.call();
	closeBracket();
	newLine();

	return subQuery;
    }

    @Override
    public <S extends Serializable> QueryStream<T> subQuery(Class<S> subType, SubQueryConsumer<S, T> consumer) {
	openBracket();
	initSubQuery(subType, consumer);

	return this;
    }

    @Override
    public <F, S extends Serializable> QueryStream<T> in(EntityField<T, F> field, Class<S> subType,
	    SubQueryConsumer<S, T> consumer) {
	appSubQuery(field, Operators.IN);
	initSubQuery(subType, consumer);

	return this;
    }

    @Override
    public <F, S extends Serializable> QueryStream<T> notIn(EntityField<T, F> field, Class<S> subType,
	    SubQueryConsumer<S, T> consumer) {
	appSubQuery(field, Operators.NOT_IN);
	initSubQuery(subType, consumer);

	return this;
    }

    @Override
    public <F, S extends Serializable> QueryStream<T> exists(Class<S> subType, SubQueryConsumer<S, T> consumer) {

	appendBody(Operators.EXISTS);
	openBracket();
	initSubQuery(subType, consumer);

	return this;
    }

    @Override
    public <F, S extends Serializable> QueryStream<T> notExists(Class<S> subType, SubQueryConsumer<S, T> consumer) {

	appendBody(Operators.NOT_EXISTS);
	openBracket();
	initSubQuery(subType, consumer);

	return this;
    }

    // ===============================Joins==================================//
    private <E extends Serializable, C extends Collection<E>> void procesJoin(EntityField<T, C> field,
	    String expression, SubQueryConsumer<E, T> consumer) {

	QueryTuple tuple = oppJoin(field, expression);
	SubQueryStream<E, T> joinQuery = joinStream(tuple);
	appendJoin(joinQuery.getAlias());
	appendJoin(NEW_LINE);
	acceptConsumer(consumer, joinQuery);
	joinQuery.call();
    }

    @Override
    public <E extends Serializable, C extends Collection<E>> QueryStream<T> join(EntityField<T, C> field,
	    SubQueryConsumer<E, T> consumer) {
	procesJoin(field, Joins.JOIN, consumer);
	return this;
    }

    @Override
    public <E extends Serializable, C extends Collection<E>> QueryStream<T> leftJoin(EntityField<T, C> field,
	    SubQueryConsumer<E, T> consumer) {
	procesJoin(field, Joins.LEFT, consumer);
	return this;
    }

    @Override
    public <E extends Serializable, C extends Collection<E>> QueryStream<T> fetchJoin(EntityField<T, C> field,
	    SubQueryConsumer<E, T> consumer) {
	procesJoin(field, Joins.FETCH, consumer);
	return this;
    }

    // =======================================================================//
    @Override
    public <F> QueryStream<T> set(EntityField<T, F> field, F value) {
	setOpp(field, value);
	return this;
    }

    @Override
    public QueryStream<T> orderBy(EntityField<T, ?> field) {
	setOrder(new EntityField[] { field });
	return this;
    }

    @Override
    public QueryStream<T> orderByDesc(EntityField<T, ?> field) {
	setOrder(Orders.DESC, new EntityField[] { field });
	return this;
    }
}
