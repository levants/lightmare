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

import org.lightmare.criteria.links.Filters;

/**
 * Main class for lambda expression analyze and JPA query generator
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
class FullQueryStream<T extends Serializable> extends EntityQueryStream<T> {

    protected FullQueryStream(final EntityManager em, final Class<T> entityType, final String alias) {
	super(em, entityType, alias);
    }

    /**
     * Generates DELETE statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link FullQueryStream} with select statement
     */
    protected static <T extends Serializable> FullQueryStream<T> delete(final EntityManager em,
	    final Class<T> entityType, final String alias) {

	FullQueryStream<T> stream = new FullQueryStream<T>(em, entityType, alias);

	stream.appendPrefix(Filters.DELETE).appendPrefix(Filters.FROM);
	appendEntityPart(stream);

	return stream;
    }

    /**
     * Generates DELETE statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link FullQueryStream} with select statement
     */
    protected static <T extends Serializable> FullQueryStream<T> delete(final EntityManager em, Class<T> entityType) {
	return delete(em, entityType, DEFAULT_ALIAS);
    }

    /**
     * Generates UPDATE statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link FullQueryStream} with select statement
     */
    protected static <T extends Serializable> FullQueryStream<T> update(final EntityManager em,
	    final Class<T> entityType, final String alias) {

	FullQueryStream<T> stream = new FullQueryStream<T>(em, entityType, alias);

	stream.appendPrefix(Filters.UPDATE);
	appendEntityPart(stream);

	return stream;
    }

    /**
     * Generates UPDATE statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link FullQueryStream} with select statement
     */
    protected static <T extends Serializable> FullQueryStream<T> update(final EntityManager em, Class<T> entityType) {
	return update(em, entityType, DEFAULT_ALIAS);
    }

    /**
     * Generates SELECT statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link FullQueryStream} with select statement
     */
    protected static <T extends Serializable> FullQueryStream<T> select(final EntityManager em,
	    final Class<T> entityType, final String alias) {

	FullQueryStream<T> stream = new FullQueryStream<T>(em, entityType, alias);

	stream.appendPrefix(Filters.SELECT).appendPrefix(stream.alias).appendPrefix(Filters.FROM);
	appendEntityPart(stream);

	return stream;
    }

    /**
     * Generates SELECT statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link FullQueryStream} with select statement
     */
    protected static <T extends Serializable> FullQueryStream<T> select(final EntityManager em, Class<T> entityType) {
	return select(em, entityType, DEFAULT_ALIAS);
    }
}
