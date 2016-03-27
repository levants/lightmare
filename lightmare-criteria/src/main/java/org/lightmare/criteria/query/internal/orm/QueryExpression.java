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
import org.lightmare.criteria.query.providers.JpaQueryStream;

/**
 * General query components
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface QueryExpression<T>
        extends ORMQueryWrapper<T>, Expression<T, JpaQueryStream<T>>, ColumnExpression<T, JpaQueryStream<T>>,
        FunctionExpression<T>, SelectExpression<T>, OrderExpression<T, JpaQueryStream<T>>,
        GroupExpression<T, Object[], JpaQueryStream<Object[]>>, JoinExpressions<T>, ResultStream<T>,
        SubQueryProcessor<T>, AggregateFunction<T, Object[], JpaQueryStream<Object[]>> {

    /**
     * Gets wrapped entity {@link Class} instance
     * 
     * @return {@link Class} of entity type T
     */
    Class<T> getEntityType();

    // ======================================================================//

    /**
     * Set clause for bulk UPDATE query
     * 
     * @param field
     * @param value
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         current instance
     */
    <F> JpaQueryStream<T> set(EntityField<T, F> field, F value);

    // ======================================================================//

    /**
     * Appends to generated query prefix custom clause
     * 
     * @param clause
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         current instance
     */
    JpaQueryStream<T> appendPrefix(Object clause);

    /**
     * Appends to generated FROM clause
     * 
     * @param clause
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         current instance
     */
    JpaQueryStream<T> appendFrom(Object clause);

    /**
     * Gets generated JPA query for element count
     * 
     * @return {@link String} JPA query
     */
    String countSql();
}
