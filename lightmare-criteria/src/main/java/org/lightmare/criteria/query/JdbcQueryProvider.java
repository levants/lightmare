package org.lightmare.criteria.query;

import java.sql.Connection;

/**
 * Query provider for JDBC layer
 * 
 * @author Levan Tsinadze
 *
 */
public abstract class JdbcQueryProvider {

    /**
     * Generates DELETE statements with custom alias
     * 
     * @param connection
     * @param entityType
     * @param entityAlias
     * @return {@link org.lightmare.criteria.query.QueryStream} with select
     *         statement
     */
    public static <T> QueryStream<T> delete(final Connection connection, final Class<T> entityType,
            final String entityAlias) {
        return JdbcQueryStreamBuilder.delete(connection, entityType, entityAlias);
    }

    /**
     * Generates DELETE statements with default alias
     * 
     * @param connection
     * @param entityType
     * @return {@link org.lightmare.criteria.query.QueryStream} with select
     *         statement
     */
    public static <T> QueryStream<T> delete(final Connection connection, Class<T> entityType) {
        return JdbcQueryStreamBuilder.delete(connection, entityType, QueryStream.DEFAULT_ALIAS);
    }

    /**
     * Generates UPDATE statements with custom alias
     * 
     * @param connection
     * @param entityType
     * @param entityAlias
     * @return {@link org.lightmare.criteria.query.QueryStream} with select
     *         statement
     */
    public static <T> QueryStream<T> update(final Connection connection, final Class<T> entityType,
            final String entityAlias) {
        return JdbcQueryStreamBuilder.update(connection, entityType, entityAlias);
    }

    /**
     * Generates UPDATE statements with default alias
     * 
     * @param connection
     * @param entityType
     * @return {@link org.lightmare.criteria.query.QueryStream} with select
     *         statement
     */
    public static <T> QueryStream<T> update(final Connection connection, Class<T> entityType) {
        return JdbcQueryStreamBuilder.update(connection, entityType, QueryStream.DEFAULT_ALIAS);
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
        return JdbcQueryStreamBuilder.query(connection, entityType, entityAlias);
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
        return JdbcQueryStreamBuilder.query(connection, entityType, QueryStream.DEFAULT_ALIAS);
    }
}
