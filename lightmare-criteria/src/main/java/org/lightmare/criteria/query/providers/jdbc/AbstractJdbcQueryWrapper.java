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
package org.lightmare.criteria.query.providers.jdbc;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;

import org.lightmare.criteria.query.internal.layers.JpaQueryLayer;
import org.lightmare.criteria.query.internal.orm.builders.EntityQueryStream;
import org.lightmare.criteria.query.layers.LayerProvider;
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
abstract class AbstractJdbcQueryWrapper<T> extends EntityQueryStream<T, JdbcQueryStream<T>, JdbcQueryStream<Object[]>>
        implements JdbcQueryStream<T> {

    private Integer startPosition;

    private Map<String, Object> hints = new HashMap<>();

    private FlushModeType flushMode;

    private LockModeType lockMode;

    protected AbstractJdbcQueryWrapper(LayerProvider provider, Class<T> entityType) {
        super(provider, entityType);
    }

    /**
     * Sets first result flag to query
     * 
     * @param query
     */
    private void putFirstResult(JpaQueryLayer<?> query) {
        ObjectUtils.nonNull(startPosition, query::setFirstResult);
    }

    /**
     * Sets query hints
     * 
     * @param query
     */
    private void putHints(JpaQueryLayer<?> query) {
        CollectionUtils.valid(hints, c -> c.forEach(query::setHint));
    }

    /**
     * Sets flush mode to query
     * 
     * @param query
     */
    private void putFlushMode(JpaQueryLayer<?> query) {
        ObjectUtils.nonNull(flushMode, query::setFlushMode);
    }

    /**
     * Sets lock mode to query
     * 
     * @param query
     */
    private void setLockMode(JpaQueryLayer<?> query) {
        ObjectUtils.nonNull(lockMode, query::setLockMode);
    }

    /**
     * Adds additional JPA configuration to passed
     * {@link javax.persistence.Query} instance
     * 
     * @param query
     */
    protected void setJpaConfiguration(JpaQueryLayer<?> query) {

        putFirstResult(query);
        putHints(query);
        putFlushMode(query);
        setLockMode(query);
    }
}
