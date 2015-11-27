package org.lightmare.criteria.query.internal.jpa;

import java.util.Comparator;

import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Operators;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Sub query processor for ANY clause and arbitrary object or {@link Comparator}
 * implementations
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface AnyToObjectSubQueryProcessor<T> extends SubQueryOperator<T> {

    /**
     * Provides method to process sub queries with ANY clause
     * 
     * @param value
     * @param operator
     * @param stream
     * @return {@link QueryStream} current instance
     */
    default <F, S> QueryStream<T> operateSubQuery(Object value, String operator, Any<S> stream) {
        String composed = StringUtils.concat(operator, Operators.ANY);
        return operateSubQuery(value, composed, stream.getType(), stream.getConsumer());
    }
}
