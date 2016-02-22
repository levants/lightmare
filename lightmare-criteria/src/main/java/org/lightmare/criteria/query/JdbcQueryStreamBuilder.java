package org.lightmare.criteria.query;

import java.sql.Connection;

import org.lightmare.criteria.query.internal.JdbcEntityQueryStream;
import org.lightmare.criteria.query.internal.connectors.JdbcProvider;
import org.lightmare.criteria.query.internal.connectors.LayerProvider;
import org.lightmare.criteria.query.internal.jpa.links.Clauses;

/**
 * Main class for lambda expression analyze and JDBC query build and run
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 */
public class JdbcQueryStreamBuilder<T> extends JdbcEntityQueryStream<T> {

    protected JdbcQueryStreamBuilder(LayerProvider provider, Class<T> entityType, String alias) {
        super(provider, entityType, alias);
    }

    /**
     * Generates DELETE statement with custom alias
     * 
     * @param em
     * @param entityType
     * @param alias
     * @return {@link org.lightmare.criteria.query.JdbcQueryStreamBuilder} with
     *         select statement
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
     * @return {@link org.lightmare.criteria.query.JdbcQueryStreamBuilder} with
     *         select statement
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
     * @return {@link org.lightmare.criteria.query.JdbcQueryStreamBuilder} with
     *         select statement
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
     * @return {@link org.lightmare.criteria.query.JdbcQueryStreamBuilder} with
     *         select statement
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
     * @return {@link org.lightmare.criteria.query.JdbcQueryStreamBuilder} with
     *         select statement
     */
    protected static <T> JdbcQueryStreamBuilder<T> query(final Connection connection, final Class<T> entityType,
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
     * @return {@link org.lightmare.criteria.query.JdbcQueryStreamBuilder} with
     *         select statement
     */
    protected static <T> JdbcQueryStreamBuilder<T> query(final Connection connection, Class<T> entityType) {
        return query(connection, entityType, DEFAULT_ALIAS);
    }
}
