package org.lightmare.criteria.query.internal.connectors;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
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

    @Override
    public void setMaxResults(int maxResult) {
        query.setMaxResults(maxResult);
    }

    @Override
    public int getMaxResults() {
        return query.getMaxResults();
    }

    @Override
    public void setFirstResult(int startPosition) {
        query.setFirstResult(startPosition);
    }

    @Override
    public int getFirstResult() {
        return query.getFirstResult();
    }

    @Override
    public void setHint(String hintName, Object value) {
        query.setHint(hintName, value);
    }

    @Override
    public Map<String, Object> getHints() {
        return query.getHints();
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
        query.setFlushMode(flushMode);
    }

    @Override
    public void setLockMode(LockModeType lockMode) {
        query.setLockMode(lockMode);
    }

    @Override
    public void close() {
        em.close();
    }
}
