package org.lightmare.linq.lambda;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Interface for method reference call
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 */
@FunctionalInterface
public interface FieldGetter<T> extends Supplier<T>, Serializable {

    T get();
}
