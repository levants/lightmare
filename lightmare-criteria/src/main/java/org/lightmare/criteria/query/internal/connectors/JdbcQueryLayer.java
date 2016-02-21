package org.lightmare.criteria.query.internal.connectors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;

import org.lightmare.criteria.config.Configuration.ResultRetriever;
import org.lightmare.criteria.utils.CollectionUtils;

/**
 * Implementation for JDBC layer
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            result type parameter
 */
public class JdbcQueryLayer<T> implements QueryLayer<T> {

    private final Connection connection;

    private final ResultRetriever retriever;

    private Class<T> type;

    private PreparedStatement statement;

    /**
     * Functional interface for JDBC method calls
     * 
     * @author Levan Tsinadze
     *
     */
    @FunctionalInterface
    private static interface JdbcFunction {

        PreparedStatement apply(String sql) throws SQLException;

    }

    @FunctionalInterface
    private static interface JdbcSupplier<R> {

        R supply() throws SQLException;

    }

    @FunctionalInterface
    private static interface JdbcConsumer<T> {

        void accept(T t) throws SQLException;

    }

    private JdbcQueryLayer(final Connection connection, final ResultRetriever retriever) {
        this.connection = connection;
        this.retriever = retriever;
    }

    private PreparedStatement call(String sql, JdbcFunction function) {

        PreparedStatement result;

        try {
            result = function.apply(sql);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }

    private <P, R> R call(JdbcSupplier<R> supplier) {

        R result;

        try {
            result = supplier.supply();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }

    private <P> void consume(P value, JdbcConsumer<P> consumer) {

        try {
            consumer.accept(value);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public QueryLayer<T> select(Class<T> type, String sql) {

        this.type = type;
        statement = call(sql, connection::prepareStatement);

        return this;
    }

    @Override
    public QueryLayer<T> update(Class<T> type, String sql) {

        this.type = type;
        statement = call(sql, connection::prepareStatement);

        return this;
    }

    @Override
    public QueryLayer<T> delete(Class<T> type, String sql) {

        this.type = type;
        statement = call(sql, connection::prepareStatement);

        return this;
    }

    @Override
    public List<T> toList() {
        return call(() -> {

            List<T> results = new ArrayList<>();

            ResultSet rs = statement.getResultSet();
            T result;
            while (rs.next()) {
                result = retriever.readRow(rs, type);
                results.add(result);
            }

            return results;
        });
    }

    @Override
    public T get() {
        return call(() -> {

            T result;

            ResultSet rs = statement.getResultSet();
            if (rs.next()) {
                result = retriever.readRow(rs, type);
            } else {
                result = null;
            }

            return result;
        });
    }

    @Override
    public int execute() {
        return call(statement::getUpdateCount);
    }

    @Override
    public void setMaxResults(int maxResult) {
        consume(maxResult, statement::setMaxRows);
    }

    @Override
    public int getMaxResults() {
        return call(statement::getMaxRows);
    }

    @Override
    public void setFirstResult(int startPosition) {
    }

    @Override
    public int getFirstResult() {
        return CollectionUtils.EMPTY;
    }

    @Override
    public void setHint(String hintName, Object value) {
    }

    @Override
    public Map<String, Object> getHints() {
        return Collections.emptyMap();
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
    }

    @Override
    public void setLockMode(LockModeType lockMode) {
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
