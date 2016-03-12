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
package org.lightmare.criteria.query;

import org.lightmare.criteria.query.layers.LayerProvider;

/**
 * Abstraction of entity type, alias, and
 * {@link org.lightmare.criteria.query.layers.LayerProvider} for various data
 * base layers
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface LayerStream<T> {

    /**
     * Gets data base layer provider implementation
     * 
     * @return {@link org.lightmare.criteria.query.layers.LayerProvider}
     *         implementation
     */
    LayerProvider getLayerProvider();

    /**
     * Gets entity {@link Class} for this query stream
     * 
     * @return {@link Class} entity type
     */
    Class<T> getEntityType();

    /**
     * Gets entity alias for query
     * 
     * @return {@link String} entity alias
     */
    String getAlias();

    /**
     * Gets generated query
     * 
     * @return {@link String} query
     */
    String sql();
}
