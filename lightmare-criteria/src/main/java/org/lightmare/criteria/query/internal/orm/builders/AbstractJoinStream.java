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
package org.lightmare.criteria.query.internal.orm.builders;

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.links.Joins;
import org.lightmare.criteria.query.internal.orm.links.Operators.Brackets;
import org.lightmare.criteria.query.internal.orm.subqueries.EntityJoinProcessor;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Implementation for JOIN clause query generation
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 * 
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 * @param <O>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
abstract class AbstractJoinStream<T, Q extends QueryStream<T, ? super Q>, O extends QueryStream<Object[], ? super O>>
        extends AbstractFunctionExpression<T, Q, O> {

    protected AbstractJoinStream(final LayerProvider provider, final Class<T> entityType) {
        super(provider, entityType);
    }

    /**
     * Replaces last new line element from query joins
     */
    private void replaceJoinNewLine(char character) {
        StringUtils.replaceOrAppend(joins, StringUtils.LINE, character);
    }

    /**
     * Adds JOIN column to query body
     * 
     * @param tuple
     */
    private void appendJoinField(QueryTuple tuple) {
        appendJoin(tuple.getAlias(), StringUtils.DOT, tuple.getFieldName(), StringUtils.SPACE);
    }

    /**
     * Processes join statement for collection field
     * 
     * @param field
     * @param expression
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for field and
     *         expression
     */
    protected <C extends Collection<?>> QueryTuple oppJoin(EntityField<T, C> field, String expression) {

        QueryTuple tuple;

        appendJoin(expression);
        tuple = compose(field);
        appendJoinField(tuple);

        return tuple;
    }

    /**
     * Generates {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     * for JOIN query
     * 
     * @param type
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     *         for JOIN query
     */
    private <E, S extends LambdaStream<E, ? super S>> S joinStream(Class<E> type) {

        S joinQuery = ObjectUtils.applyAndCast(type, c -> new EntityJoinProcessor<E, T>(this, c));
        appendJoin(joinQuery.getAlias(), StringUtils.NEWLINE);

        return joinQuery;
    }

    /**
     * Generates {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     * for JOIN query
     * 
     * @param tuple
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     *         for JOIN query
     */
    private <E, S extends LambdaStream<E, ? super S>> S joinStream(QueryTuple tuple) {

        S joinStream;

        Class<E> type = tuple.getCollectionType();
        joinStream = joinStream(type);

        return joinStream;
    }

    /**
     * Generates {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     * for JOIN query
     * 
     * @param field
     * @param expression
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     *         for JOIN query
     */
    protected <E, C extends Collection<E>, S extends LambdaStream<E, ? super S>> S joinStream(EntityField<T, C> field,
            String expression) {

        S joinQuery;

        QueryTuple tuple = oppJoin(field, expression);
        joinQuery = joinStream(tuple);

        return joinQuery;
    }

    /**
     * Begins ON expression
     */
    private void openOnExpression() {
        appendJoin(Joins.ON);
        appendJoin(Brackets.OPEN);
    }

    /**
     * Ends ON expression
     */
    private void closeOnExpression() {
        replaceJoinNewLine(Brackets.CLOSE);
        appendJoin(StringUtils.NEWLINE);
    }

    /**
     * Generates ON expression for JOIN
     * 
     * @param on
     * @param alias
     * @param type
     */
    private <E, S extends LambdaStream<E, ? super S>> void joinOn(QueryConsumer<E, S> on, String alias, Class<E> type) {

        openOnExpression();
        S onQuery = ObjectUtils.applyAndCast(type, c -> new EntityJoinProcessor<E, T>(this, alias, c));
        acceptAndCall(on, onQuery);
        closeOnExpression();
    }

    /**
     * Generates JOIN clause
     * 
     * @param field
     * @param expression
     * @param on
     * @param consumer
     */
    private <E, C extends Collection<E>, S extends LambdaStream<E, ? super S>> void joinBody(EntityField<T, C> field,
            String expression, QueryConsumer<E, S> on, QueryConsumer<E, S> consumer) {

        S joinQuery = joinStream(field, expression);
        ObjectUtils.nonNull(on, c -> joinOn(c, joinQuery.getAlias(), joinQuery.getEntityType()));
        acceptAndCall(consumer, joinQuery);
    }

    @Override
    public <E, C extends Collection<E>, S extends LambdaStream<E, ? super S>> Q procesJoin(EntityField<T, C> field,
            String expression, QueryConsumer<E, S> on, QueryConsumer<E, S> consumer) {

        Q stream = stream();
        joinBody(field, expression, on, consumer);

        return stream;
    }
}
