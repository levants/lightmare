package org.lightmare.criteria.query.internal.connectors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.TemporalType;

import org.lightmare.criteria.config.Configuration.ResultRetriever;
import org.lightmare.criteria.config.DefaultConfiguration.DefaultRetriever;
import org.lightmare.criteria.tuples.ParameterTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Implementation for JDBC layer
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            result type parameter
 */
public class JdbcQueryLayer<T> implements QueryLayer<T> {

    private final Class<T> type;

    private final String sql;

    private final Connection connection;

    private PreparedStatement statement;

    private Map<Integer, ParameterTuple> parameters = new TreeMap<>();

    // Parameter sign
    private static final String NATURAL_PARAM = "?";

    private static final int NON_EXISTING = -1;

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

    protected JdbcQueryLayer(final Connection connection, String sql, Class<T> type) {
        this.type = type;
        this.sql = sql;
        this.connection = connection;
    }

    private void replace(String name, StringBuilder builder) {

        int size = name.length();
        int index = builder.indexOf(name);
        if (ObjectUtils.notEquals(index, NON_EXISTING)) {
            int end = index + size;
            builder.replace(index, end, NATURAL_PARAM);
        }
    }

    private void replaceParameters() {
        StringBuilder builder = new StringBuilder(sql);
        parameters.values().forEach(c -> replace(c.getName(), builder));
    }

    private static void putParameter(Integer key, ParameterTuple parameter, PreparedStatement statement) {

        try {
            statement.setObject(key, parameter.getValue());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private PreparedStatement call(String sql, JdbcFunction function) {

        PreparedStatement result;

        try {
            replaceParameters();
            result = function.apply(sql);
            parameters.forEach((k, v) -> putParameter(k, v, result));
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

    /**
     * Gets result from result set
     * 
     * @param retriever
     * @return T result
     */
    public T get(ResultRetriever retriever) {
        return call(() -> {

            T result;
            statement = call(sql, connection::prepareStatement);
            ResultSet rs = statement.getResultSet();
            if (rs.next()) {
                result = retriever.readRow(rs, type);
            } else {
                result = null;
            }

            return result;
        });
    }

    /**
     * Gets {@link java.util.List} of result
     * 
     * @param retriever
     * @return {@link java.util.List} of result
     */
    public List<T> toList(ResultRetriever retriever) {
        return call(() -> {

            List<T> results = new ArrayList<>();

            statement = call(sql, connection::prepareStatement);
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
    public List<T> toList() {

        List<T> results;

        ResultRetriever retriever = new DefaultRetriever();
        results = toList(retriever);

        return results;
    }

    @Override
    public T get() {

        T result;

        ResultRetriever retriever = new DefaultRetriever();
        result = get(retriever);

        return result;
    }

    @Override
    public int execute() {
        return call(statement::getUpdateCount);
    }

    @Override
    public void setParameter(String name, Object value) {
    }

    @Override
    public void setParameter(String name, Calendar value, TemporalType temporalType) {
    }

    @Override
    public void setParameter(String name, Date value, TemporalType temporalType) {
    }

    @Override
    public void setParameter(ParameterTuple tuple) {
        parameters.put(tuple.getCount(), tuple);
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
}
