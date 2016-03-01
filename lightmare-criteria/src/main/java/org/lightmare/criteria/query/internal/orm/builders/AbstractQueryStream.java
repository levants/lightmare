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

import org.lightmare.criteria.query.internal.layers.JpaJdbcQueryLayer;
import org.lightmare.criteria.query.internal.layers.LayerProvider;
import org.lightmare.criteria.query.internal.layers.QueryLayer;
import org.lightmare.criteria.tuples.AggregateTuple;
import org.lightmare.criteria.utils.ObjectUtils;

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
     * @return {@link org.lightmare.criteria.query.internal.layers.QueryLayer}
     *         for entity type
     */
    protected QueryLayer<T> initTypedQuery() {

        JpaJdbcQueryLayer<T> query;

        String sqlText = sql();

        QueryLayer<T> raw = provider.query(sqlText, entityType);
        query = ObjectUtils.cast(raw);
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

        JpaJdbcQueryLayer<Long> query;

        String sqlText = countSql();
        QueryLayer<Long> raw = provider.query(sqlText, Long.class);
        query = ObjectUtils.cast(raw);
        setParameters(query);

        return query;
    }

    /**
     * Creates {@link javax.persistence.Query} from generated SQL for UPDATE or
     * DELETE statements
     * 
     * @return {@link org.lightmare.criteria.query.internal.layers.QueryLayer}
     *         for bulk modification
     */
    protected QueryLayer<?> initBulkQuery() {

        JpaJdbcQueryLayer<?> query;

        String sqlText = sql();
        QueryLayer<?> raw = provider.query(sqlText);
        query = ObjectUtils.cast(raw);
        setParameters(query);

        return query;
    }
}
