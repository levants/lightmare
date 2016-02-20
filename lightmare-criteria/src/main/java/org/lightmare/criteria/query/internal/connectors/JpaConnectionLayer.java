package org.lightmare.criteria.query.internal.connectors;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Implementation for JPA layer
 * 
 * @author Levan Tsinadze
 * 
 * @param <T>
 *            result type parameter
 */
public class JpaConnectionLayer<T> implements QueryLayer<T> {

    private final EntityManager em;

    private TypedQuery<T> query;

    private JpaConnectionLayer(final EntityManager em) {
        this.em = em;
    }

    @Override
    public QueryLayer<T> select(Class<T> type, String sql) {
        query = em.createQuery(sql, type);
        return this;
    }

    @Override
    public QueryLayer<T> update(Class<T> type, String sql) {
        query = em.createQuery(sql, type);
        return this;
    }

    @Override
    public QueryLayer<T> delete(Class<T> type, String sql) {
        query = em.createQuery(sql, type);
        return this;
    }

    @Override
    public List<T> toList() {
        return query.getResultList();
    }

    @Override
    public T get() {
        return query.getSingleResult();
    }

    @Override
    public int execute() {
        return query.executeUpdate();
    }
}
