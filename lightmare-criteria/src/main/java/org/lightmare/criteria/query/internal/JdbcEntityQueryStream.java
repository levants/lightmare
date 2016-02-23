package org.lightmare.criteria.query.internal;

import org.lightmare.criteria.query.internal.connectors.LayerProvider;

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
}
