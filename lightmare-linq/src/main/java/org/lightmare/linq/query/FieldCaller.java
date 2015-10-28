package org.lightmare.linq.query;

import java.io.Serializable;

@FunctionalInterface
public interface FieldCaller<T> extends Serializable {

	T call();
}
