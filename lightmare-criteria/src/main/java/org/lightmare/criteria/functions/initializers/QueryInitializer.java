package org.lightmare.criteria.functions.initializers;

import java.io.Serializable;
import java.util.function.BiFunction;

import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.query.layers.LayerProvider;

/**
 * Functional interface to initialize
 * {@link org.lightmare.criteria.query.QueryStream} implementation
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type
 * @param <Q>
 *            {@link org.lightmare.criteria.query.LambdaStream} implementation
 */
@FunctionalInterface
public interface QueryInitializer<T, L extends LayerProvider, Q extends LambdaStream<T, ? super Q>>
        extends BiFunction<L, Class<T>, Q>, Serializable {

    /**
     * Initializes {@link org.lightmare.criteria.query.QueryStream}
     * implementation by
     * {@link org.lightmare.criteria.query.layers.LayerProvider} and
     * {@link Class} entity type
     * 
     * @return S {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    Q apply(L provider, Class<T> type);
}
