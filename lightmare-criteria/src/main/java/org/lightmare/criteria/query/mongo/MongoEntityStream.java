package org.lightmare.criteria.query.mongo;

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.internal.layers.LayerProvider;
import org.lightmare.criteria.query.mongo.layers.MongoProvider;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Builder of MongoDB queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public class MongoEntityStream<T> implements MongoStream<T>, MongoResolver<T> {

    private final MongoProvider provider;

    private Class<T> entityType;

    public MongoEntityStream(final MongoProvider provider, Class<T> entityType) {
        this.provider = provider;
        this.entityType = entityType;
    }

    @Override
    public LayerProvider getLayerProvider() {
        return provider;
    }

    @Override
    public Class<T> getEntityType() {
        return entityType;
    }

    @Override
    public String getAlias() {
        return StringUtils.EMPTY;
    }

    @Override
    public <F> MongoStream<T> operate(EntityField<T, F> field, String operator) {
        resolveAndOperate(field, operator);
        return this;
    }

    @Override
    public <F> MongoStream<T> operate(EntityField<T, ? extends F> field, Object value, String operator) {
        resolveAndOperate(field, operator, value);
        return this;
    }

    @Override
    public <F> MongoStream<T> operateCollection(EntityField<T, F> field, Collection<F> values, String operator) {
        return operate(field, values, operator);
    }

    @Override
    public MongoStream<T> appendOperator(Object operator) {
        return this;
    }

    @Override
    public MongoStream<T> where() {
        return this;
    }

    @Override
    public MongoStream<T> brackets(QueryConsumer<T, MongoStream<T>> consumer) {
        return this;
    }

    @Override
    public String sql() {
        return StringUtils.EMPTY;
    }
}
