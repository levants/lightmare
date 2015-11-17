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
import java.util.function.Consumer;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.GroupByConsumer;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.builders.AbstractGroupByStream;
import org.lightmare.criteria.query.internal.jpa.links.Aggregates;
import org.lightmare.criteria.query.internal.jpa.links.Joins;
import org.lightmare.criteria.query.internal.jpa.links.Operators;
import org.lightmare.criteria.query.internal.jpa.links.Orders;
import org.lightmare.criteria.query.internal.jpa.subqueries.SubQueryStream;
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
public abstract class EntityQueryStream<T> extends AbstractGroupByStream<T> {

    protected EntityQueryStream(EntityManager em, Class<T> entityType, final String alias) {
        super(em, entityType, alias);
    }

    @Override
    public <F> QueryStream<T> operate(EntityField<T, F> field, String operator) {
        oppLine(field, operator);
        return this;
    }

    @Override
    public <F> QueryStream<T> operate(EntityField<T, ? extends F> field, Object value, String operator) {
        appendOperator();
        oppLine(field, value, operator);

        return this;
    }

    @Override
    public <F> QueryStream<T> operate(EntityField<T, ? extends F> field, Object value1, Object value2,
            String operator) {
        appendOperator();
        oppLine(field, value1, value2, Operators.BETWEEN);

        return this;
    }

    @Override
    public <F> QueryStream<T> operateCollection(EntityField<T, F> field, Collection<F> values, String operator) {
        appendOperator();
        oppCollection(field, values, operator);

        return this;
    }

    // ========================= Entity self method composers ===============//

    @Override
    public <F, S> QueryStream<T> operate(EntityField<T, ? extends F> field1, EntityField<S, ? extends F> field2,
            String operator) {
        appendOperator();
        oppField(field1, field2, operator);

        return this;
    }

    @Override
    public <F, S> QueryStream<T> operate(EntityField<T, ? extends F> field1, EntityField<S, ? extends F> field2,
            EntityField<S, ? extends F> field3, String operator) {
        appendOperator();
        oppLine(field1, field2, field3, operator);

        return this;
    }

    @Override
    public <F, S> QueryStream<T> operateCollection(EntityField<T, F> field1, EntityField<S, Collection<F>> field2,
            String operator) {
        appendOperator();
        oppCollectionField(field1, field2, operator);

        return this;
    }

    // =========================embedded=field=queries=======================//

    @Override
    public <F> QueryStream<T> embedded(EntityField<T, F> field, QueryConsumer<F> consumer) {

        QueryTuple tuple = compose(field);
        Class<F> type = tuple.getFieldType();
        String embeddedName = tuple.getFieldName();
        QueryStream<F> embeddedQuery = new EntityEmbeddedStream<>(this, type, embeddedName);
        acceptAndCall(consumer, embeddedQuery);

        return this;
    }

    // =========================group by=====================================//

    @Override
    public <F> QueryStream<Object[]> count(EntityField<T, F> field, GroupByConsumer<T> consumer) {

        oppAggregate(field, Aggregates.COUNT);
        acceptConsumer(consumer, this);

        return this.selectStream;
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
     * Generates {@link QueryStream} for JOIN query
     * 
     * @param subType
     * @return {@link QueryStream} for JOIN query
     */
    public <S> SubQueryStream<S, T> joinStream(Class<S> subType) {
        return new EntityJoinProcessor<S, T>(this, subType);
    }

    /**
     * Generates {@link QueryStream} for JOIN query
     * 
     * @param tuple
     * @return {@link QueryStream} for JOIN query
     */
    public <S> QueryStream<S> joinStream(QueryTuple tuple) {

        QueryStream<S> joinStream;

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
    private <S> void acceptConsumer(Consumer<S> consumer, S value) {

        if (Objects.nonNull(consumer)) {
            consumer.accept(value);
        }
    }

    /**
     * Validates and calls sub query stream methods
     * 
     * @param consumer
     * @param subQuery
     */
    private <S> void acceptAndCall(QueryConsumer<S> consumer, QueryStream<S> query) {

        acceptConsumer(consumer, query);
        if (query instanceof SubQueryStream<?, ?>) {
            SubQueryStream<S, T> subQuery = ObjectUtils.cast(query);
            subQuery.call();
        }
    }

    /**
     * Creates {@link SubQueryStream} for instant {@link Class} entity type
     * 
     * @param subType
     * @param consumer
     * @return {@link SubQueryStream} for entity type
     */
    private <S> QueryStream<S> initSubQuery(Class<S> subType, QueryConsumer<S> consumer) {

        QueryStream<S> query = subQuery(subType);

        acceptAndCall(consumer, query);
        closeBracket();
        newLine();

        return query;
    }

    @Override
    public <S> QueryStream<T> subQuery(Class<S> subType, QueryConsumer<S> consumer) {
        openBracket();
        initSubQuery(subType, consumer);

        return this;
    }

    @Override
    public <F, S> QueryStream<T> in(EntityField<T, F> field, Class<S> subType, QueryConsumer<S> consumer) {

        appendOperator();
        appSubQuery(field, Operators.IN);
        initSubQuery(subType, consumer);

        return this;
    }

    @Override
    public <F, S> QueryStream<T> notIn(EntityField<T, F> field, Class<S> subType, QueryConsumer<S> consumer) {

        appendOperator();
        appSubQuery(field, Operators.NOT_IN);
        initSubQuery(subType, consumer);

        return this;
    }

    @Override
    public <F, S> QueryStream<T> exists(Class<S> subType, QueryConsumer<S> consumer) {

        appendOperator();
        appendBody(Operators.EXISTS);
        openBracket();
        initSubQuery(subType, consumer);

        return this;
    }

    @Override
    public <F, S> QueryStream<T> notExists(Class<S> subType, QueryConsumer<S> consumer) {

        appendOperator();
        appendBody(Operators.NOT_EXISTS);
        openBracket();
        initSubQuery(subType, consumer);

        return this;
    }

    // ===============================Joins==================================//

    @Override
    public <E, C extends Collection<E>> void procesJoin(EntityField<T, C> field, String expression,
            QueryConsumer<E> consumer) {

        QueryTuple tuple = oppJoin(field, expression);
        QueryStream<E> joinQuery = joinStream(tuple);
        appendJoin(joinQuery.getAlias());
        appendJoin(StringUtils.NEWLINE);
        acceptAndCall(consumer, joinQuery);
    }

    @Override
    public <E, C extends Collection<E>> QueryStream<T> join(EntityField<T, C> field, QueryConsumer<E> consumer) {
        procesJoin(field, Joins.JOIN, consumer);
        return this;
    }

    @Override
    public <E, C extends Collection<E>> QueryStream<T> leftJoin(EntityField<T, C> field, QueryConsumer<E> consumer) {
        procesJoin(field, Joins.LEFT, consumer);
        return this;
    }

    @Override
    public <E, C extends Collection<E>> QueryStream<T> fetchJoin(EntityField<T, C> field, QueryConsumer<E> consumer) {
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
