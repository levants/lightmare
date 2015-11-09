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

import javax.persistence.EntityManager;

/**
 * Provider or factory class to initialize {@link QueryStream} by SELECT, UPDATE
 * or DELETE clause for entity type
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
     * @return {@link JPAQueryStream} with select statement
     */
    public static <T> QueryStream<T> delete(final EntityManager em, final Class<T> entityType,
	    final String entityAlias) {
	return JPAQueryStream.delete(em, entityType, entityAlias);
    }

    /**
     * Generates DELETE statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link JPAQueryStream} with select statement
     */
    public static <T> QueryStream<T> delete(final EntityManager em, Class<T> entityType) {
	return JPAQueryStream.delete(em, entityType, QueryStream.DEFAULT_ALIAS);
    }

    /**
     * Generates UPDATE statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link JPAQueryStream} with select statement
     */
    public static <T> QueryStream<T> update(final EntityManager em, final Class<T> entityType,
	    final String entityAlias) {
	return JPAQueryStream.update(em, entityType, entityAlias);
    }

    /**
     * Generates UPDATE statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link JPAQueryStream} with select statement
     */
    public static <T> QueryStream<T> update(final EntityManager em, Class<T> entityType) {
	return JPAQueryStream.update(em, entityType, QueryStream.DEFAULT_ALIAS);
    }

    /**
     * Generates SELECT statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link JPAQueryStream} with select statement
     */
    public static <T> QueryStream<T> select(final EntityManager em, final Class<T> entityType,
	    final String entityAlias) {
	return JPAQueryStream.query(em, entityType, entityAlias);
    }

    /**
     * Generates SELECT statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link JPAQueryStream} with select statement
     */
    public static <T> QueryStream<T> select(final EntityManager em, Class<T> entityType) {
	return JPAQueryStream.query(em, entityType, QueryStream.DEFAULT_ALIAS);
    }

    /**
     * Generates SELECT statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link JPAQueryStream} with select statement
     */
    public static <T> QueryStream<T> query(final EntityManager em, final Class<T> entityType,
	    final String entityAlias) {
	return select(em, entityType, entityAlias);
    }

    /**
     * Generates SELECT statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link JPAQueryStream} with select statement
     */
    public static <T> QueryStream<T> query(final EntityManager em, Class<T> entityType) {
	return select(em, entityType);
    }
}
