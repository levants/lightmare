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
package org.lightmare.criteria.query;

import java.sql.Connection;

/**
 * Query provider for JDBC layer
 * 
 * @author Levan Tsinadze
 *
 */
public abstract class JdbcQueryProvider extends QueryProvider {

    /**
     * Generates DELETE statements with custom alias
     * 
     * @param connection
     * @param entityType
     * @param entityAlias
     * @return {@link org.lightmare.criteria.query.QueryStream} with delete
     *         statement
     */
    public static <T> QueryStream<T> delete(final Connection connection, final Class<T> entityType,
            final String entityAlias) {
        return delete(() -> JdbcQueryStreamBuilder.delete(connection, entityType, entityAlias));
    }

    /**
     * Generates DELETE statements with default alias
     * 
     * @param connection
     * @param entityType
     * @return {@link org.lightmare.criteria.query.QueryStream} with delete
     *         statement
     */
    public static <T> QueryStream<T> delete(final Connection connection, Class<T> entityType) {
        return delete(() -> JdbcQueryStreamBuilder.delete(connection, entityType, QueryStream.DEFAULT_ALIAS));
    }

    /**
     * Generates UPDATE statements with custom alias
     * 
     * @param connection
     * @param entityType
     * @param entityAlias
     * @return {@link org.lightmare.criteria.query.QueryStream} with update
     *         statement
     */
    public static <T> QueryStream<T> update(final Connection connection, final Class<T> entityType,
            final String entityAlias) {
        return update(() -> JdbcQueryStreamBuilder.update(connection, entityType, entityAlias));
    }

    /**
     * Generates UPDATE statements with default alias
     * 
     * @param connection
     * @param entityType
     * @return {@link org.lightmare.criteria.query.QueryStream} with update
     *         statement
     */
    public static <T> QueryStream<T> update(final Connection connection, Class<T> entityType) {
        return update(() -> JdbcQueryStreamBuilder.update(connection, entityType, QueryStream.DEFAULT_ALIAS));
    }

    /**
     * Generates SELECT statements with custom alias
     * 
     * @param connection
     * @param entityType
     * @param entityAlias
     * @return {@link org.lightmare.criteria.query.QueryStream} with select
     *         statement
     */
    public static <T> QueryStream<T> select(final Connection connection, final Class<T> entityType,
            final String entityAlias) {
        return select(() -> JdbcQueryStreamBuilder.select(connection, entityType, entityAlias));
    }

    /**
     * Generates SELECT statements with default alias
     * 
     * @param connection
     * @param entityType
     * @return {@link org.lightmare.criteria.query.QueryStream} with select
     *         statement
     */
    public static <T> QueryStream<T> select(final Connection connection, Class<T> entityType) {
        return select(() -> JdbcQueryStreamBuilder.select(connection, entityType, QueryStream.DEFAULT_ALIAS));
    }
}
