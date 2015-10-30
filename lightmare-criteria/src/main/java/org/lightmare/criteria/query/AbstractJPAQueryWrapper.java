package org.lightmare.criteria.query;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.lightmare.utils.collections.CollectionUtils;

/**
 * Abstract implementation of {@link JPAQueryWrapper} interface
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
abstract class AbstractJPAQueryWrapper<T extends Serializable> implements JPAQueryWrapper<T> {

    private Integer maxResult;

    private Integer startPosition;

    private Map<String, Object> hints = new HashMap<>();

    private FlushModeType flushMode;

    private LockModeType lockMode;

    @Override
    public QueryStream<T> setMaxResults(int maxResult) {
	this.maxResult = maxResult;
	return this;
    }

    @Override
    public int getMaxResults() {
	return maxResult;
    }

    private void putMaxResult(Query query) {

	if (Objects.nonNull(maxResult)) {
	    query.setMaxResults(maxResult);
	}
    }

    @Override
    public QueryStream<T> setFirstResult(int startPosition) {
	this.startPosition = startPosition;
	return this;
    }

    @Override
    public int getFirstResult() {
	return startPosition;
    }

    private void putFirstResult(Query query) {

	if (Objects.nonNull(startPosition)) {
	    query.setFirstResult(startPosition);
	}
    }

    @Override
    public QueryStream<T> setHint(String hintName, Object value) {
	hints.put(hintName, value);
	return this;
    }

    @Override
    public Map<String, Object> getHints() {
	return hints;
    }

    private void putHints(Query query) {

	if (CollectionUtils.valid(hints)) {
	    hints.forEach((key, value) -> query.setHint(key, value));
	}
    }

    @Override
    public QueryStream<T> setFlushMode(FlushModeType flushMode) {
	this.flushMode = flushMode;
	return this;
    }

    private void putFlushMode(Query query) {

	if (Objects.nonNull(flushMode)) {
	    query.setFlushMode(flushMode);
	}
    }

    @Override
    public QueryStream<T> setLockMode(LockModeType lockMode) {
	this.lockMode = lockMode;
	return this;
    }

    private void setLockMode(Query query) {

	if (Objects.nonNull(lockMode)) {
	    query.setLockMode(lockMode);
	}
    }

    /**
     * Adds additional JPA configuration to passed {@link Query} instance
     * 
     * @param query
     */
    protected void setConfiguration(Query query) {

	putFirstResult(query);
	putMaxResult(query);
	putHints(query);
	putFlushMode(query);
	setLockMode(query);
    }
}
