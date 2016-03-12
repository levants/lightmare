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
        return QueryProvider.delete(new MongoProvider<>(db, entityType), entityType, MongoEntityStream<T>::new);
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
        return QueryProvider.update(new MongoProvider<>(db, entityType), entityType, MongoEntityStream<T>::new);
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
        return QueryProvider.select(new MongoProvider<>(db, entityType), entityType, MongoEntityStream<T>::new);
    }
}
