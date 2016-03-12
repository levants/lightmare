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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.bson.conversions.Bson;
import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryResolver;
import org.lightmare.criteria.query.layers.LayerProvider;
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

    /**
     * Applies binary expressions
     * 
     * @param field
     * @param value
     * @param function
     * @return {@link org.lightmare.criteria.query.mongo.MongoStream} current
     *         instance
     */
    private <V> MongoStream<T> apply(Serializable field, V value, BiFunction<String, V, Bson> function) {

        Bson condition = resolveAndApply(field, value, (t, v) -> function.apply(t.getFieldName(), v));
        addCondition(condition);

        return this;
    }

    /**
     * Applies unary expressions
     * 
     * @param field
     * @param function
     * @return {@link org.lightmare.criteria.query.mongo.MongoStream} current
     *         instance
     */
    private <V> MongoStream<T> apply(Serializable field, Function<String, Bson> function) {

        Bson condition = resolveAndApply(field, t -> function.apply(t.getFieldName()));
        addCondition(condition);

        return this;
    }

    @Override
    public <F> MongoStream<T> equal(EntityField<T, F> field, Object value) {
        return apply(field, value, Filters::eq);
    }

    @Override
    public <F> MongoStream<T> notEqual(EntityField<T, F> field, Object value) {
        return apply(field, value, Filters::ne);
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> gt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return apply(field, value, Filters::gt);
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> lt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return apply(field, value, Filters::lt);
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> ge(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return apply(field, value, Filters::gte);
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> le(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return apply(field, value, Filters::lte);
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
        return apply(field, values, Filters::in);
    }

    @Override
    public <F> MongoStream<T> notIn(EntityField<T, F> field, Collection<F> values) {
        return apply(field, values, Filters::nin);
    }

    @Override
    public <F> MongoStream<T> isNull(EntityField<T, F> field) {
        return apply(field, Boolean.FALSE, Filters::exists);
    }

    @Override
    public <F> MongoStream<T> isNotNull(EntityField<T, F> field) {
        return apply(field, Filters::exists);
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

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> greaterThan(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> lessThan(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> greaterThanOrEqualTo(
            EntityField<T, Comparable<? super F>> field, Comparable<? super F> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <F extends Comparable<? super F>> MongoStream<T> lessThanOrEqualTo(
            EntityField<T, Comparable<? super F>> field, Comparable<? super F> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<T> toList() {
        return null;
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public int execute() {
        return 0;
    }
}
