package org.lightmare.criteria.query.internal.layers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.lightmare.criteria.tuples.ParameterTuple;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Implementation of
 * {@link org.lightmare.criteria.query.internal.layers.JpaJdbcQueryLayer} for
 * criteria API
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public class CriteriaQueryLayer<T> implements JpaJdbcQueryLayer<T> {

    private final Query query;

    public CriteriaQueryLayer(final EntityManager em, CriteriaQuery<?> sql) {
        query = em.createQuery(sql);
    }

    @Override
    public List<T> toList() {

        List<T> resuts;

        TypedQuery<T> typed = ObjectUtils.cast(query);
        resuts = typed.getResultList();

        return resuts;
    }

    @Override
    public T get() {

        T result;

        TypedQuery<T> typed = ObjectUtils.cast(query);
        result = typed.getSingleResult();

        return result;
    }

    @Override
    public int execute() {
        return query.executeUpdate();
    }

    @Override
    public void setParameter(String name, Object value) {
        query.setParameter(name, value);
    }

    @Override
    public void setParameter(String name, Calendar value, TemporalType temporalType) {
        query.setParameter(name, value, temporalType);
    }

    @Override
    public void setParameter(String name, Date value, TemporalType temporalType) {
        query.setParameter(name, value, temporalType);
    }

    /**
     * Sets date parameter to query
     * 
     * @param parameter
     */
    private void setDateParameter(ParameterTuple tuple) {

        String name = tuple.getName();
        Object value = tuple.getValue();
        TemporalType temporalType = tuple.getTemporalType();
        if (value instanceof Calendar) {
            ObjectUtils.cast(value, Calendar.class, c -> setParameter(name, c, temporalType));
        } else if (value instanceof Date) {
            ObjectUtils.cast(value, Date.class, c -> setParameter(name, c, temporalType));
        }
    }

    /**
     * Sets parameter to query
     * 
     * @param tuple
     */
    @Override
    public void setParameter(ParameterTuple tuple) {

        String name = tuple.getName();
        Object value = tuple.getValue();
        TemporalType temporalType = tuple.getTemporalType();
        if (temporalType == null) {
            setParameter(name, value);
        } else {
            setDateParameter(tuple);
        }
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
}
