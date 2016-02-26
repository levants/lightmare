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

import org.lightmare.criteria.query.internal.EntityQueryStream;
import org.lightmare.criteria.query.internal.connectors.JdbcProvider;
import org.lightmare.criteria.query.internal.connectors.LayerProvider;
import org.lightmare.criteria.query.internal.orm.links.Clauses;

/**
 * Main class for lambda expression analyze and JDBC query build and run
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 */
public class JdbcQueryStreamBuilder<T> extends EntityQueryStream<T> {

    protected JdbcQueryStreamBuilder(LayerProvider provider, Class<T> entityType, String alias) {
        super(provider, entityType, alias);
    }

    /**
     * Generates DELETE statement with custom alias
     * 
     * @param em
     * @param entityType
     * @param alias
     * @return {@link org.lightmare.criteria.query.providers.JdbcQueryStreamBuilder} with
     *         delete statement
     */
    protected static <T> JdbcQueryStreamBuilder<T> delete(final Connection connection, final Class<T> entityType,
            final String alias) {

        JdbcQueryStreamBuilder<T> stream;

        final LayerProvider provider = new JdbcProvider(connection);
        stream = new JdbcQueryStreamBuilder<>(provider, entityType, alias);
        stream.appendPrefix(Clauses.DELETE);
        stream.appendEntityPart();

        return stream;
    }

    /**
     * Generates DELETE statement with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link org.lightmare.criteria.query.providers.JdbcQueryStreamBuilder} with
     *         delete statement
     */
    protected static <T> JdbcQueryStreamBuilder<T> delete(final Connection connection, Class<T> entityType) {
        return delete(connection, entityType, DEFAULT_ALIAS);
    }

    /**
     * Generates UPDATE statement with custom alias
     * 
     * @param em
     * @param entityType
     * @param alias
     * @return {@link org.lightmare.criteria.query.providers.JdbcQueryStreamBuilder} with
     *         update statement
     */
    protected static <T> JdbcQueryStreamBuilder<T> update(final Connection connection, final Class<T> entityType,
            final String alias) {

        JdbcQueryStreamBuilder<T> stream;

        final LayerProvider provider = new JdbcProvider(connection);
        stream = new JdbcQueryStreamBuilder<>(provider, entityType, alias);
        stream.appendPrefix(Clauses.UPDATE);
        stream.appendEntityPart();

        return stream;
    }

    /**
     * Generates UPDATE statement with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link org.lightmare.criteria.query.providers.JdbcQueryStreamBuilder} with
     *         update statement
     */
    protected static <T> JdbcQueryStreamBuilder<T> update(final Connection connection, Class<T> entityType) {
        return update(connection, entityType, DEFAULT_ALIAS);
    }

    /**
     * Generates SELECT statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param alias
     * @return {@link org.lightmare.criteria.query.providers.JdbcQueryStreamBuilder} with
     *         select statement
     */
    protected static <T> JdbcQueryStreamBuilder<T> select(final Connection connection, final Class<T> entityType,
            final String alias) {

        JdbcQueryStreamBuilder<T> stream;

        final LayerProvider provider = new JdbcProvider(connection);
        stream = new JdbcQueryStreamBuilder<>(provider, entityType, alias);
        stream.startsSelect();

        return stream;
    }

    /**
     * Generates SELECT statement with default alias
     * 
     * @param connection
     * @param entityType
     * @return {@link org.lightmare.criteria.query.providers.JdbcQueryStreamBuilder} with
     *         select statement
     */
    protected static <T> JdbcQueryStreamBuilder<T> select(final Connection connection, Class<T> entityType) {
        return select(connection, entityType, DEFAULT_ALIAS);
    }
}
