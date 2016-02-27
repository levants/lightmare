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
package org.lightmare.criteria.query.providers;

import javax.persistence.EntityManager;

import org.lightmare.criteria.query.internal.connectors.JpaProvider;

/**
 * Query provider for JPA layer
 * 
 * @author Levan Tsinadze
 *
 */
public abstract class JpaQueryProvider {

    /**
     * Generates DELETE statements with custom alias
     * 
     * @param em
     * @param entityType
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         with delete statement
     */
    public static <T> JpaQueryStream<T> delete(final EntityManager em, Class<T> entityType) {
        return QueryProvider.delete(new JpaProvider(em), entityType, JpaQueryStreamBuilder::delete);
    }

    /**
     * Generates UPDATE statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         with update statement
     */
    public static <T> JpaQueryStream<T> update(final EntityManager em, Class<T> entityType) {
        return QueryProvider.update(new JpaProvider(em), entityType, JpaQueryStreamBuilder::update);
    }

    /**
     * Generates SELECT statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         with select statement
     */
    public static <T> JpaQueryStream<T> select(final EntityManager em, Class<T> entityType) {
        return QueryProvider.select(new JpaProvider(em), entityType, JpaQueryStreamBuilder::query);
    }
}
