package org.lightmare.criteria.query.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.conversions.Bson;
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

    private final List<Bson> ands = new ArrayList<>();

    private final List<Bson> ors = new ArrayList<>();

    private List<Bson> current = ands;

    public MongoEntityStream(final LayerProvider provider, Class<T> entityType) {
        this.provider = provider;
        this.entityType = entityType;
    }

    private void addCondition(Bson condition) {
        current.add(condition);
        current = ands;
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

        Bson condition = resolveAndApply(field, value, (t, v) -> Filters.eq(t.getFieldName(), v));
        addCondition(condition);

        return this;
    }

    @Override
    public <F> MongoStream<T> notEqual(EntityField<T, F> field, Object value) {

        Bson condition = resolveAndApply(field, value, (t, v) -> Filters.ne(t.getFieldName(), v));
        addCondition(condition);

        return this;
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> gt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {

        Bson condition = resolveAndApply(field, value, (t, v) -> Filters.gt(t.getFieldName(), v));
        addCondition(condition);

        return this;
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> lt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {

        Bson condition = resolveAndApply(field, value, (t, v) -> Filters.lt(t.getFieldName(), v));
        addCondition(condition);

        return this;
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> ge(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {

        Bson condition = resolveAndApply(field, value, (t, v) -> Filters.gte(t.getFieldName(), v));
        addCondition(condition);

        return this;
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> le(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {

        Bson condition = resolveAndApply(field, value, (t, v) -> Filters.lte(t.getFieldName(), v));
        addCondition(condition);

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

        Bson condition = resolveAndApply(field, values, (t, v) -> Filters.in(t.getFieldName(), v));
        addCondition(condition);

        return this;
    }

    @Override
    public <F> MongoStream<T> notIn(EntityField<T, F> field, Collection<F> values) {

        Bson condition = resolveAndApply(field, values, (t, v) -> Filters.not(Filters.in(t.getFieldName(), v)));
        addCondition(condition);

        return this;
    }

    @Override
    public <F> MongoStream<T> isNull(EntityField<T, F> field) {

        Bson condition = resolveAndApply(field, t -> Filters.exists(t.getFieldName()));
        addCondition(condition);

        return this;
    }

    @Override
    public <F> MongoStream<T> isNotNull(EntityField<T, F> field) {

        Bson condition = resolveAndApply(field, t -> Filters.not(Filters.exists(t.getFieldName())));
        addCondition(condition);

        return this;
    }

    @Override
    public MongoStream<T> and() {
        current = ands;
        return this;
    }

    @Override
    public MongoStream<T> or() {
        current = ors;
        return this;
    }
}
