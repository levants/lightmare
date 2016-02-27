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

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.internal.layers.LayerProvider;
import org.lightmare.criteria.query.internal.orm.builders.AbstractAggregateStream;
import org.lightmare.criteria.query.internal.orm.links.Joins;
import org.lightmare.criteria.query.internal.orm.links.Operators;
import org.lightmare.criteria.query.internal.orm.subqueries.SubQueryStream;
import org.lightmare.criteria.query.providers.JpaQueryStream;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Query builder from setter method references
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated JPA query
 */
public abstract class EntityQueryStream<T> extends AbstractAggregateStream<T> {

    protected EntityQueryStream(final LayerProvider provider, Class<T> entityType) {
        super(provider, entityType);
    }

    @Override
    public <F> JpaQueryStream<T> operate(EntityField<T, F> field, String operator) {

        appendOperator();
        oppLine(field, operator);

        return this;
    }

    @Override
    public <F> JpaQueryStream<T> operate(EntityField<T, ? extends F> field, Object value, String operator) {

        appendOperator();
        oppLine(field, value, operator);

        return this;
    }

    @Override
    public <F> JpaQueryStream<T> operate(EntityField<T, ? extends F> field, Object value1, Object value2,
            String operator) {

        appendOperator();
        oppLine(field, value1, value2, Operators.BETWEEN);

        return this;
    }

    @Override
    public <F> JpaQueryStream<T> operate(EntityField<T, ? extends F> field, String operator1, Object value1,
            String operator2, Object value2) {

        appendOperator();
        oppLine(field, operator1, value1, operator2, value2);

        return this;
    }

    @Override
    public <F> JpaQueryStream<T> operateCollection(EntityField<T, F> field, Collection<F> values, String operator) {

        appendOperator();
        oppCollection(field, values, operator);

        return this;
    }

    @Override
    public <S, F> JpaQueryStream<T> operateCollection(Object value, EntityField<S, Collection<F>> field,
            String operator) {

        appendOperator();
        oppCollection(value, field, operator);

        return this;
    }

    // ========================= Entity self method composers ===============//

    @Override
    public <F, S> JpaQueryStream<T> operate(EntityField<T, ? extends F> field1, EntityField<S, ? extends F> field2,
            String operator) {

        appendOperator();
        oppField(field1, field2, operator);

        return this;
    }

    @Override
    public <F, E, S, L> JpaQueryStream<T> operate(EntityField<T, ? extends F> field1, String operator1,
            EntityField<S, ? extends F> field2, String operator2, EntityField<L, E> field3) {

        appendOperator();
        oppField(field1, operator1, field2, operator2, field3);

        return this;
    }

    @Override
    public <F, S> JpaQueryStream<T> operate(EntityField<T, ? extends F> field1, EntityField<S, ? extends F> field2,
            EntityField<S, ? extends F> field3, String operator) {

        appendOperator();
        oppLine(field1, field2, field3, operator);

        return this;
    }

    @Override
    public <F, S> JpaQueryStream<T> operateCollection(EntityField<T, F> field1, EntityField<S, Collection<F>> field2,
            String operator) {

        appendOperator();
        oppCollectionField(field1, field2, operator);

        return this;
    }

    // =========================embedded=field=queries=======================//

    @Override
    public <F> JpaQueryStream<T> embedded(EntityField<T, F> field, QueryConsumer<F, JpaQueryStream<F>> consumer) {

        QueryTuple tuple = compose(field);
        Class<F> type = tuple.getFieldGenericType();
        String embeddedName = tuple.getFieldName();
        JpaQueryStream<F> embeddedQuery = new EntityEmbeddedStream<>(this, type, embeddedName);
        acceptAndCall(consumer, embeddedQuery);

        return this;
    }

    /**
     * Generates
     * {@link org.lightmare.criteria.query.internal.orm.subqueries.SubQueryStream}
     * for entity type
     * 
     * @param type
     * @return {@link org.lightmare.criteria.query.internal.orm.subqueries.SubQueryStream}
     *         for entity {@link Class}
     */
    public <S> SubQueryStream<S, T> subQuery(Class<S> type) {
        return new EntitySubQueryStream<S, T>(this, type);
    }

    /**
     * Generates {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     * for JOIN query
     * 
     * @param type
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         JOIN query
     */
    public <S> SubQueryStream<S, T> joinStream(Class<S> type) {
        return new EntityJoinProcessor<S, T>(this, type);
    }

