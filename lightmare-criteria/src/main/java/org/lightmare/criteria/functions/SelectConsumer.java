package org.lightmare.criteria.functions;

import java.io.Serializable;
import java.util.function.Consumer;

import org.lightmare.criteria.query.internal.jpa.SelectExpression.Select;

/**
 * Implementation of {@link java.util.function.Consumer} functional interface to
 * process JPA query SELECT clause
 * 
 * @author Levan Tsinadze
 *
 */
@FunctionalInterface
public interface SelectConsumer extends Consumer<Select>, Serializable {

    @Override
    void accept(Select select);
}
