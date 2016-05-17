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

import java.util.Calendar;
import java.util.Date;

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
     * Sets {@link java.util.Calendar} query parameters by names and temporal
     * types
     * 
     * @param name
     * @param value
     * @param temporalType
     */
    void setParameter(String name, Calendar value, TemporalType temporalType);

    /**
     * Sets {@link java.util.Date} query parameters by names and temporal types
     * 
     * @param name
     * @param value
     * @param temporalType
     */
    void setParameter(String name, Date value, TemporalType temporalType);

    /**
     * Unwraps and adds parameter from
     * {@link org.lightmare.criteria.tuples.ParameterTuple} to query
     * 
     * @param tuple
     */
    void setParameter(ParameterTuple tuple);

    /**
     * Sets upper limit for results to query
     * 
     * @param maxResult
     */
    void setMaxResults(int maxResult);

    /**
     * Gets upper limit for results to query
     * 
     * @return <code>int</code> upper limit for results
     */
    int getMaxResults();
}
