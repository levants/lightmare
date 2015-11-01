package org.lightmare.criteria.lambda;

import java.io.IOException;
import java.io.Serializable;

import org.lightmare.criteria.query.SubQueryStream;

/**
 * Functional interface for sub query generation
 * 
 * @author Levan Tsinadze
 *
 * @param <S>
 *            sub query entity type
 * @param <T>
 *            entity type
 */
@FunctionalInterface
public interface SubQuery<S extends Serializable, T extends Serializable> extends Serializable {

    void accept(SubQueryStream<S, T> subQuery) throws IOException;
}
