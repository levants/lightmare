package org.lightmare.criteria.query.internal.connectors;

/**
 * Provider for query layer
 * 
 * @author Levan Tsinadze
 *
 */
public interface LayerProvider {

    <T> QueryLayer<T> query(String sql, Class<T> type);

    QueryLayer<?> query(String sql);

    void close();
}
