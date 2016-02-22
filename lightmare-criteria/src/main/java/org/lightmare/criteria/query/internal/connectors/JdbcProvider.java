package org.lightmare.criteria.query.internal.connectors;

import java.sql.Connection;
import java.sql.SQLException;

import org.lightmare.criteria.annotations.DBTable;
import org.lightmare.criteria.config.Configuration.ColumnResolver;
import org.lightmare.criteria.config.CriteriaConfiguration.DefaultResolver;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Implementation for
 * {@link org.lightmare.criteria.query.internal.connectors.LayerProvider} JDBC
 * queries
 * 
 * @author Levan Tsinadze
 *
 */
public class JdbcProvider implements LayerProvider {

    private final Connection connection;

    private final ColumnResolver resolver;

    private static final String SELECT_TYPE = ".*";

    private static final String COUNT_TYPE = "*";

    public JdbcProvider(final Connection connection, final ColumnResolver resolver) {
        this.connection = connection;
        this.resolver = resolver;
    }

    public JdbcProvider(final Connection connection) {
        this(connection, new DefaultResolver());
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
    public String getTableName(Class<?> type) {
        return ObjectUtils.ifNull(() -> type.getAnnotation(DBTable.class), c -> type.getName(),
                c -> StringUtils.thisOrDefault(c.value(), type::getName));
    }

    @Override
    public String getSelectType(String alias) {
        return StringUtils.concat(alias, SELECT_TYPE);
    }

    @Override
    public String getCountType(String alias) {
        return StringUtils.concat(alias, COUNT_TYPE);
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