    /**
     * Generates {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     * for JOIN query
     * 
     * @param tuple
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         JOIN query
     */
    public <S> JpaQueryStream<S> joinStream(QueryTuple tuple) {

        JpaQueryStream<S> joinStream;

        Class<S> type = tuple.getCollectionType();
        joinStream = joinStream(type);

        return joinStream;
    }

    // =========================operate=sub=queries==========================//

    /**
     * Validates and calls sub query stream methods
     * 
     * @param consumer
     * @param subQuery
     */
    private <S> void acceptAndCall(QueryConsumer<S, JpaQueryStream<S>> consumer, JpaQueryStream<S> query) {
        ObjectUtils.accept(consumer, query);
        ObjectUtils.castIfValid(query, SubQueryStream.class, SubQueryStream::call);
    }

    /**
     * Creates
     * {@link org.lightmare.criteria.query.internal.orm.subqueries.SubQueryStream}
     * for instant {@link Class} entity type
     * 
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.internal.orm.subqueries.SubQueryStream}
     *         for entity type
     */
    private <S> JpaQueryStream<S> initSubQuery(Class<S> type, QueryConsumer<S, JpaQueryStream<S>> consumer) {

        JpaQueryStream<S> query = subQuery(type);

        acceptAndCall(consumer, query);
        closeBracket();
        newLine();

        return query;
    }

    @Override
    public <S> JpaQueryStream<T> operateSubQuery(Class<S> type, QueryConsumer<S, JpaQueryStream<S>> consumer) {

        openBracket();
        initSubQuery(type, consumer);

        return this;
    }

    @Override
    public <F, S> JpaQueryStream<T> operateSubQuery(EntityField<T, F> field, String operator, Class<S> type,
            QueryConsumer<S, JpaQueryStream<S>> consumer) {

        appendOperator();
        appSubQuery(field, operator);
        initSubQuery(type, consumer);

        return this;
    }

    @Override
    public <F, S> JpaQueryStream<T> operateSubQuery(Object value, String operator, Class<S> type,
            QueryConsumer<S, JpaQueryStream<S>> consumer) {

        appendOperator();
        appendBody(value).appendBody(StringUtils.SPACE).appendBody(operator);
        openBracket();
        initSubQuery(type, consumer);

        return this;
    }

    @Override
    public <F, S> JpaQueryStream<T> operateFunctionWithSubQuery(FunctionConsumer<T> function, String operator,
            Class<S> type, QueryConsumer<S, JpaQueryStream<S>> consumer) {

        startFunctionExpression(function, operator);
        openBracket();
        initSubQuery(type, consumer);

        return this;
    }

    @Override
    public <F, S> JpaQueryStream<T> operateSubQuery(String operator, Class<S> type,
            QueryConsumer<S, JpaQueryStream<S>> consumer) {

        appendOperator();
        appendBody(operator);
        openBracket();
        initSubQuery(type, consumer);

        return this;
    }

    // ===============================Joins==================================//

    @Override
    public <E, C extends Collection<E>> void procesJoin(EntityField<T, C> field, String expression,
            QueryConsumer<E, JpaQueryStream<E>> consumer) {

        QueryTuple tuple = oppJoin(field, expression);
        JpaQueryStream<E> joinQuery = joinStream(tuple);
        appendJoin(joinQuery.getAlias());
        appendJoin(StringUtils.NEWLINE);
        acceptAndCall(consumer, joinQuery);
    }

    @Override
    public <E, C extends Collection<E>> JpaQueryStream<T> join(EntityField<T, C> field,
            QueryConsumer<E, JpaQueryStream<E>> consumer) {
        procesJoin(field, Joins.JOIN, consumer);
        return this;
    }

    @Override
    public <E, C extends Collection<E>> JpaQueryStream<T> leftJoin(EntityField<T, C> field,
            QueryConsumer<E, JpaQueryStream<E>> consumer) {
        procesJoin(field, Joins.LEFT, consumer);
        return this;
    }

    @Override
    public <E, C extends Collection<E>> JpaQueryStream<T> fetchJoin(EntityField<T, C> field,
            QueryConsumer<E, JpaQueryStream<E>> consumer) {
        procesJoin(field, Joins.FETCH, consumer);
        return this;
    }

    // =======================================================================//

    @Override
    public <F> JpaQueryStream<T> set(EntityField<T, F> field, F value) {
        setOpp(field, value);
        return this;
    }

    @Override
    public <F> JpaQueryStream<T> order(String dir, EntityField<T, F> field) {
        setOrder(dir, new EntityField[] { field });
        return this;
    }
}
