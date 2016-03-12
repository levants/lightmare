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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.lightmare.criteria.query.layers.QueryLayer;
import org.lightmare.criteria.query.mongo.layers.MongoRetriever.FieldType;
import org.lightmare.criteria.utils.ClassUtils;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;

import com.mongodb.client.MongoCollection;

/**
 * Layer for MongoDB query
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public class MongoQueryLayer<T> implements QueryLayer<T> {

    private final MongoCollection<Document> collection;

    private final Bson filter;

    private final Class<T> type;

    public MongoQueryLayer(final MongoCollection<Document> collection, final Bson filter, final Class<T> type) {
        this.collection = collection;
        this.filter = filter;
        this.type = type;
    }

    private T resolve(Document document) {

        T value = ClassUtils.newInstance(type);

        List<FieldType> types = MongoRetriever.getColumns(type);
        types.forEach(c -> c.set(document, value));

        return value;
    }

    public void add(Document document, List<T> results) {
        T result = resolve(document);
        ObjectUtils.nonNull(result, results::add);
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    @Override
    public List<T> toList() {

        List<T> results = new ArrayList<>();

        Iterable<Document> items = collection.find(filter);
        items.forEach(c -> add(c, results));
        return Collections.emptyList();
    }

    @Override
    public T get() {

        T result;

        Document document = collection.find(filter).first();
        result = resolve(document);

        return result;
    }

    @Override
    public int execute() {
        return CollectionUtils.EMPTY;
    }
}
