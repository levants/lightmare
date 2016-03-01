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
package org.lightmare.criteria.query.internal.orm.builders;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;

import org.lightmare.criteria.query.QueryResolver;
import org.lightmare.criteria.query.internal.layers.JpaJdbcQueryLayer;
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
 *            entity type for generated JPA query
 */
abstract class AbstractORMQueryWrapper<T> implements JpaQueryStream<T>, QueryResolver<T> {

    private Integer maxResult;

    private Integer startPosition;

    private Map<String, Object> hints = new HashMap<>();

    private FlushModeType flushMode;

    private LockModeType lockMode;

    @Override
    public JpaQueryStream<T> setMaxResults(int maxResult) {
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
    private void putMaxResult(JpaJdbcQueryLayer<?> query) {
        ObjectUtils.nonNull(maxResult, query::setMaxResults);
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
    protected void setJPAConfiguration(JpaJdbcQueryLayer<?> query) {

        putFirstResult(query);
        putMaxResult(query);
        putHints(query);
        putFlushMode(query);
        setLockMode(query);
    }

    @Override
    public void close() {
        getLayerProvider().close();
    }
}
