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
package org.lightmare.criteria.query.orm;

import java.util.Set;

import org.lightmare.criteria.tuples.ParameterTuple;

/**
 * Interface for query result methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public interface ResultStream<T> {

    /**
     * Gets query parameters
     * 
     * @return java.util.Set} of
     *         {@link org.lightmare.criteria.tuples.ParameterTuple}s
     */
    Set<ParameterTuple> getParameters();

    /**
     * Runs generated query {@link javax.persistence.Query#getSingleResult()}
     * and retrieves single result for element count
     * 
     * @return {@link Long} element count value
     */
    Long count();
}
