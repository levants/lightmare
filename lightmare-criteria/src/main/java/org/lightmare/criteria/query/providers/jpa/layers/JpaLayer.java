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
package org.lightmare.criteria.query.providers.jpa.layers;

import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;

/**
 * General methods to override in
 * {@link org.lightmare.criteria.query.layers.QueryLayer} implementation for JPA
 * queries
 * 
 * @author levan Tsinadze
 *
 */
public interface JpaLayer {

    /**
     * Sets first index for results
     * 
     * @param startPosition
     */
    void setFirstResult(int startPosition);

    /**
     * The position of the first result the query object was set to retrieve.
     * Returns 0 if <code>setFirstResult</code> was not applied to the query
     * object.
     *
     * @return position of the first result
     */
    int getFirstResult();

    /**
     * Set a query property or hint
     * 
     * @param hintName
     * @param value
     */
    void setHint(String hintName, Object value);

    /**
     * Get the properties and hints and associated values that are in effect for
     * the query instance.
     * 
     * @return query properties and hints
     */
    Map<String, Object> getHints();

    /**
     * Set the flush mode type to be used for the query execution. The flush
     * mode type applies to the query regardless of the flush mode type in use
     * for the entity manager.
     * 
     * @param flushMode
     */
    void setFlushMode(FlushModeType flushMode);

    /**
     * Set the lock mode type to be used for the query execution.
     * 
     * @param lockMode
     */
    void setLockMode(LockModeType lockMode);
}
