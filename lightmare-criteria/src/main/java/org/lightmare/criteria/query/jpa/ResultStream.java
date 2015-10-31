package org.lightmare.criteria.query.jpa;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.lightmare.criteria.tuples.ParameterTuple;

/**
 * Interface for query result methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public interface ResultStream<T extends Serializable> {

    /**
     * Gets query parameters
     * 
     * @return {@link Set} of {@link ParameterTuple}s
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
     * @return {@link List} of query results
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
}
