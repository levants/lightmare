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
package org.lightmare.criteria.query.internal.layers;

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
import org.lightmare.criteria.query.internal.orm.links.Parts;
import org.lightmare.criteria.tuples.ParameterTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Implementation for JDBC layer
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            result type parameter
 */
public class JdbcQueryLayer<T> implements JpaJdbcQueryLayer<T> {

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

    /**
     * Supplier for data retrieve
     * 
     * @author Levan Tsinadze
     *
     * @param <R>
     *            result type
     */
    @FunctionalInterface
    private static interface JdbcSupplier<R> {

        R supply() throws SQLException;
    }

    /**
     * Parameter setter for {@link java.sql.ResultSet} by field type
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            argument type parameter
     */
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

        String paramName = StringUtils.concat(Parts.PARAM_PREFIX, name);
        int size = paramName.length();
        int index = builder.indexOf(paramName);
        if (ObjectUtils.notEquals(index, NON_EXISTING)) {
            int end = index + size;
            builder.replace(index, end, NATURAL_PARAM);
        }
    }

    private String replaceParameters() {

        String refined;

        StringBuilder builder = new StringBuilder(sql);
        parameters.values().forEach(c -> replace(c.getName(), builder));
        refined = builder.toString();

        return refined;
    }

    private static void putParameter(Integer key, ParameterTuple parameter, PreparedStatement statement) {
        Object value = parameter.getValue();
        ObjectUtils.acceptWrap(key, value, statement::setObject);
    }

    private PreparedStatement call(JdbcFunction function) {
        return ObjectUtils.applyWrap(function, c -> {

            PreparedStatement result;

            String refined = replaceParameters();
            result = c.apply(refined);
            parameters.forEach((k, v) -> putParameter(k, v, result));

            return result;
        });
    }

    private <P, R> R call(JdbcSupplier<R> supplier) {
        return ObjectUtils.applyWrap(supplier, JdbcSupplier::supply);
    }

    private <P> void consume(P value, JdbcConsumer<P> consumer) {
        ObjectUtils.acceptWrap(consumer, value, JdbcConsumer::accept);
    }

    private ResultSet executeQuery() throws SQLException {

        ResultSet rs;

        statement = call(connection::prepareStatement);
        rs = statement.executeQuery();

        return rs;
    }

    /**
     * Gets result from result set
     * 
     * @param retriever
     * @return T result
     */
    public T get(ResultRetriever<T> retriever) {
        return call(() -> {

            T result;

            ResultSet rs = executeQuery();
            if (rs.next()) {
                result = retriever.readRow(rs);
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
    public List<T> toList(ResultRetriever<T> retriever) {
        return call(() -> {

            List<T> results = new ArrayList<>();

            ResultSet rs = executeQuery();
            T result;
            while (rs.next()) {
                result = retriever.readRow(rs);
                results.add(result);
            }

            return results;
        });
    }

    @Override
    public List<T> toList() {

        List<T> results;

        ResultRetriever<T> retriever = new DefaultRetriever<>(type);
        results = toList(retriever);

        return results;
    }

    @Override
    public T get() {

        T result;

        ResultRetriever<T> retriever = new DefaultRetriever<>(type);
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
