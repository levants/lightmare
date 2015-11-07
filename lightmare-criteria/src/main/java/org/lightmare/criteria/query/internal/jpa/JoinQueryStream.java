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
package org.lightmare.criteria.query.internal.jpa;

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.query.internal.QueryStream;

/**
 * Interface for [INNER, LEFT, FETCH] JOIN implementations
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public interface JoinQueryStream<T> {

    <E, C extends Collection<E>> void procesJoin(EntityField<T, C> field, String expression,
	    SubQueryConsumer<E, T> consumer);

    /**
     * Method for INNER JOIN function call
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <E, C extends Collection<E>> QueryStream<T> join(EntityField<T, C> field,
	    SubQueryConsumer<E, T> consumer);

    /**
     * Method for INNER JOIN function call without conditions
     * 
     * @param field
     * @return {@link QueryStream} current instance
     */
    default <E, C extends Collection<E>> QueryStream<T> join(EntityField<T, C> field) {
	return join(field, null);
    }

    /**
     * Method for LEFT JOIN function call
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <E, C extends Collection<E>> QueryStream<T> leftJoin(EntityField<T, C> field,
	    SubQueryConsumer<E, T> consumer);

    /**
     * Method for LEFT JOIN function call without conditions
     * 
     * @param field
     * @return {@link QueryStream} current instance
     */
    default <E, C extends Collection<E>> QueryStream<T> leftJoin(EntityField<T, C> field) {
	return leftJoin(field, null);
    }

    /**
     * Method for FETCH JOIN function call
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <E, C extends Collection<E>> QueryStream<T> fetchJoin(EntityField<T, C> field,
	    SubQueryConsumer<E, T> consumer);

    /**
     * Method for FETCH JOIN function call without conditions
     * 
     * @param field
     * @return {@link QueryStream} current instance
     */
    default <E, C extends Collection<E>> QueryStream<T> fetchJoin(EntityField<T, C> field) {
	return fetchJoin(field, null);
    }
}
