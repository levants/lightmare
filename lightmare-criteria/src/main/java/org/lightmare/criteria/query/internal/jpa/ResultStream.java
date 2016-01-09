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

import java.util.List;
import java.util.Set;

import org.lightmare.criteria.tuples.ParameterTuple;
import org.lightmare.criteria.utils.CollectionUtils;

/**
 * Interface for query result methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public interface ResultStream<T> {

    /**
     * Gets query parameters
     * 
     * @return java.util.Set} of
     *         {@link org.lightmare.criteria.tuples.ParameterTuple}s
     */
    Set<ParameterTuple> getParameters();

    /**
     * Runs generated query {@link javax.persistence.Query#getSingleResult()}
     * and retrieves single result for element count
     * 
     * @return {@link Long} element count value
     */
    Long count();

    /**
     * Runs generated query {@link javax.persistence.Query#getResultList()} and
     * retrieves result list
     * 
     * @return {@link java.util.List} of query results
     * @see javax.persistence.Query#getResultList()
     */
    List<T> toList();

    /**
     * Runs generated query {@link javax.persistence.Query#getSingleResult()}
     * and retrieves single result
     * 
     * @return T single query result
     * @see javax.persistence.Query#getSingleResult()
     */
    T get();

    /**
     * Executes generates bulk update or delete query
     * {@link javax.persistence.Query#executeUpdate()} and returns number of
     * modified rows
     * 
     * @return <code>int<code/> number of modified rows
     * @see javax.persistence.Query#executeUpdate()
     */
    int execute();

    /**
     * Calls
     * {@link org.lightmare.criteria.query.internal.jpa.ResultStream#toList()}
     * and retrieves first or passed default result if no element was found
     * 
     * @param defaultValue
     * @return T query first or passed default result
     */
    default T firstOrDefault(T defaultValue) {

        T result;

        List<T> results = toList();
        result = CollectionUtils.getFirst(results, defaultValue);

        return result;
    }

    /**
     * Calls
     * {@link org.lightmare.criteria.query.internal.jpa.ResultStream#toList()}
     * and retrieves first result or <code>null</code> if no element was found
     * 
     * @return T query first result
     */
    default T getFirst() {
        return firstOrDefault(null);
    }
}
