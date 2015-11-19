package org.lightmare.criteria.query.internal.jpa.builders;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.query.QueryStream;

/**
 * Abstract class to process functional expression
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
abstract class AbstractFunctionExpression<T> extends AbstractQueryStream<T> {

    protected AbstractFunctionExpression(final EntityManager em, final Class<T> entityType, final String alias) {
        super(em, entityType, alias);
    }

    @Override
    public QueryStream<T> operateFunction(FunctionConsumer<T> function, String operator, Object value) {
        return this;
    }

    @Override
    public <S, F> QueryStream<T> operateFunctions(FunctionConsumer<T> function1, FunctionConsumer<T> function2,
            String function) {
        return this;
    }
}
