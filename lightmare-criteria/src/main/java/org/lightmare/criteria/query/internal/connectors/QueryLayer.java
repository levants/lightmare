package org.lightmare.criteria.query.internal.connectors;

import java.util.List;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;

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

    /**
     * Sets max results to query
     * 
     * @param maxResults
     */
    void setMaxResults(int maxResult);

    int getMaxResults();

    void setFirstResult(int startPosition);

    int getFirstResult();

    void setHint(String hintName, Object value);

    Map<String, Object> getHints();

    void setFlushMode(FlushModeType flushMode);

    void setLockMode(LockModeType lockMode);

    void close();
}
