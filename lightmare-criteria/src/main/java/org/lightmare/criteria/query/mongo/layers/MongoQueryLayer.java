package org.lightmare.criteria.query.mongo.layers;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.TemporalType;

import org.bson.Document;
import org.lightmare.criteria.query.internal.layers.QueryLayer;
import org.lightmare.criteria.tuples.ParameterTuple;
import org.lightmare.criteria.utils.CollectionUtils;

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

    public MongoQueryLayer(final MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    @Override
    public List<T> toList() {
        return Collections.emptyList();
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public int execute() {
        return CollectionUtils.EMPTY;
    }

    @Override
    public void setParameter(String name, Object value) {
    }

    @Override
    public void setParameter(String name, Calendar value, TemporalType temporalType) {
    }

    @Override
    public void setParameter(String name, Date value, TemporalType temporalType) {
    }

    @Override
    public void setParameter(ParameterTuple tuple) {
    }

    @Override
    public void setMaxResults(int maxResult) {
    }

    @Override
    public int getMaxResults() {
        return CollectionUtils.EMPTY;
    }

    @Override
    public void setFirstResult(int startPosition) {
    }

    @Override
    public int getFirstResult() {
        return CollectionUtils.EMPTY;
    }

    @Override
    public void setHint(String hintName, Object value) {
    }

    @Override
    public Map<String, Object> getHints() {
        return null;
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
    }

    @Override
    public void setLockMode(LockModeType lockMode) {
    }
}
