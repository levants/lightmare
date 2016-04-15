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

import java.util.List;

import org.lightmare.criteria.utils.CollectionUtils;

/**
 * Query result retrieving methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameters
 */
interface QueryResult<T> {

    /**
     * Runs generated query {@link javax.persistence.Query#getResultList()} and
     * retrieves result list
     * 
     * @return {@link java.util.List} of query results
     */
    List<T> toList();

    /**
     * Runs generated query {@link javax.persistence.Query#getSingleResult()}
     * and retrieves single result
     * 
     * @return T single query result
     */
    T get();

    /**
     * Executes generates bulk update or delete query
     * {@link javax.persistence.Query#executeUpdate()} and returns number of
     * modified rows
     * 
     * @return <code>int<code/> number of modified rows
     */
    int execute();

    /**
     * Gets first or default value from query results
     * 
     * @param defaultValue
     * @return T first or default value
     */
    default T firstOrDefault(T defaultValue) {

        T result;

        List<T> results = toList();
        result = CollectionUtils.getFirst(results, defaultValue);

        return result;
    }

    /**
     * Gets first value or <code>null</code> if no data found from query results
     * 
     * @return T first result or <code>null</code> if results are empty
     */
    default T getFirst() {
        return firstOrDefault(null);
    }
}
