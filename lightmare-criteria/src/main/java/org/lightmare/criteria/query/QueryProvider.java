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

import java.io.Serializable;

import javax.persistence.EntityManager;

/**
 * Provider class to initialize {@link QueryStream} by clause
 * 
 * @author Levan Tsinadze
 *
 */
public class QueryProvider {

    /**
     * Generates DELETE statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link FullQueryStream} with select statement
     */
    public static <T extends Serializable> QueryStream<T> delete(final EntityManager em, final Class<T> entityType,
	    final String entityAlias) {
	return FullQueryStream.delete(em, entityType, entityAlias);
    }

    /**
     * Generates DELETE statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link FullQueryStream} with select statement
     */
    public static <T extends Serializable> QueryStream<T> delete(final EntityManager em, Class<T> entityType) {
	return FullQueryStream.delete(em, entityType, QueryStream.DEFAULT_ALIAS);
    }

    /**
     * Generates UPDATE statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link FullQueryStream} with select statement
     */
    public static <T extends Serializable> QueryStream<T> update(final EntityManager em, final Class<T> entityType,
	    final String entityAlias) {
	return FullQueryStream.update(em, entityType, entityAlias);
    }

    /**
     * Generates UPDATE statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link FullQueryStream} with select statement
     */
    public static <T extends Serializable> QueryStream<T> update(final EntityManager em, Class<T> entityType) {
	return FullQueryStream.update(em, entityType, QueryStream.DEFAULT_ALIAS);
    }

    /**
     * Generates SELECT statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link FullQueryStream} with select statement
     */
    public static <T extends Serializable> QueryStream<T> select(final EntityManager em, final Class<T> entityType,
	    final String entityAlias) {
	return FullQueryStream.select(em, entityType, entityAlias);
    }

    /**
     * Generates SELECT statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link FullQueryStream} with select statement
     */
    public static <T extends Serializable> QueryStream<T> select(final EntityManager em, Class<T> entityType) {
	return FullQueryStream.select(em, entityType, QueryStream.DEFAULT_ALIAS);
    }
}
