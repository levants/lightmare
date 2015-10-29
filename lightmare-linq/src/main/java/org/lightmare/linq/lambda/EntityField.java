package org.lightmare.linq.lambda;

import java.util.function.Function;

/**
 * Interface for getter method reference call with entities
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            field type parameter
 */
@FunctionalInterface
public interface EntityField<T, F> extends Function<T, F> {

    F apply(T value);
}
