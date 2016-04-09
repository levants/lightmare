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
package org.lightmare.criteria.query.orm.builders;

import java.io.Serializable;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.GroupByConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.query.orm.links.Aggregates;
import org.lightmare.criteria.query.orm.links.Clauses;
import org.lightmare.criteria.tuples.AggregateTuple;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Abstract class for aggregate functions
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * 
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 * @param <O>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
abstract class AbstractAggregateStream<T, Q extends QueryStream<T, ? super Q>, O extends QueryStream<Object[], ? super O>>
        extends AbstractGroupByStream<T, Q, O> {

    protected AbstractAggregateStream(final LayerProvider provider, final Class<T> entityType) {
        super(provider, entityType);
    }

    /**
     * Generates aggregate field and
     * {@link org.lightmare.criteria.query.orm.links.Aggregates} type
     * expression
     * 
     * @param field
     * @param aggregate
     */
    protected void oppAggregate(Serializable field, Aggregates aggregate) {
        QueryTuple tuple = resolve(field);
        aggregateTuple(tuple, aggregate);
    }

    /**
     * Generates and appends aggregate query prefix to buffer
     * 
     * @param buffer
     */
    private void appendAggregateFields(AggregateTuple tuple, StringBuilder buffer) {

        String expression = tuple.expression();
        buffer.append(Clauses.SELECT);
        buffer.append(expression);
    }

    /**
     * Clears passed {@link StringBuilder} and append aggregate clauses
     * 
     * @param buffer
     */
    private void clearAndAppendAggregate(StringBuilder buffer) {
        StringUtils.clear(buffer);
        aggregateFields.forEach(tuple -> appendAggregateFields(tuple, buffer));
    }

    /**
     * Generates aggregate query prefix
     */
    protected void appendAggregate(StringBuilder buffer) {
        CollectionUtils.valid(aggregateFields, c -> clearAndAppendAggregate(buffer));
    }

    @Override
    public <F, R extends Number, L extends QueryStream<R, ? super L>> L aggregate(EntityField<T, F> field,
            Aggregates function, Class<R> type) {

        L stream;

        QueryTuple tuple = compose(field);
        aggregateTuple(tuple, function);
        Class<R> selectType = ObjectUtils.getAndCast(() -> ObjectUtils.thisOrDefault(type, tuple::getFieldGenericType));
        stream = castSelectQuery(selectType);

        return stream;
    }

    @Override
    public <N extends Number, L extends QueryStream<N, ? super L>> L aggregate(EntityField<T, N> field,
            Aggregates function) {

        L stream;

        Class<N> type = null;
        stream = aggregate(field, function, type);

        return stream;
    }

    @Override
    public <F> O aggregate(EntityField<T, F> field, Aggregates function, GroupByConsumer<T, O> consumer) {

        O stream;

        oppAggregate(field, function);
        ObjectUtils.accept(consumer, this);
        stream = ObjectUtils.cast(selectStream);

        return stream;
    }
}
