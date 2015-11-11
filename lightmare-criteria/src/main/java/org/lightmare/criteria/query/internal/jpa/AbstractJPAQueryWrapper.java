/*
 * Lightmare-criteria, JPA-QL query generator using lambda expressions
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.criteria.query.internal.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.utils.CollectionUtils;

/**
 * Abstract implementation of {@link JPAQueryWrapper} interface
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
abstract class AbstractJPAQueryWrapper<T> implements QueryStream<T> {

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

    /**
     * Sets max results to query
     * 
     * @param query
     */
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

    /**
     * Sets first result flag to query
     * 
     * @param query
     */
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

    /**
     * Sets query hints
     * 
     * @param query
     */
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
    protected void setJPAConfiguration(Query query) {

        putFirstResult(query);
        putMaxResult(query);
        putHints(query);
        putFlushMode(query);
        setLockMode(query);
    }

    @Override
    public void close() {
        getEntityManager().close();
    }
}
