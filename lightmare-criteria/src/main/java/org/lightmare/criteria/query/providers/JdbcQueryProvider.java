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
package org.lightmare.criteria.query.providers;

import java.sql.Connection;

import org.lightmare.criteria.query.QueryProvider;
import org.lightmare.criteria.query.internal.layers.JdbcProvider;

/**
 * Query provider for JDBC layer
 * 
 * @author Levan Tsinadze
 *
 */
public abstract class JdbcQueryProvider {

    /**
     * Generates DELETE statements
     * 
     * @param connection
     * @param entityType
     * @return {@link org.lightmare.criteria.query.providers.JdbcQueryStream}
     *         with delete statement
     */
    public static <T> JdbcQueryStream<T> delete(final Connection connection, Class<T> entityType) {
        return QueryProvider.delete(new JdbcProvider(connection), entityType, JdbcQueryStreamBuilder::delete);
    }

    /**
     * Generates UPDATE statements
     * 
     * @param connection
     * @param entityType
     * @return {@link org.lightmare.criteria.query.providers.JdbcQueryStream}
     *         with update statement
     */
    public static <T> JdbcQueryStream<T> update(final Connection connection, Class<T> entityType) {
        return QueryProvider.update(new JdbcProvider(connection), entityType, JdbcQueryStreamBuilder::update);
    }

    /**
     * Generates SELECT statements
     * 
     * @param connection
     * @param entityType
     * @return {@link org.lightmare.criteria.query.providers.JdbcQueryStream}
     *         with select statement
     */
    public static <T> JdbcQueryStream<T> select(final Connection connection, Class<T> entityType) {
        return QueryProvider.select(new JdbcProvider(connection), entityType, JdbcQueryStreamBuilder::select);
    }
}
