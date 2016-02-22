package org.lightmare.criteria.query.internal;

import java.util.List;

import org.lightmare.criteria.config.Configuration.ResultRetriever;
import org.lightmare.criteria.query.QueryStream;

/**
 * Query stream for direct JDBC expressions
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface JdbcQueryStream<T> extends QueryStream<T> {

    T get(ResultRetriever retriever);

    List<T> toList(ResultRetriever retriever);
}
