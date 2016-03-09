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

import org.lightmare.criteria.functions.initializers.QueryInitializer;
import org.lightmare.criteria.query.internal.layers.LayerProvider;

/**
 * Provider or factory class to initialize
 * {@link org.lightmare.criteria.query.providers.JpaQueryStream} by SELECT,
 * UPDATE or DELETE clause for entity type
 * 
 * @author Levan Tsinadze
 *
 */
public abstract class QueryProvider {

    protected QueryProvider() {
        throw new IllegalAccessError();
    }

    /**
     * Generates DELETE statements with custom alias
     * 
     * @param provider
     * @param entityType
     * @param initializer
     * @return S implementation of
     *         {@link org.lightmare.criteria.query.QueryStream} implementation
     *         with delete statement
     */
    public static <T, L extends LayerProvider, S extends QueryStream<T, ? super S>> S delete(L provider,
            Class<T> entityType, QueryInitializer<T, L, S> initializer) {
        return initializer.apply(provider, entityType);
    }

    /**
     * Generates UPDATE statements with custom alias
     * 
     * @param provider
     * @param entityType
     * @param initializer
     * @return S implementation of
     *         {@link org.lightmare.criteria.query.QueryStream} implementation
     *         with update statement
     */
    public static <T, L extends LayerProvider, S extends QueryStream<T, ? super S>> S update(L provider,
            Class<T> entityType, QueryInitializer<T, L, S> initializer) {
        return initializer.apply(provider, entityType);
    }

    /**
     * Generates SELECT statements with custom alias
     * 
     * @param provider
     * @param entityType
     * @param initializer
     * @return S implementation of
     *         {@link org.lightmare.criteria.query.QueryStream} implementation
     *         with select statement
     */
    public static <T, L extends LayerProvider, S extends QueryStream<T, ? super S>> S select(L provider,
            Class<T> entityType, QueryInitializer<T, L, S> initializer) {
        return initializer.apply(provider, entityType);
    }
}