package org.lightmare.criteria.query.providers.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.internal.orm.subqueries.EntityEmbeddedStream;
import org.lightmare.criteria.query.internal.orm.subqueries.EntitySubQueryStream;
import org.lightmare.criteria.query.internal.orm.subqueries.SubQueryStream;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.query.providers.JpaQueryStream;
import org.lightmare.criteria.tuples.QueryTuple;

public class JpaEntityQueryStream<T> extends AbstractJpaQueryWrapper<T> implements JpaQueryStream<T> {

    protected JpaEntityQueryStream(final LayerProvider provider, final Class<T> entityType) {
        super(provider, entityType);
    }
    
    // =========================operate=sub=queries==========================//

    /**
     * Generates
     * {@link org.lightmare.criteria.query.internal.orm.subqueries.SubQueryStream}
     * for entity type
     * 
     * @param type
     * @return {@link org.lightmare.criteria.query.internal.orm.subqueries.SubQueryStream}
     *         for entity {@link Class}
     */
    public <S> SubQueryStream<S, T> subQuery(Class<S> type) {
        return new EntitySubQueryStream<S, T>(this, type);
    }

    /**
     * Creates
     * {@link org.lightmare.criteria.query.internal.orm.subqueries.SubQueryStream}
     * for instant {@link Class} entity type
     * 
     * @param type
     * @param consumer
     */
    private <S> void initSubQuery(Class<S> type, QueryConsumer<S, JpaQueryStream<S>> consumer) {

        JpaQueryStream<S> query = subQuery(type);
        acceptAndCall(consumer, query);
        closeBracket();
        newLine();
    }

    @Override
    public <S> JpaQueryStream<T> operateSubQuery(Class<S> type, QueryConsumer<S, JpaQueryStream<S>> consumer) {

        openBracket();
        initSubQuery(type, consumer);

        return stream();
    }

    @Override
    public <F, S> JpaQueryStream<T> operateSubQuery(EntityField<T, F> field, String operator, Class<S> type,
            QueryConsumer<S, JpaQueryStream<S>> consumer) {

        appendOperator();
        appSubQuery(field, operator);
        initSubQuery(type, consumer);

        return stream();
    }

    @Override
    public <F, S> JpaQueryStream<T> operateSubQuery(Object value, String operator, Class<S> type,
            QueryConsumer<S, JpaQueryStream<S>> consumer) {

        appendOperator();
        appendOperator(value, operator);
        openBracket();
        initSubQuery(type, consumer);

        return stream();
    }

    @Override
    public <F, S> JpaQueryStream<T> operateFunctionWithSubQuery(FunctionConsumer<T> function, String operator,
            Class<S> type, QueryConsumer<S, JpaQueryStream<S>> consumer) {

        startFunctionExpression(function, operator);
        openBracket();
        initSubQuery(type, consumer);

        return stream();
    }

    @Override
    public <F, S> JpaQueryStream<T> operateSubQuery(String operator, Class<S> type,
            QueryConsumer<S, JpaQueryStream<S>> consumer) {

        appendOperator();
        appendBody(operator);
        openBracket();
        initSubQuery(type, consumer);

        return stream();
    }

    // =========================embedded=field=queries=======================//

    @Override
    public <F> JpaQueryStream<T> embedded(EntityField<T, F> field, QueryConsumer<F, JpaQueryStream<F>> consumer) {

        QueryTuple tuple = compose(field);
        Class<F> type = tuple.getFieldGenericType();
        String embeddedName = tuple.getFieldName();
        JpaQueryStream<F> embeddedQuery = new EntityEmbeddedStream<>(this, type, embeddedName);
        acceptAndCall(consumer, embeddedQuery);

        return this;
    }
}
