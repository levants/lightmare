package org.lightmare.criteria.query.providers;

import org.lightmare.criteria.query.QueryStream;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryStream} for JPA
 * criteria queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface CriteriaQueryStream<T> extends QueryStream<T, CriteriaQueryStream<T>> {

}
