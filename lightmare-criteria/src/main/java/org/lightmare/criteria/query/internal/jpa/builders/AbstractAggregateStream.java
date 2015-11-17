package org.lightmare.criteria.query.internal.jpa.builders;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.GroupByConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Aggregates;
import org.lightmare.criteria.tuples.AggregateTuple;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Abstract class for aggregate functions
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
abstract class AbstractAggregateStream<T> extends AbstractGroupByStream<T> {

    protected Set<AggregateTuple> aggregateFields;

    protected AbstractAggregateStream(final EntityManager em, final Class<T> entityType, final String alias) {
        super(em, entityType, alias);
    }

    private void initAggregateFields() {

        if (aggregateFields == null) {
            aggregateFields = new HashSet<>();
        }
    }

    protected void oppAggregate(Serializable field, Aggregates aggregate) {

        QueryTuple tuple = compose(field);
        initAggregateFields();
        AggregateTuple aggregateTuple = AggregateTuple.of(tuple, aggregate);
        aggregateFields.add(aggregateTuple);
    }
    
    @Override
    public <F> QueryStream<Object[]> count(EntityField<T, F> field, GroupByConsumer<T> consumer) {

        oppAggregate(field, Aggregates.COUNT);
        acceptConsumer(consumer, this);

        return this.selectStream;
    }
}
