package org.lightmare.criteria.functions;

import java.io.Serializable;
import java.util.function.Consumer;

import org.lightmare.criteria.query.internal.jpa.GroupExpression;

/**
 * Consumer to process JPA query GROUP BY clause
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
@FunctionalInterface
public interface GroupByConsumer<T> extends Consumer<GroupExpression<T>>, Serializable {

    @Override
    void accept(GroupExpression<T> stream);
}
