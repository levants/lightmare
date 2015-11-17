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
package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Clauses;
import org.lightmare.criteria.query.internal.jpa.links.Operators;

/**
 * General query components
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface QueryExpression<T> extends JPAQueryWrapper<T>, SelectExpression<T>, GroupExpression<T>,
        JoinExpressions<T>, ResultStream<T>, SubQueryProcessor<T>, AggregateFunction<T> {

    /**
     * Gets wrapped entity {@link Class} instance
     * 
     * @return {@link Class} of entity type T
     */
    Class<T> getEntityType();

    // =========================order=by=====================================//

    /**
     * Generates ORDER BY part for field
     * 
     * @param field
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> orderBy(EntityField<T, F> field);

    /**
     * Generates ORDER BY with DESC for field
     * 
     * @param field
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> orderByDesc(EntityField<T, F> field);

    // ======================================================================//

    /**
     * Set clause for bulk UPDATE query
     * 
     * @param field
     * @param value
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> set(EntityField<T, F> field, F value);

    // ======================================================================//

    /**
     * WHERE clause appender
     * 
     * @return {@link QueryStream} current instance
     */
    QueryStream<T> where();

    /**
     * AND part appender
     * 
     * @return {@link QueryStream} current instance
     */
    default QueryStream<T> and() {
        return appendBody(Clauses.AND);
    }

    /**
     * OR part appender
     * 
     * @return {@link QueryStream} current instance
     */
    default QueryStream<T> or() {
        return appendBody(Clauses.OR);
    }

    /**
     * Opens bracket in query body
     * 
     * @return {@link QueryStream} current instance
     */
    default QueryStream<T> openBracket() {
        return appendBody(Operators.OPEN_BRACKET);
    }

    /**
     * Closes bracket in query body
     * 
     * @return {@link QueryStream} current instance
     */
    default QueryStream<T> closeBracket() {
        return appendBody(Operators.CLOSE_BRACKET);
    }

    /**
     * Creates query part in brackets
     * 
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    QueryStream<T> brackets(QueryConsumer<T> consumer);

    // ======================================================================//

    /**
     * Appends to generated query prefix custom clause
     * 
     * @param clause
     * @return {@link QueryStream} current instance
     */
    QueryStream<T> appendPrefix(Object clause);

    /**
     * Appends to generated query body custom clause
     * 
     * @param clause
     * @return {@link QueryStream} current instance
     */
    QueryStream<T> appendBody(Object clause);

    /**
     * Gets generated JPA query
     * 
     * @return {@link String} JPA query
     */
    String sql();

    /**
     * Gets generated JPA query for element count
     * 
     * @return {@link String} JPA query
     */
    String countSql();
}
