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

import java.util.function.Function;

/**
 * Provider or factory class to initialize
 * {@link org.lightmare.criteria.query.JpaQueryStream} by SELECT, UPDATE or DELETE
 * clause for entity type
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
     * @param entityType
     * @param function
     * @return S implementation of
     *         {@link org.lightmare.criteria.query.JpaQueryStream} with delete
     *         statement
     */
    public static <T, S extends JpaQueryStream<T>> S delete(Class<T> entityType, Function<Class<T>, S> function) {
        return function.apply(entityType);
    }

    /**
     * Generates UPDATE statements with custom alias
     * 
     * @param entityType
     * @param function
     * @return S implementation of
     *         {@link org.lightmare.criteria.query.JpaQueryStream} with update
     *         statement
     */
    public static <T, S extends JpaQueryStream<T>> S update(Class<T> entityType, Function<Class<T>, S> function) {
        return function.apply(entityType);
    }

    /**
     * Generates SELECT statements with custom alias
     * 
     * @param entityType
     * @param function
     * @return S implementation of
     *         {@link org.lightmare.criteria.query.JpaQueryStream} with select
     *         statement
     */
    public static <T, S extends JpaQueryStream<T>> S select(Class<T> entityType, Function<Class<T>, S> function) {
        return function.apply(entityType);
    }
}