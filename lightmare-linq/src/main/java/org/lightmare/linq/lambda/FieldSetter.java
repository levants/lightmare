package org.lightmare.linq.lambda;

import java.util.function.Function;

/**
 * Interface for setter method reference call
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            field type parameter
 */
@FunctionalInterface
public interface FieldSetter<T, F> extends Function<T, F> {

    F apply(T value);
}
