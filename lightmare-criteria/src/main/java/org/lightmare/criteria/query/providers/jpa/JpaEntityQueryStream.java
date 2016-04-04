package org.lightmare.criteria.query.providers.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.internal.orm.subqueries.EntityEmbeddedStream;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.query.providers.JpaQueryStream;
import org.lightmare.criteria.tuples.QueryTuple;

public class JpaEntityQueryStream<T> extends AbsstractJpaQueryWrapper<T> implements JpaQueryStream<T> {

    protected JpaEntityQueryStream(final LayerProvider provider, final Class<T> entityType) {
        super(provider, entityType);
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
