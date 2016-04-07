package org.lightmare.criteria.query.providers.jdbc;

import java.util.List;

import org.lightmare.criteria.config.Configuration.ResultRetriever;
import org.lightmare.criteria.query.internal.orm.subqueries.AbstractSubQueryStream;
import org.lightmare.criteria.query.internal.orm.subqueries.SubSelectStream;

/**
 * Query builder for JDBC sub query SELECT expressions
 * 
 * @author Levan Tsinadze
 *
 * @param <E>
 *            sub select type parameter
 * @param <T>
 *            entity type parameter
 */
public class JdbcSubSelectStream<T, E> extends SubSelectStream<T, E, JdbcQueryStream<E>, JdbcQueryStream<Object[]>>
        implements JdbcQueryStream<E> {

    protected JdbcSubSelectStream(AbstractSubQueryStream<T, ?, ?, ?> stream, Class<E> type) {
        super(stream, type);
    }

    @Override
    public E get(ResultRetriever<E> retriever) {
        return null;
    }

    @Override
    public List<E> toList(ResultRetriever<E> retriever) {
        return null;
    }
}
