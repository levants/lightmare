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

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.providers.JpaQueryStream;

/**
 * Provides methods to process sub queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for sub query
 */
interface SubQueryOperator<T> {

    /**
     * Processes sub query for entity field instant operator and sub query
     * consumer
     * 
     * @param field
     * @param operator
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         current instance
     */
    <F, S> JpaQueryStream<T> operateSubQuery(EntityField<T, F> field, String operator, Class<S> type,
            QueryConsumer<S, JpaQueryStream<S>> consumer);

    /**
     * Processes sub query for entity field instant operator and sub query
     * consumer
     * 
     * @param value
     * @param operator
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         current instance
     */
    <F, S> JpaQueryStream<T> operateSubQuery(Object value, String operator, Class<S> type,
            QueryConsumer<S, JpaQueryStream<S>> consumer);

    /**
     * Processes sub query for entity field instant operator and sub query
     * consumer
     * 
     * @param function
     * @param operator
     * @param type
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         current instance
     */
    <F, S> JpaQueryStream<T> operateFunctionWithSubQuery(FunctionConsumer<T> function, String operator, Class<S> type,
            QueryConsumer<S, JpaQueryStream<S>> consumer);
}
