package org.lightmare.criteria.functions;

import java.io.Serializable;
import java.util.function.Consumer;

import org.lightmare.criteria.query.internal.jpa.HavingExpression;

/**
 * Consumer for HAVING clause
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
@FunctionalInterface
public interface HavingConsumer<T> extends Consumer<HavingExpression<T>>, Serializable {

    @Override
    void accept(HavingExpression<T> expression);
}
