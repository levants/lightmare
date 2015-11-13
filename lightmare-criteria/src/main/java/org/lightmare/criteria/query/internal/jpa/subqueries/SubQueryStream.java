package org.lightmare.criteria.query.internal.jpa.subqueries;

import org.lightmare.criteria.query.QueryStream;

/**
 * Inretface to process sub queries, embedded queries and join queries
 * 
 * @author Levan Tsinadze
 *
 * @param <S>
 *            entity type parameter
 * @param <T>
 *            parent entity type parameter
 */
public interface SubQueryStream<S, T> extends QueryStream<S> {

    void call();
}
