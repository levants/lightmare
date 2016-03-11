package org.lightmare.criteria.query.mongo;

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryResolver;
import org.lightmare.criteria.query.internal.layers.LayerProvider;
import org.lightmare.criteria.utils.StringUtils;

import com.mongodb.client.model.Filters;

/**
 * Builder of MongoDB queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public class MongoEntityStream<T> implements MongoStream<T>, QueryResolver<T> {

    private final LayerProvider provider;

    private Class<T> entityType;

    public MongoEntityStream(final LayerProvider provider, Class<T> entityType) {
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
    public String sql() {
        return StringUtils.EMPTY;
    }

    @Override
    public <F> MongoStream<T> equal(EntityField<T, F> field, Object value) {
        resolveAndOperate(field, value, (t, v) -> Filters.eq(getLayerProvider().getColumnName(t), v));
        return this;
    }

    @Override
    public <F> MongoStream<T> notEqual(EntityField<T, F> field, Object value) {
        resolveAndOperate(field, value, (t, v) -> Filters.ne(getLayerProvider().getColumnName(t), v));
        return this;
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> gt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        resolveAndOperate(field, value, (t, v) -> Filters.gt(getLayerProvider().getColumnName(t), v));
        return this;
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> lt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        resolveAndOperate(field, value, (t, v) -> Filters.lt(getLayerProvider().getColumnName(t), v));
        return this;
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> ge(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        resolveAndOperate(field, value, (t, v) -> Filters.gte(getLayerProvider().getColumnName(t), v));
        return this;
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> le(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        resolveAndOperate(field, value, (t, v) -> Filters.lte(getLayerProvider().getColumnName(t), v));
        return this;
    }

    @Override
    public MongoStream<T> like(EntityField<T, String> field, String value) {
        return this;
    }

    @Override
    public MongoStream<T> notLike(EntityField<T, String> field, String value) {
        return this;
    }

    @Override
    public <F> MongoStream<T> in(EntityField<T, F> field, Collection<F> values) {
        return null;
    }

    @Override
    public <F> MongoStream<T> notIn(EntityField<T, F> field, Collection<F> values) {
        resolveAndOperate(field, values, (t, v) -> Filters.in(getLayerProvider().getColumnName(t), v));
        return this;
    }

    @Override
    public <F> MongoStream<T> isNull(EntityField<T, F> field) {
        resolveAndOperate(field, t -> Filters.exists(getLayerProvider().getColumnName(t)));
        return this;
    }

    @Override
    public <F> MongoStream<T> isNotNull(EntityField<T, F> field) {
        resolveAndOperate(field, t -> Filters.not(Filters.exists(getLayerProvider().getColumnName(t))));
        return this;
    }

    @Override
    public MongoStream<T> and() {
        return this;
    }

    @Override
    public MongoStream<T> or() {
        return this;
    }
}
