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

import org.lightmare.criteria.query.QueryResolver;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.layers.JpaJdbcQueryLayer;
import org.lightmare.criteria.query.providers.sql.SQLStream;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Abstract implementation of
 * {@link org.lightmare.criteria.query.internal.orm.ORMQueryWrapper} interface
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated SQL statements
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 * @param <O>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 * 
 */
abstract class AbstractORMQueryWrapper<T, Q extends QueryStream<T, ? super Q>, O extends QueryStream<Object[], ?>>
        implements SQLStream<T, Q, O>, QueryResolver<T> {

    private Integer maxResult;

    @Override
    public Q setMaxResults(int maxResult) {

        Q stream = stream();
        this.maxResult = maxResult;

        return stream;
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

    /**
     * Adds additional JPA configuration to passed
     * {@link javax.persistence.Query} instance
     * 
     * @param query
     */
    protected void setJPAConfiguration(JpaJdbcQueryLayer<?> query) {
        putMaxResult(query);
    }
}
