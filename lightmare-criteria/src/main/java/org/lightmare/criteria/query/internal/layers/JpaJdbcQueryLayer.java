package org.lightmare.criteria.query.internal.layers;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.TemporalType;

import org.lightmare.criteria.tuples.ParameterTuple;

/**
 * Query lauer for JPA / JDBC queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface JpaJdbcQueryLayer<T> extends QueryLayer<T> {

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
     * Sets query parameters by
     * {@link org.lightmare.criteria.tuples.ParameterTuple} instance
     * 
     * @param tuple
     */
    void setParameter(ParameterTuple tuple);

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
