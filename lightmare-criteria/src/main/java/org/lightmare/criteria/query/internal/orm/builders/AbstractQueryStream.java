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

import java.util.Queue;
import java.util.Set;

import org.lightmare.criteria.query.internal.connectors.LayerProvider;
import org.lightmare.criteria.query.internal.connectors.QueryLayer;
import org.lightmare.criteria.tuples.AggregateTuple;

/**
 * Abstract class for lambda expression analyze and JPA query generator
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 */
public abstract class AbstractQueryStream<T> extends AbstractAppenderStream<T> {

    protected AbstractQueryStream(final LayerProvider provider, final Class<T> entityType) {
        super(provider, entityType);
    }

    /**
     * Gets {@link java.util.Set} of
     * {@link org.lightmare.criteria.tuples.AggregateTuple} for GROUP BY
     * processing
     * 
     * @return {@link java.util.Set} of
     *         {@link org.lightmare.criteria.tuples.AggregateTuple}
     */
    protected abstract Set<AggregateTuple> getAggregateFields();

    /**
     * Gets {@link java.util.Queue} of
     * {@link org.lightmare.criteria.tuples.AggregateTuple} for HAVING
     * processing
     * 
     * @return {@link java.util.Queue} of
     *         {@link org.lightmare.criteria.tuples.AggregateTuple}
     */
    protected abstract Queue<AggregateTuple> getAggregateQueue();

    /**
     * Creates {@link javax.persistence.TypedQuery} from generated SQL for
     * SELECT statements
     * 
     * @return {@link org.lightmare.criteria.query.internal.connectors.QueryLayer}
     *         for entity type
     */
    protected QueryLayer<T> initTypedQuery() {

        QueryLayer<T> query;

        String sqlText = sql();
        query = provider.query(sqlText, entityType);
        setParameters(query);

        return query;
    }

    /**
     * Generates {@link javax.persistence.TypedQuery} for COUNT JPA-QL statement
     * 
     * @return {@link org.lightmare.criteria.query.internal.connectors.e.TypedQuery}
     *         with {@link Long} type for element count
     */
    protected QueryLayer<Long> initCountQuery() {

        QueryLayer<Long> query;

        String sqlText = countSql();
        query = provider.query(sqlText, Long.class);
        setParameters(query);

        return query;
    }

    /**
     * Creates {@link javax.persistence.Query} from generated SQL for UPDATE or
     * DELETE statements
     * 
     * @return {@link org.lightmare.criteria.query.internal.connectors.QueryLayer}
     *         for bulk modification
     */
    protected QueryLayer<?> initBulkQuery() {

        QueryLayer<?> query;

        String sqlText = sql();
        query = provider.query(sqlText);
        setParameters(query);

        return query;
    }
}
