package org.lightmare.criteria.lambda;

import java.io.Serializable;

import org.lightmare.criteria.query.SubQueryStream;

@FunctionalInterface
public interface SubQueryConsumer<S extends Serializable, T extends Serializable> extends Serializable {

    void accept(SubQueryStream<S, T> subQuery);
}
