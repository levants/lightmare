package org.lightmare.criteria.query.internal.connectors;

import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Provider for query layer
 * 
 * @author Levan Tsinadze
 *
 */
public interface LayerProvider {

    <T> QueryLayer<T> query(String sql, Class<T> type);

    QueryLayer<?> query(String sql);

    String getTableName(Class<?> type);

    String getColumnName(QueryTuple tuple);

    String getSelectType(String alias);

    String getCountType(String alias);

    void close();
}
