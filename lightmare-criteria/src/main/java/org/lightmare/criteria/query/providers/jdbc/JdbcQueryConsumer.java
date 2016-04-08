package org.lightmare.criteria.query.providers.jdbc;

import org.lightmare.criteria.functions.QueryConsumer;

/**
 * Implementation of {@link org.lightmare.criteria.functions.QueryConsumer} for
 * JDBC queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
@FunctionalInterface
public interface JdbcQueryConsumer<T> extends QueryConsumer<T, JdbcQueryStream<T>> {

    @Override
    void accept(JdbcQueryStream<T> stream);
}
