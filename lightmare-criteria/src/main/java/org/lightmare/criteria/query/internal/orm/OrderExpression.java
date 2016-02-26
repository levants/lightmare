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
package org.lightmare.criteria.query.internal.orm;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.JpaQueryStream;
import org.lightmare.criteria.query.internal.orm.links.Orders;

/**
 * Query expressions for ORDER BY clauses
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface OrderExpression<T> {

    // =========================order=by=====================================//

    /**
     * Generates ORDER BY part for field
     * 
     * @param dir
     * @param field
     * @return {@link org.lightmare.criteria.query.JpaQueryStream} current instance
     */
    <F> JpaQueryStream<T> order(String dir, EntityField<T, F> field);

    /**
     * Generates ORDER BY part for field
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.JpaQueryStream} current instance
     */
    default <F> JpaQueryStream<T> orderBy(EntityField<T, F> field) {
        return order(null, field);
    }

    /**
     * Generates ORDER BY with DESC for field
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.JpaQueryStream} current instance
     */
    default <F> JpaQueryStream<T> orderByDesc(EntityField<T, F> field) {
        return order(Orders.DESC, field);
    }
}
