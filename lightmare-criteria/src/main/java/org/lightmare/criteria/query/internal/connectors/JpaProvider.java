package org.lightmare.criteria.query.internal.connectors;

import javax.persistence.EntityManager;

public class JpaProvider implements LayerProvider {

    private final EntityManager em;

    public JpaProvider(final EntityManager em) {
        this.em = em;
    }

    @Override
    public <T> QueryLayer<T> query(String sql, Class<T> type) {
        return new JpaQueryLayer<>(em, sql, type);
    }

    @Override
    public QueryLayer<?> query(String sql) {
        return new JpaQueryLayer<>(em, sql);
    }

    @Override
    public void close() {
        em.close();
    }
}
