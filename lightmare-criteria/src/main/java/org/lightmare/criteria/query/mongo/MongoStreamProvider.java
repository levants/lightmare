package org.lightmare.criteria.query.mongo;

import org.lightmare.criteria.query.QueryProvider;
import org.lightmare.criteria.query.mongo.layers.MongoProvider;

import com.mongodb.client.MongoDatabase;

/**
 * Factory class for MongoDB queries
 * 
 * @author Levan Tsinadze
 *
 */
public class MongoStreamProvider {

    /**
     * Generates DELETE statements
     * 
     * @param db
     * @param entityType
     * @return {@link org.lightmare.criteria.query.mongo.MongoStream} with
     *         delete statement
     */
    public static <T> MongoStream<T> delete(final MongoDatabase db, Class<T> entityType) {
        return QueryProvider.delete(new MongoProvider(db), entityType, MongoEntityStream<T>::new);
    }

    /**
     * Generates UPDATE statements
     * 
     * @param db
     * @param entityType
     * @return {@link org.lightmare.criteria.query.mongo.MongoStream} with
     *         update statement
     */
    public static <T> MongoStream<T> update(final MongoDatabase db, Class<T> entityType) {
        return QueryProvider.update(new MongoProvider(db), entityType, MongoEntityStream<T>::new);
    }

    /**
     * Generates SELECT statements
     * 
     * @param db
     * @param entityType
     * @return {@link org.lightmare.criteria.query.mongo.MongoStream} with
     *         select statement
     */
    public static <T> MongoStream<T> select(final MongoDatabase db, Class<T> entityType) {
        return QueryProvider.select(new MongoProvider(db), entityType, MongoEntityStream<T>::new);
    }
}
