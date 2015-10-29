package org.lightmare.linq.lambda;

import java.io.Serializable;

/**
 * Interface for getter method reference call with entities
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            field type parameter
 */
@FunctionalInterface
public interface EntityField<T, F> extends Serializable {

    F apply(T value);
}
