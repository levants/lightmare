package org.lightmare.criteria.query.mongo;

import org.lightmare.criteria.query.QueryStream;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryStream} for
 * MongoDB queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface MongoStream<T> extends QueryStream<T, MongoStream<T>> {

}
