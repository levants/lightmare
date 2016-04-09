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
package org.lightmare.criteria.query.providers.jdbc;

import java.util.List;
import java.util.function.BiFunction;

import org.lightmare.criteria.config.Configuration.ResultRetriever;
import org.lightmare.criteria.query.orm.builders.AbstractQueryStream;
import org.lightmare.criteria.query.orm.builders.SelectStream;
import org.lightmare.criteria.query.providers.jdbc.layers.JdbcQueryLayer;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Query builder for JDBC SELECT expressions
 * 
 * @author Levan Tsinadze
 *
 * @param <E>
 *            select type parameter
 * @param <T>
 *            entity type parameter
 */
class JdbcSelectStream<E, T> extends SelectStream<T, E, JdbcQueryStream<E>, JdbcQueryStream<Object[]>>
        implements JdbcQueryStream<E> {

    protected JdbcSelectStream(AbstractQueryStream<T, ?, ?> stream, Class<E> type) {
        super(stream, type);
    }

    /**
     * Retrieves result from generated
     * {@link org.lightmare.criteria.query.providers.jdbc.layers.JdbcQueryLayer}
     * instance
     * 
     * @param retriever
     * @param function
     * @return R result from generated
     *         {@link org.lightmare.criteria.query.providers.jdbc.layers.JdbcQueryLayer}
     */
    private <R> R retrieveResult(ResultRetriever<E> retriever,
            BiFunction<JdbcQueryLayer<E>, ResultRetriever<E>, R> function) {

        R result;

        JdbcQueryLayer<E> jdbcQuery = ObjectUtils.getAndCast(this::initTypedQuery);
        result = function.apply(jdbcQuery, retriever);

        return result;
    }

    @Override
    public E get(ResultRetriever<E> retriever) {
        return retrieveResult(retriever, JdbcQueryLayer::get);
    }

    @Override
    public List<E> toList(ResultRetriever<E> retriever) {
        return retrieveResult(retriever, JdbcQueryLayer::toList);
    }
}
