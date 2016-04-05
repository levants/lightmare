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

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import org.lightmare.criteria.config.Configuration.ResultRetriever;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.layers.JdbcQueryLayer;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.query.layers.QueryLayer;
import org.lightmare.criteria.tuples.ParameterTuple;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Abstract class for generated JPA query result
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 * 
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 * @param <O>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
abstract class AbstractResultStream<T, Q extends QueryStream<T, ? super Q>, O extends QueryStream<Object[], ?>>
        extends AbstractJoinStream<T, Q, O> {

    protected AbstractResultStream(final LayerProvider provider, final Class<T> entityType) {
        super(provider, entityType);
    }

    @Override
    public Long count() {

        Long result;

        QueryLayer<Long> query = initCountQuery();
        result = query.get();

        return result;
    }

    @Override
    public List<T> toList() {

        List<T> results;

        QueryLayer<T> query = initTypedQuery();
        results = query.toList();

        return results;
    }

    @Override
    public T get() {

        T result;

        QueryLayer<T> query = initTypedQuery();
        result = query.get();

        return result;
    }

    /**
     * Retrieves result from generated
     * {@link org.lightmare.criteria.query.internal.layers.JdbcQueryLayer}
     * instance
     * 
     * @param retriever
     * @param function
     * @return R result from generated
     *         {@link org.lightmare.criteria.query.internal.layers.JdbcQueryLayer}
     */
    private <R> R retrieveResult(ResultRetriever<T> retriever,
            BiFunction<JdbcQueryLayer<T>, ResultRetriever<T>, R> function) {

        R result;

        JdbcQueryLayer<T> jdbcQuery = ObjectUtils.getAndCast(this::initTypedQuery);
        result = function.apply(jdbcQuery, retriever);

        return result;
    }

    @Override
    public T get(ResultRetriever<T> retriever) {
        return retrieveResult(retriever, JdbcQueryLayer::get);
    }

    @Override
    public List<T> toList(ResultRetriever<T> retriever) {
        return retrieveResult(retriever, JdbcQueryLayer::toList);
    }

    @Override
    public int execute() {

        int result;

        QueryLayer<?> query = initBulkQuery();
        result = query.execute();

        return result;
    }

    @Override
    public Set<ParameterTuple> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return sql();
    }
}
