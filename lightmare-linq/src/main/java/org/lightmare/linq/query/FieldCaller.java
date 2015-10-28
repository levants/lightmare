package org.lightmare.linq.query;

import java.io.Serializable;

/**
 * Interface for method reference call
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 */
@FunctionalInterface
public interface FieldCaller<T> extends Serializable {

    T call();
}
