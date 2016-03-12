package org.lightmare.criteria.query;

import java.util.List;

import org.lightmare.criteria.utils.CollectionUtils;

/**
 * Query result retriving methods
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
     * @return T first result or <code>null</code>
     */
    default T getFirst() {
        return firstOrDefault(null);
    }
}
