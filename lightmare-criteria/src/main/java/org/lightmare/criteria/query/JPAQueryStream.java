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

import org.lightmare.criteria.query.internal.EntityQueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Clauses;

/**
 * Main class for lambda expression analyze and JPA query build and run
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 */
public class JPAQueryStream<T> extends EntityQueryStream<T> {

    protected JPAQueryStream(final EntityManager em, final Class<T> entityType, final String alias) {
        super(em, entityType, alias);
    }

    /**
     * Generates DELETE statement with custom alias
     * 
     * @param em
     * @param entityType
     * @param alias
     * @return {@link org.lightmare.criteria.query.JPAQueryStream} with select
     *         statement
     */
    protected static <T> JPAQueryStream<T> delete(final EntityManager em, final Class<T> entityType,
            final String alias) {

        JPAQueryStream<T> stream = new JPAQueryStream<T>(em, entityType, alias);

        stream.appendPrefix(Clauses.DELETE);
        appendEntityPart(stream);

        return stream;
    }

    /**
     * Generates DELETE statement with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link org.lightmare.criteria.query.JPAQueryStream} with select
     *         statement
     */
    protected static <T> JPAQueryStream<T> delete(final EntityManager em, Class<T> entityType) {
        return delete(em, entityType, DEFAULT_ALIAS);
    }

    /**
     * Generates UPDATE statement with custom alias
     * 
     * @param em
     * @param entityType
     * @param alias
     * @return {@link org.lightmare.criteria.query.JPAQueryStream} with select
     *         statement
     */
    protected static <T> JPAQueryStream<T> update(final EntityManager em, final Class<T> entityType,
            final String alias) {

        JPAQueryStream<T> stream = new JPAQueryStream<T>(em, entityType, alias);

        stream.appendPrefix(Clauses.UPDATE);
        appendEntityPart(stream);

        return stream;
    }

    /**
     * Generates UPDATE statement with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link org.lightmare.criteria.query.JPAQueryStream} with select
     *         statement
     */
    protected static <T> JPAQueryStream<T> update(final EntityManager em, Class<T> entityType) {
        return update(em, entityType, DEFAULT_ALIAS);
    }

    /**
     * Generates SELECT statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param alias
     * @return {@link org.lightmare.criteria.query.JPAQueryStream} with select
     *         statement
     */
    protected static <T> JPAQueryStream<T> query(final EntityManager em, final Class<T> entityType,
            final String alias) {

        JPAQueryStream<T> stream = new JPAQueryStream<T>(em, entityType, alias);
        startsSelect(stream);

        return stream;
    }

    /**
     * Generates SELECT statement with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link org.lightmare.criteria.query.JPAQueryStream} with select
     *         statement
     */
    protected static <T> JPAQueryStream<T> query(final EntityManager em, Class<T> entityType) {
        return query(em, entityType, DEFAULT_ALIAS);
    }
}
