package org.lightmare.criteria.query.internal.connectors;

import java.util.List;

/**
 * Database abstract layer
 * 
 * @author Levan Tsinadze
 *
 */
public interface QueryLayer {

    <T> QueryLayer select(Class<T> type, String query);

    <T> QueryLayer update(Class<T> type, String query);

    <T> QueryLayer delete(Class<T> type, String query);

    <T> List<T> toList();

    <T> T get();

    int execute();
}
