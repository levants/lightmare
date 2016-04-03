package org.lightmare.criteria.query;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.internal.orm.links.Operators;

/**
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <S>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 */
interface TextQuery<T, S extends QueryStream<T, ? super S>> extends LambdaStream<T, S> {

    // Entity default alias
    String DEFAULT_ALIAS = "c";

    /**
     * Appends to generated query body custom clause
     * 
     * @param clause
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    S appendBody(Object clause);

    // ======================================================================//

    /**
     * Opens bracket in query body
     * 
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default S openBracket() {
        return appendBody(Operators.OPEN_BRACKET);
    }

    /**
     * Closes bracket in query body
     * 
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default S closeBracket() {
        return appendBody(Operators.CLOSE_BRACKET);
    }

    /**
     * Generates query part for instant field and operator
     * 
     * @param field
     * @param operator
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    <F> S operate(EntityField<T, F> field, String operator);

    /**
     * Generates query part for instant field with parameter and operator
     * 
     * @param field
     * @param value
     * @param operator
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    <F> S operate(EntityField<T, ? extends F> field, Object value, String operator);

    /**
     * Creates query part in brackets
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    S brackets(QueryConsumer<T, S> consumer);

    // ====================== Generated=SQL=String============================//

    /**
     * Gets generated query
     * 
     * @return {@link String} query
     */
    String sql();
}
