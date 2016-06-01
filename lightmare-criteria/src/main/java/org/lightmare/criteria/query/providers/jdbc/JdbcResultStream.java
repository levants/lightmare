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

import org.lightmare.criteria.config.Configuration.ResultRetriever;
import org.lightmare.criteria.query.orm.ResultStream;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Implementation of {@link org.lightmare.criteria.query.orm.ResultStream} for
 * JDBC queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface JdbcResultStream<T> extends ResultStream<T> {

    /**
     * Gets {@link java.sql.ResultSet} and retrieves single result
     * <p>
     * Note: Only for direct JDBC streams
     * <p/>
     * 
     * @param retriever
     * 
     * @return T single query result
     * @see java.sql.ResultSet
     * @see org.lightmare.criteria.config.Configuration.ResultRetriever
     */
    T get(ResultRetriever<T> retriever);

    /**
     * Gets {@link java.sql.ResultSet} and retrieves {@link java.util.List} of
     * result
     * <p>
     * Note: Only for direct JDBC streams
     * <p/>
     * 
     * @param retriever
     * @return {@link java.util.List} of query results
     * @see java.sql.ResultSet
     * @see org.lightmare.criteria.config.Configuration.ResultRetriever
     */
    List<T> toList(ResultRetriever<T> retriever);

    /**
     * Gets {@link java.sql.ResultSet} and retrieves first result or if it is
     * <code>null</code> then default value
     * <p>
     * Note: Only for direct JDBC streams
     * <p/>
     * 
     * @param retriever
     * @param defaultValue
     * @return T query first result
     * @see java.sql.ResultSet
     * @see org.lightmare.criteria.config.Configuration.ResultRetriever
     */
    default T firstOrDefault(ResultRetriever<T> retriever, T defaultValue) {
        return ObjectUtils.callOrInit(retriever, this::get, c -> defaultValue);
    }

    /**
     * Gets {@link java.sql.ResultSet} and retrieves first result or
     * <code>null</code> if no element was found
     * <p>
     * Note: Only for direct JDBC streams
     * <p/>
     * 
     * @param retriever
     * @return T query first result
     * @see java.sql.ResultSet
     * @see org.lightmare.criteria.config.Configuration.ResultRetriever
     */
    default T getFirst(ResultRetriever<T> retriever) {
        return firstOrDefault(retriever, null);
    }
}
