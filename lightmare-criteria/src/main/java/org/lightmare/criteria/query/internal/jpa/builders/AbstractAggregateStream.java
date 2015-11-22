package org.lightmare.criteria.query.internal.jpa.builders;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.GroupByConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Aggregates;
import org.lightmare.criteria.query.internal.jpa.links.Filters;
import org.lightmare.criteria.tuples.AggregateTuple;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Abstract class for aggregate functions
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public abstract class AbstractAggregateStream<T> extends AbstractGroupByStream<T> {

    protected AbstractAggregateStream(final EntityManager em, final Class<T> entityType, final String alias) {
        super(em, entityType, alias);
    }

    protected void oppAggregate(Serializable field, Aggregates aggregate) {

        QueryTuple tuple = compose(field);
        aggregateTuple(tuple, aggregate);
    }

    private static <C> Class<C> getAggregateType(Class<C> type, QueryTuple tuple) {

        Class<C> resulType;

        if (type == null) {
            resulType = tuple.getFieldType();
        } else {
            resulType = type;
        }

        return resulType;
    }

    /**
     * Generates aggregate query prefix
     * 
     * @param buffer
     */
    private void appendAggregateFields(AggregateTuple tuple, StringBuilder buffer) {

        String expression = tuple.expression();
        StringUtils.clear(buffer);
        buffer.append(Filters.SELECT);
        buffer.append(expression);
    }

    /**
     * Generates aggregate query prefix
     */
    protected void appendAggregate(StringBuilder buffer) {

        if (CollectionUtils.valid(aggregateFields)) {
            aggregateFields.forEach(tuple -> appendAggregateFields(tuple, buffer));
        }
    }

    @Override
    public <F, R extends Number> QueryStream<R> aggregate(EntityField<T, F> field, Aggregates function, Class<R> type) {

        QueryStream<R> stream;

        QueryTuple tuple = compose(field);
        aggregateTuple(tuple, function);
        Class<R> selectType = getAggregateType(type, tuple);
        stream = new SelectStream<>(this, selectType);

        return stream;
    }

    @Override
    public <N extends Number> QueryStream<N> aggregate(EntityField<T, N> field, Aggregates function) {

        QueryStream<N> stream;

        Class<N> type = null;
        stream = aggregate(field, function, type);

        return stream;
    }

    @Override
    public <F> QueryStream<Object[]> aggregate(EntityField<T, F> field, Aggregates function,
            GroupByConsumer<T> consumer) {
        oppAggregate(field, function);
        acceptConsumer(consumer, this);

        return this.selectStream;
    }
}
