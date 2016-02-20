package org.lightmare.criteria.query.internal.connectors;

import java.util.List;

/**
 * Database abstract layer
 * 
 * @author Levan Tsinadze
 * 
 * @param <T>
 *            entity type parameter
 */
public interface QueryLayer<T> {

    QueryLayer<T> select(Class<T> type, String sql);

    QueryLayer<T> update(Class<T> type, String sql);

    QueryLayer<T> delete(Class<T> type, String sql);

    /**
     * Retrieves result from DB throw layer
     * 
     * @return {@link java.util.List} of T type elements
     */
    List<T> toList();

    /**
     * Retrieves result from DB throw layer
     * 
     * @return T single result
     */
    T get();

    /**
     * Executes UPDATE / DELETE on DB table throw layer
     * 
     * @return <code>int</code> updated rows
     */
    int execute();
}
