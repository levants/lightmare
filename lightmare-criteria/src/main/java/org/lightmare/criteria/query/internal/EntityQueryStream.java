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
package org.lightmare.criteria.query.internal;

import java.util.Collection;
import java.util.Objects;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.links.Joins;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.links.Orders;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.AbstractSelectStatements;
import org.lightmare.criteria.query.internal.jpa.subqueries.SubQueryStream;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Query builder from setter method references
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public abstract class EntityQueryStream<T> extends AbstractSelectStatements<T> {

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
    public <F> QueryStream<T> operateCollection(EntityField<T, F> field, Collection<F> values, String operator) {
        appendOperator();
        oppCollection(field, values, operator);

        return this;
    }

    @Override
    public <F> QueryStream<T> between(EntityField<T, F> field, F value1, F value2) {
        appendOperator();
        oppLine(field, value1, value2, Operators.BETWEEN);

        return this;
    }

    @Override
    public <F> QueryStream<T> notBetween(EntityField<T, F> field, F value1, F value2) {
        appendOperator();
        oppLine(field, value1, value2, Operators.NOT_BETWEEN);

        return this;
    }

    // ========================= Entity self method composers ===============//

    @Override
    public <F> QueryStream<T> operateCl(EntityField<T, F> field1, EntityField<T, F> field2, String operator) {
        appendOperator();
        oppField(field1, field2, operator);

        return this;
    }

    @Override
    public <F> QueryStream<T> operateCollectionCl(EntityField<T, F> field1, EntityField<T, Collection<F>> field2,
            String operator) {
        appendOperator();
        oppCollectionField(field1, field2, operator);
        return this;
    }

    @Override
    public <F> QueryStream<T> betweenCl(EntityField<T, F> field1, EntityField<T, F> field2, EntityField<T, F> field3) {
        appendOperator();
        oppLine(field1, field2, field3, Operators.BETWEEN);

        return this;
    }

    @Override
    public <F> QueryStream<T> notBetweenCl(EntityField<T, F> field1, EntityField<T, F> field2,
            EntityField<T, F> field3) {

        appendOperator();
        oppLine(field1, field2, field3, Operators.NOT_BETWEEN);

        return this;
    }

    // =========================embedded=field=queries=======================//

    @Override
    public <F> QueryStream<T> embedded(EntityField<T, F> field, SubQueryConsumer<F, T> consumer) {

        QueryTuple tuple = compose(field);
        Class<F> type = tuple.getFieldType();
        String embeddedName = tuple.getFieldName();
        EntityEmbeddedStream<F, T> embeddedQuery = new EntityEmbeddedStream<>(this, type, embeddedName);
        acceptAndCall(consumer, embeddedQuery);

        return this;
    }

    // =========================Sub queries ===============//

    /**
     * Generates {@link SubQueryStream} for entity type
     * 
     * @param subType
     * @return {@link SubQueryStream} for entity {@link Class}
     */
    public <S> SubQueryStream<S, T> subQuery(Class<S> subType) {
        return new EntitySubQueryStream<S, T>(this, subType);
    }

    /**
     * Generates {@link SubQueryStream} for JOIN query
     * 
     * @param subType
     * @return {@link SubQueryStream} for JOIN query
     */
    public <S> SubQueryStream<S, T> joinStream(Class<S> subType) {
        return new EntityJoinProcessor<S, T>(this, subType);
    }

    /**
     * Generates {@link SubQueryStream} for JOIN query
     * 
     * @param tuple
     * @return {@link SubQueryStream} for JOIN query
     */
    public <S> SubQueryStream<S, T> joinStream(QueryTuple tuple) {

        SubQueryStream<S, T> joinStream;

        Class<S> subType = tuple.getFieldType();
        joinStream = joinStream(subType);

        return joinStream;
    }

    /**
     * Validates and calls sub query stream methods
     * 
     * @param consumer
     * @param subQuery
     */
    private <S> void acceptConsumer(SubQueryConsumer<S, T> consumer, SubQueryStream<S, T> subQuery) {

        if (Objects.nonNull(consumer)) {
            consumer.accept(subQuery);
        }
    }

    /**
     * Validates and calls sub query stream methods
     * 
     * @param consumer
     * @param subQuery
     */
    private <S> void acceptAndCall(SubQueryConsumer<S, T> consumer, SubQueryStream<S, T> subQuery) {

        acceptConsumer(consumer, subQuery);
        subQuery.call();
    }

    /**
     * Creates {@link SubQueryStream} for instant {@link Class} entity type
     * 
     * @param subType
     * @param consumer
     * @return {@link SubQueryStream} for entity type
     */
    private <S> SubQueryStream<S, T> initSubQuery(Class<S> subType, SubQueryConsumer<S, T> consumer) {

        SubQueryStream<S, T> subQuery = subQuery(subType);

        acceptAndCall(consumer, subQuery);
        closeBracket();
        newLine();

        return subQuery;
    }

    @Override
    public <S> QueryStream<T> subQuery(Class<S> subType, SubQueryConsumer<S, T> consumer) {
        openBracket();
        initSubQuery(subType, consumer);

        return this;
    }

    @Override
    public <F, S> QueryStream<T> in(EntityField<T, F> field, Class<S> subType, SubQueryConsumer<S, T> consumer) {

        appendOperator();
        appSubQuery(field, Operators.IN);
        initSubQuery(subType, consumer);

        return this;
    }

    @Override
    public <F, S> QueryStream<T> notIn(EntityField<T, F> field, Class<S> subType, SubQueryConsumer<S, T> consumer) {

        appendOperator();
        appSubQuery(field, Operators.NOT_IN);
        initSubQuery(subType, consumer);

        return this;
    }

    @Override
    public <F, S> QueryStream<T> exists(Class<S> subType, SubQueryConsumer<S, T> consumer) {

        appendOperator();
        appendBody(Operators.EXISTS);
        openBracket();
        initSubQuery(subType, consumer);

        return this;
    }

    @Override
    public <F, S> QueryStream<T> notExists(Class<S> subType, SubQueryConsumer<S, T> consumer) {

        appendOperator();
        appendBody(Operators.NOT_EXISTS);
        openBracket();
        initSubQuery(subType, consumer);

        return this;
    }

    // ===============================Joins==================================//

    @Override
    public <E, C extends Collection<E>> void procesJoin(EntityField<T, C> field, String expression,
            SubQueryConsumer<E, T> consumer) {

        QueryTuple tuple = oppJoin(field, expression);
        SubQueryStream<E, T> joinQuery = joinStream(tuple);
        appendJoin(joinQuery.getAlias());
        appendJoin(StringUtils.NEWLINE);
        acceptAndCall(consumer, joinQuery);
    }

    @Override
    public <E, C extends Collection<E>> QueryStream<T> join(EntityField<T, C> field, SubQueryConsumer<E, T> consumer) {
        procesJoin(field, Joins.JOIN, consumer);
        return this;
    }

    @Override
    public <E, C extends Collection<E>> QueryStream<T> leftJoin(EntityField<T, C> field,
            SubQueryConsumer<E, T> consumer) {
        procesJoin(field, Joins.LEFT, consumer);
        return this;
    }

    @Override
    public <E, C extends Collection<E>> QueryStream<T> fetchJoin(EntityField<T, C> field,
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
    public <F> QueryStream<T> orderBy(EntityField<T, F> field) {
        setOrder(new EntityField[] { field });
        return this;
    }

    @Override
    public <F> QueryStream<T> orderByDesc(EntityField<T, F> field) {
        setOrder(Orders.DESC, new EntityField[] { field });
        return this;
    }
}
