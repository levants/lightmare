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
package org.lightmare.criteria.query.internal.jpa.builders;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.HavingConsumer;
import org.lightmare.criteria.functions.SelectConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Aggregates;
import org.lightmare.criteria.tuples.AggregateTuple;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Abstract utility class for GROUP BY processing
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
abstract class AbstractGroupByStream<T> extends AbstractSelectStatements<T> {

    protected SelectStream<T, ?> selectStream;

    protected Set<AggregateTuple> aggregateFields;

    protected Queue<AggregateTuple> aggregateQueue;

    protected AbstractGroupByStream(final EntityManager em, final Class<T> entityType, final String alias) {
        super(em, entityType, alias);
    }

    @Override
    protected Set<AggregateTuple> getAggregateFields() {
        return aggregateFields;
    }

    private void setAggregateFields(Set<AggregateTuple> aggregateFields) {
        this.aggregateFields = aggregateFields;
    }

    @Override
    protected Queue<AggregateTuple> getAggregateQueue() {
        return aggregateQueue;
    }

    private void setAggregateQueue(Queue<AggregateTuple> aggregateQueue) {
        this.aggregateQueue = aggregateQueue;
    }

    /**
     * Generates {@link org.lightmare.criteria.tuples.AggregateTuple} and adds
     * to cache for processing
     * 
     * @param tuple
     * @param aggregate
     */
    protected void aggregateTuple(QueryTuple tuple, Aggregates aggregate) {

        ObjectUtils.thisOrDefault(aggregateFields, HashSet<AggregateTuple>::new, this::setAggregateFields);
        AggregateTuple aggregateTuple = AggregateTuple.of(tuple, aggregate, alias);
        if (aggregateFields.add(aggregateTuple)) {
            ObjectUtils.thisOrDefault(aggregateQueue, LinkedList<AggregateTuple>::new, this::setAggregateQueue);
            aggregateQueue.offer(aggregateTuple);
        }
    }

    @Override
    public void having(HavingConsumer<T> consumer) {

        if (Objects.nonNull(consumer) && CollectionUtils.valid(aggregateQueue)) {
            AggregateTuple havingTuple = aggregateQueue.poll();
            HavingProcessor<T> havingProcessor = new HavingProcessor<T>(having, havingTuple);
            consumer.accept(havingProcessor);
        }
    }

    /**
     * Generates appropriated query stream
     * 
     * @return {@link QueryStream} for special type parameter
     */
    private <F> QueryStream<F> generateStream(Class<F> type) {

        QueryStream<F> stream;

        selectStream = new SelectStream<>(this, type);
        stream = ObjectUtils.cast(selectStream);

        return stream;
    }

    /**
     * Processes select method call for all arguments
     * 
     * @param fields
     * @return {@link QueryStream} for select method
     */
    private QueryStream<Object[]> groupByField(Serializable field) {

        QueryStream<Object[]> stream;

        oppGroups(Collections.singleton(field));
        stream = generateStream(Object[].class);

        return stream;
    }

    @Override
    public QueryStream<Object[]> groupBy(Select select) {

        QueryStream<Object[]> stream;

        oppGroups(select.getFields());
        stream = generateStream(Object[].class);

        return stream;
    }

    @Override
    public QueryStream<Object[]> group(SelectConsumer select) {

        Select columns = Select.select();
        ObjectUtils.accept(select, columns);

        return groupBy(columns);
    }

    @Override
    public <F> QueryStream<Object[]> groupBy(EntityField<T, F> field) {
        return groupByField(field);
    }
}
