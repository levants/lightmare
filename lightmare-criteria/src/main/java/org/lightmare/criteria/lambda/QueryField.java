package org.lightmare.criteria.lambda;

import java.io.IOException;
import java.io.Serializable;

import org.lightmare.criteria.query.QueryStream;

/**
 * Query parts generator
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type
 */
@FunctionalInterface
public interface QueryField<T extends Serializable> {

    void accept(QueryStream<T> stream) throws IOException;
}
