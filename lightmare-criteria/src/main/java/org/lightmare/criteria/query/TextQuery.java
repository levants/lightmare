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

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.orm.links.Operators;

/**
 * Interface for base methods of {@link String} based queries with
 * {@link org.lightmare.criteria.query.LambdaStream} for abstract data base
 * layers
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 */
interface TextQuery<T, Q extends QueryStream<T, ? super Q>> extends LambdaStream<T, Q> {

    /**
     * Default alias for entity
     */
    String DEFAULT_ALIAS = "c";

    /**
     * Appends to generated query body custom clause
     * 
     * @param clause
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    Q appendBody(Object clause);

    // ======================================================================//

    /**
     * Opens bracket in query body
     * 
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default Q openBracket() {
        return appendBody(Operators.OPEN_BRACKET);
    }

    /**
     * Closes bracket in query body
     * 
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default Q closeBracket() {
        return appendBody(Operators.CLOSE_BRACKET);
    }

    /**
     * Generates query part for instant field and operator
     * 
     * @param field
     * @param operator
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    <F> Q operate(EntityField<T, F> field, String operator);

    /**
     * Generates query part for instant field with parameter and operator
     * 
     * @param field
     * @param value
     * @param operator
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    <F> Q operate(EntityField<T, ? extends F> field, Object value, String operator);

    /**
     * Creates query part in brackets
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    Q brackets(QueryConsumer<T, Q> consumer);

    // ====================== Generated=SQL=String============================//

    /**
     * Gets generated query
     * 
     * @return {@link String} query
     */
    String sql();
}
