package org.lightmare.criteria.query.internal.connectors;

import java.sql.Connection;
import java.sql.SQLException;

import org.lightmare.criteria.config.Configuration.ColumnResolver;

/**
 * JDBC query layer provider
 * 
 * @author Levan Tsinadze
 *
 */
public class JdbcProvider implements LayerProvider {

    private final Connection connection;

    private final ColumnResolver resolver;

    public JdbcProvider(final Connection connection, final ColumnResolver resolver) {
        this.connection = connection;
        this.resolver = resolver;
    }

    @Override
    public <T> QueryLayer<T> query(String sql, Class<T> type) {
        return new JdbcQueryLayer<T>(connection, sql, type);
    }

    @Override
    public QueryLayer<?> query(String sql) {
        return new JdbcQueryLayer<Void>(connection, sql, Void.class);
    }

    public ColumnResolver getResolver() {
        return resolver;
    }

    @Override
    public void close() {

        try {
            connection.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
