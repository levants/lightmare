package org.lightmare.criteria.query.internal.connectors;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.TemporalType;

/**
 * Database abstract layer
 * 
 * @author Levan Tsinadze
 * 
 * @param <T>
 *            entity type parameter
 */
public interface QueryLayer<T> {

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
     * Sets query parameters by names
     * 
     * @param name
     * @param value
     */
    void setParameter(String name, Object value);

    /**
     * Sets query parameters by names and temporal types
     * 
     * @param name
     * @param value
     * @param temporalType
     */
    void setParameter(String name, Calendar value, TemporalType temporalType);

    /**
     * Sets query parameters by names and temporal types
     * 
     * @param name
     * @param value
     * @param temporalType
     */
    void setParameter(String name, Date value, TemporalType temporalType);

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
}
