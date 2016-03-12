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
package org.lightmare.criteria.query.internal.layers;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.TemporalType;

import org.lightmare.criteria.query.layers.QueryLayer;
import org.lightmare.criteria.tuples.ParameterTuple;

/**
 * Query lauer for JPA / JDBC queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface JpaJdbcQueryLayer<T> extends QueryLayer<T> {

    /**
     * Sets query parameters by names
     * 
     * @param name
     * @param value
     */
    void setParameter(String name, Object value);

    /**
     * Sets query parameters by names and temporal types
     * 
     * @param name
     * @param value
     * @param temporalType
     */
    void setParameter(String name, Calendar value, TemporalType temporalType);

    /**
     * Sets query parameters by names and temporal types
     * 
     * @param name
     * @param value
     * @param temporalType
     */
    void setParameter(String name, Date value, TemporalType temporalType);

    /**
     * Sets query parameters by
     * {@link org.lightmare.criteria.tuples.ParameterTuple} instance
     * 
     * @param tuple
     */
    void setParameter(ParameterTuple tuple);

    /**
     * Sets max results to query
     * 
     * @param maxResults
     */
    void setMaxResults(int maxResult);

    int getMaxResults();

    void setFirstResult(int startPosition);

    int getFirstResult();

    void setHint(String hintName, Object value);

    Map<String, Object> getHints();

    void setFlushMode(FlushModeType flushMode);

    void setLockMode(LockModeType lockMode);
}
