package org.lightmare.criteria.query.providers.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;

import org.lightmare.criteria.query.internal.EntityQueryStream;
import org.lightmare.criteria.query.internal.layers.JpaJdbcQueryLayer;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.query.providers.JpaQueryStream;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Abstract implementation of
 * {@link org.lightmare.criteria.query.internal.orm.ORMQueryWrapper} interface
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
abstract class AbstractJpaQueryWrapper<T> extends EntityQueryStream<T>
        implements JpaQueryWrapper<T>, JpaQueryStream<T> {

    private Integer startPosition;

    private Map<String, Object> hints = new HashMap<>();

    private FlushModeType flushMode;

    private LockModeType lockMode;

    protected AbstractJpaQueryWrapper(LayerProvider provider, Class<T> entityType) {
        super(provider, entityType);
    }

    @Override
    public JpaQueryStream<T> setFirstResult(int startPosition) {
        this.startPosition = startPosition;
        return this;
    }

    @Override
    public int getFirstResult() {
        return startPosition;
    }

    /**
     * Sets first result flag to query
     * 
     * @param query
     */
    private void putFirstResult(JpaJdbcQueryLayer<?> query) {
        ObjectUtils.nonNull(startPosition, query::setFirstResult);
    }

    @Override
    public JpaQueryStream<T> setHint(String hintName, Object value) {
        hints.put(hintName, value);
        return this;
    }

    @Override
    public Map<String, Object> getHints() {
        return hints;
    }

    /**
     * Sets query hints
     * 
     * @param query
     */
    private void putHints(JpaJdbcQueryLayer<?> query) {
        CollectionUtils.valid(hints, c -> c.forEach(query::setHint));
    }

    @Override
    public JpaQueryStream<T> setFlushMode(FlushModeType flushMode) {
        this.flushMode = flushMode;
        return this;
    }

    /**
     * Sets flush mode to query
     * 
     * @param query
     */
    private void putFlushMode(JpaJdbcQueryLayer<?> query) {
        ObjectUtils.nonNull(flushMode, query::setFlushMode);
    }

    @Override
    public JpaQueryStream<T> setLockMode(LockModeType lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    /**
     * Sets lock mode to query
     * 
     * @param query
     */
    private void setLockMode(JpaJdbcQueryLayer<?> query) {
        ObjectUtils.nonNull(lockMode, query::setLockMode);
    }

    /**
     * Adds additional JPA configuration to passed
     * {@link javax.persistence.Query} instance
     * 
     * @param query
     */
    protected void setJpaConfiguration(JpaJdbcQueryLayer<?> query) {

        putFirstResult(query);
        putHints(query);
        putFlushMode(query);
        setLockMode(query);
    }
}
