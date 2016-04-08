package org.lightmare.criteria.query.providers.jpa;

import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.providers.JpaQueryStream;

/**
 * Implementation of {@link org.lightmare.criteria.functions.QueryConsumer} for
 * JPA queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
@FunctionalInterface
public interface JpaQueryConsumer<T> extends QueryConsumer<T, JpaQueryStream<T>> {

    @Override
    void accept(JpaQueryStream<T> stream);
}
