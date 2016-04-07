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
package org.lightmare.criteria.query.internal.orm;

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.query.QueryStream;

/**
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
public interface JoinOperator<T, Q extends QueryStream<T, ? super Q>> {

    /**
     * Processes JOIN statement with ON clause
     * 
     * @param field
     * @param expression
     * @param on
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    <E, C extends Collection<E>, S extends LambdaStream<E, ? super S>> Q procesJoin(EntityField<T, C> field,
            String expression, QueryConsumer<E, S> on, QueryConsumer<E, S> consumer);

    /**
     * Processes JOIN statement
     * 
     * @param field
     * @param expression
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, C extends Collection<E>, S extends LambdaStream<E, ? super S>> Q procesJoin(EntityField<T, C> field,
            String expression, QueryConsumer<E, S> consumer) {
        return procesJoin(field, expression, null, consumer);
    }

    /**
     * Processes JOIN statement with ON clause with other entity type
     * 
     * @param joinType
     * @param expression
     * @param on
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    <E, S extends LambdaStream<E, ? super S>> Q procesJoin(Class<E> joinType, String expression, QueryConsumer<E, S> on,
            QueryConsumer<E, S> consumer);

    /**
     * Processes JOIN statement with other entity type
     * 
     * @param joinType
     * @param expression
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default <E, S extends LambdaStream<E, ? super S>> Q procesJoin(Class<E> joinType, String expression,
            QueryConsumer<E, S> consumer) {
        return procesJoin(joinType, expression, null, consumer);
    }
}
