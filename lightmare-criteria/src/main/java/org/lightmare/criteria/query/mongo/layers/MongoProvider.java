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
package org.lightmare.criteria.query.mongo.layers;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.lightmare.criteria.annotations.DBColumn;
import org.lightmare.criteria.annotations.DBTable;
import org.lightmare.criteria.query.internal.layers.LayerProvider;
import org.lightmare.criteria.query.internal.layers.QueryLayer;
import org.lightmare.criteria.tuples.QueryTuple;
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
public class MongoProvider implements LayerProvider {

    private final MongoDatabase db;

    public MongoProvider(final MongoDatabase db) {
        this.db = db;
    }

    public MongoDatabase getMongoDataBase() {
        return db;
    }

    @Override
    public String and() {
        return null;
    }

    @Override
    public String or() {
        return null;
    }

    @Override
    public String equal() {
        return MongoExpressions.Binaries.EQ.expression;
    }

    @Override
    public String notEqual() {
        return MongoExpressions.Binaries.NE.expression;
    }

    @Override
    public String lessThen() {
        return MongoExpressions.Binaries.LT.expression;
    }

    @Override
    public String lessThenOrEqual() {
        return MongoExpressions.Binaries.LTE.expression;
    }

    @Override
    public String greaterThen() {
        return MongoExpressions.Binaries.GT.expression;
    }

    @Override
    public String greaterThenOrEqual() {
        return MongoExpressions.Binaries.GTE.expression;
    }

    @Override
    public String like() {
        return null;
    }

    @Override
    public String notLike() {
        return null;
    }

    @Override
    public String in() {
        return MongoExpressions.Binaries.IN.expression;
    }

    @Override
    public String notIn() {
        return MongoExpressions.Binaries.NIN.expression;
    }

    @Override
    public String isNull() {
        return MongoExpressions.Unaries.EXISTS.expression;
    }

    @Override
    public String isNotNull() {
        return StringUtils.EMPTY;
    }

    @Override
    public <T> QueryLayer<T> query(Object sql, Class<T> type) {

        QueryLayer<T> query;

        String collectionName = getTableName(type);
        MongoCollection<Document> collection = db.getCollection(collectionName);
        Bson filter = ObjectUtils.cast(sql);
        query = new MongoQueryLayer<>(collection, filter, type);

        return query;
    }

    @Override
    public QueryLayer<?> query(Object sql) {
        return null;
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
