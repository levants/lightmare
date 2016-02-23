package org.lightmare.criteria.query.internal;

import java.util.List;
import java.util.function.BiFunction;

import org.lightmare.criteria.config.Configuration.ResultRetriever;
import org.lightmare.criteria.query.internal.connectors.JdbcQueryLayer;
import org.lightmare.criteria.query.internal.connectors.LayerProvider;
import org.lightmare.criteria.query.internal.connectors.QueryLayer;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Direct JDBC query builder
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 */
public class JdbcEntityQueryStream<T> extends EntityQueryStream<T> implements JdbcQueryStream<T> {

    protected JdbcEntityQueryStream(LayerProvider provider, Class<T> entityType, String alias) {
        super(provider, entityType, alias);
    }

    /**
     * Retrieves result from generated
     * {@link org.lightmare.criteria.query.internal.connectors.JdbcQueryLayer}
     * instance
     * 
     * @param retriever
     * @param function
     * @return R result from generated
     *         {@link org.lightmare.criteria.query.internal.connectors.JdbcQueryLayer}
     */
    private <R> R retrieveResult(ResultRetriever<T> retriever,
            BiFunction<JdbcQueryLayer<T>, ResultRetriever<T>, R> function) {

        R result;

        QueryLayer<T> query = initTypedQuery();
        JdbcQueryLayer<T> jdbcQuery = ObjectUtils.cast(query);
        result = function.apply(jdbcQuery, retriever);

        return result;
    }

    @Override
    public T get(ResultRetriever<T> retriever) {
        return retrieveResult(retriever, JdbcQueryLayer::get);
    }

    @Override
    public List<T> toList(ResultRetriever<T> retriever) {
        return retrieveResult(retriever, JdbcQueryLayer::toList);
    }
}
