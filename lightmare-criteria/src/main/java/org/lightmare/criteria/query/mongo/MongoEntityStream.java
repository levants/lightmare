package org.lightmare.criteria.query.mongo;

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryResolver;
import org.lightmare.criteria.query.internal.layers.LayerProvider;
import org.lightmare.criteria.query.mongo.layers.MongoExpressions.Binaries;
import org.lightmare.criteria.query.mongo.layers.MongoExpressions.Unaries;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.StringUtils;

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

    /**
     * Generates uary expression
     * 
     * @param tuple
     * @param expression
     */
    private void operateUnary(QueryTuple tuple, String expression) {

        Unaries unary = Unaries.valueOf(expression);
        String column = getLayerProvider().getColumnName(tuple);
        unary.function.apply(column);
    }

    /**
     * Generates binary expression
     * 
     * @param tuple
     * @param expression
     * @param value
     */
    private void operateBinary(QueryTuple tuple, String expression, Object value) {

        Binaries binary = Binaries.valueOf(expression);
        String column = getLayerProvider().getColumnName(tuple);
        binary.function.apply(column, value);
    }

    @Override
    public <F> MongoStream<T> operate(EntityField<T, F> field, String operator) {
        resolveAndOperate(field, c -> operateUnary(c, operator));
        return this;
    }

    @Override
    public <F> MongoStream<T> operate(EntityField<T, ? extends F> field, Object value, String operator) {
        resolveAndOperate(field, value, (c, v) -> operateBinary(c, operator, v));
        return this;
    }

    @Override
    public <F> MongoStream<T> operateCollection(EntityField<T, F> field, Collection<F> values, String operator) {
        return operate(field, values, operator);
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
