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
import org.lightmare.criteria.query.internal.orm.links.Joins;
import org.lightmare.criteria.query.internal.orm.subqueries.EntityJoinProcessor;
import org.lightmare.criteria.query.internal.orm.subqueries.SubQueryStream;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.query.providers.JpaQueryStream;
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
 */
abstract class AbstractJoinStream<T> extends AbstractFunctionExpression<T> {

    protected AbstractJoinStream(final LayerProvider provider, final Class<T> entityType) {
        super(provider, entityType);
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
        appendJoin(tuple.getAlias());
        appendJoin(StringUtils.DOT);
        appendJoin(tuple.getFieldName());
        appendJoin(StringUtils.SPACE);

        return tuple;
    }

    /**
     * Generates {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     * for JOIN query
     * 
     * @param type
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         JOIN query
     */
    private <S> SubQueryStream<S, T> joinStream(Class<S> type) {

        SubQueryStream<S, T> joinQuery = new EntityJoinProcessor<S, T>(this, type);

        appendJoin(joinQuery.getAlias());
        appendJoin(StringUtils.NEWLINE);

        return joinQuery;
    }

    /**
     * Generates {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     * for JOIN query
     * 
     * @param tuple
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         JOIN query
     */
    private <S> JpaQueryStream<S> joinStream(QueryTuple tuple) {

        JpaQueryStream<S> joinStream;

        Class<S> type = tuple.getCollectionType();
        joinStream = joinStream(type);

        return joinStream;
    }

    /**
     * Generates {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     * for JOIN query
     * 
     * @param field
     * @param expression
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for
     *         JOIN query
     */
    protected <E, C extends Collection<E>> JpaQueryStream<E> joinStream(EntityField<T, C> field, String expression,
            QueryConsumer<E, JpaQueryStream<E>> consumer) {

        JpaQueryStream<E> joinQuery;

        QueryTuple tuple = oppJoin(field, expression);
        joinQuery = joinStream(tuple);

        return joinQuery;
    }

    /**
     * Generates ON expression for JOIN
     * 
     * @param on
     * @param joinQuery
     */
    protected <E> void joinOn(QueryConsumer<E, JpaQueryStream<E>> on, JpaQueryStream<E> joinQuery) {
        appendJoin(Joins.ON);
        ObjectUtils.nonNull(joinQuery, c -> c.brackets(on));
    }
}
