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
package org.lightmare.criteria.query.internal.orm.builders;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.HavingConsumer;
import org.lightmare.criteria.functions.SelectConsumer;
import org.lightmare.criteria.query.internal.connectors.LayerProvider;
import org.lightmare.criteria.query.internal.orm.links.Aggregates;
import org.lightmare.criteria.query.providers.JpaQueryStream;
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

    // Aggregate fields
    protected Set<AggregateTuple> aggregateFields;

    protected Queue<AggregateTuple> aggregateQueue;

    protected AbstractGroupByStream(final LayerProvider provider, final Class<T> entityType, final String alias) {
        super(provider, entityType, alias);
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

    /**
     * Generates HAVING clause by
     * {@link org.lightmare.criteria.functions.HavingConsumer} implementation
     * 
     * @param consumer
     */
    private void generateHaving(HavingConsumer<T> consumer) {

        AggregateTuple havingTuple = aggregateQueue.poll();
        HavingProcessor<T> havingProcessor = new HavingProcessor<T>(having, havingTuple);
        consumer.accept(havingProcessor);
    }

    /**
     * Generates HAVING clause by
     * {@link org.lightmare.criteria.functions.HavingConsumer} implementation if
     * it's valid
     * 
     * @param consumer
     */
    private void validateAndGenerate(HavingConsumer<T> consumer) {
        CollectionUtils.valid(aggregateQueue, c -> generateHaving(consumer));
    }

    @Override
    public void having(HavingConsumer<T> consumer) {
        ObjectUtils.nonNull(consumer, this::validateAndGenerate);
    }

    /**
     * Generates appropriated query stream
     * 
     * @return {@link org.lightmare.criteria.tuples.JpaQueryStream} for special
     *         type parameter
     */
    private <F> JpaQueryStream<F> generateStream(Class<F> type) {

        JpaQueryStream<F> stream;

        selectStream = new SelectStream<>(this, type);
        stream = ObjectUtils.cast(selectStream);

        return stream;
    }

    /**
     * Processes select method call for all arguments
     * 
     * @param fields
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream} for select
     *         method
     */
    private JpaQueryStream<Object[]> groupByField(Serializable field) {

        JpaQueryStream<Object[]> stream;

        oppGroups(Collections.singleton(field));
        stream = generateStream(Object[].class);

        return stream;
    }

    @Override
    public JpaQueryStream<Object[]> groupBy(Select select) {

        JpaQueryStream<Object[]> stream;

        oppGroups(select.getFields());
        stream = generateStream(Object[].class);

        return stream;
    }

    @Override
    public JpaQueryStream<Object[]> group(SelectConsumer select) {

        Select columns = Select.select();
        ObjectUtils.accept(select, columns);

        return groupBy(columns);
    }

    @Override
    public <F> JpaQueryStream<Object[]> groupBy(EntityField<T, F> field) {
        return groupByField(field);
    }
}
