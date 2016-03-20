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

import java.util.function.Consumer;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.internal.orm.links.Operators;

/**
 * Query stream of {@link String} based queries for abstract data base layers
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <S>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 */
public interface QueryStream<T, S extends QueryStream<T, ? super S>> extends LambdaStream<T, S> {

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

    // ======================WHERE=AND=OR=clauses=with=stream================//

    /**
     * Generates AND / OR connector and sets brackets
     * 
     * @param connector
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default S brackets(Consumer<QueryStream<T, S>> connector, QueryConsumer<T, S> consumer) {

        S stream;

        connector.accept(this);
        stream = brackets(consumer);

        return stream;
    }

    /**
     * AND clause in lambda expression manner
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    @Override
    default S and(QueryConsumer<T, S> consumer) {
        return brackets(QueryStream::and, consumer);
    }

    /**
     * OR clause in lambda expression manner
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default S or(QueryConsumer<T, S> consumer) {
        return brackets(QueryStream::or, consumer);
    }
}
