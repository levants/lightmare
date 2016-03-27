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
package org.lightmare.criteria.query.providers.mongo;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.lightmare.criteria.annotations.DBColumn;
import org.lightmare.criteria.annotations.DBTable;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.query.layers.QueryLayer;
import org.lightmare.criteria.query.providers.mongo.layers.MongoQueryLayer;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Layed provider for MongoDB queries
 * 
 * @author Levan Tsinadze
 *
 */
public class MongoProvider<T> implements LayerProvider {

    private final MongoDatabase db;

    private final Class<T> entityType;

    public MongoProvider(final MongoDatabase db, final Class<T> entityType) {
        this.db = db;
        this.entityType = entityType;
    }

    public MongoDatabase getMongoDataBase() {
        return db;
    }

    @Override
    public <Q> QueryLayer<Q> query(Class<Q> type, Object... params) {

        QueryLayer<Q> query;

        String collectionName = getTableName(type);
        MongoCollection<Document> collection = db.getCollection(collectionName);
        Bson filter = CollectionUtils.getFirstType(params);
        query = new MongoQueryLayer<>(collection, filter, type);

        return query;
    }

    @Override
    public QueryLayer<?> query(Object... params) {
        return query(entityType, params);
    }

    @Override
    public String getTableName(Class<?> type) {
        return ObjectUtils.ifIsNull(type.getAnnotation(DBTable.class), c -> type.getSimpleName(), DBTable::value);
    }

    private static String getFromField(QueryTuple tuple) {
        return ObjectUtils.ifIsNull(tuple.getField().getAnnotation(DBColumn.class), c -> tuple.getFieldName(),
                DBColumn::value);
    }

    @Override
    public String getColumnName(QueryTuple tuple) {
        return ObjectUtils.ifIsNull(tuple.getField(), c -> tuple.getFieldName(), c -> getFromField(tuple));
    }

    @Override
    public String getSelectType(String alias) {
        return StringUtils.EMPTY;
    }

    @Override
    public String getCountType(String alias) {
        return StringUtils.EMPTY;
    }

    @Override
    public String alias() {
        return StringUtils.EMPTY;
    }

    @Override
    public void close() {
    }
}
