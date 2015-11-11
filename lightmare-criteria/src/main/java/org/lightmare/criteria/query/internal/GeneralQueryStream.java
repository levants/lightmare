package org.lightmare.criteria.query.internal;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.links.Clauses;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.JPAQueryWrapper;
import org.lightmare.criteria.query.internal.jpa.JoinQueryStream;
import org.lightmare.criteria.query.internal.jpa.ResultStream;
import org.lightmare.criteria.query.internal.jpa.SelectStatements;

/**
 * General query components
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface GeneralQueryStream<T>
        extends JPAQueryWrapper<T>, SelectStatements<T>, JoinQueryStream<T>, ResultStream<T> {

    /**
     * Gets wrapped entity {@link Class} instance
     * 
     * @return {@link Class} of entity type T
     */
    Class<T> getEntityType();

    // =========================embedded=field=queries=======================//

    /**
     * Generates query part for embedded entity fields
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> embedded(EntityField<T, F> field, SubQueryConsumer<F, T> consumer);

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
