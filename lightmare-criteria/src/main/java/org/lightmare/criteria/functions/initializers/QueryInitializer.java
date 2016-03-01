package org.lightmare.criteria.functions.initializers;

import java.io.Serializable;
import java.util.function.BiFunction;

import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.layers.LayerProvider;

/**
 * Functional interface to initialize
 * {@link org.lightmare.criteria.query.QueryStream} implementation
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type
 * @param <S>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 */
@FunctionalInterface
public interface QueryInitializer<T, S extends QueryStream<T, ? super S>>
        extends BiFunction<LayerProvider, Class<T>, S>, Serializable {

    /**
     * Initializes {@link org.lightmare.criteria.query.QueryStream}
     * implementation by
     * {@link org.lightmare.criteria.query.internal.layers.LayerProvider} and
     * {@link Class} entity type
     * 
     * @return S {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    S apply(LayerProvider provider, Class<T> type);
}
