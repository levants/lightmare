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
package org.lightmare.criteria.query.orm;

import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * General query components
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 * @param <O>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
public interface QueryExpression<T, Q extends QueryStream<T, ? super Q>, O extends QueryStream<Object[], ? super O>>
        extends ORMQueryWrapper<T, Q>, Expression<T, Q>, ColumnExpression<T, Q>, FunctionExpression<T, Q>,
        SelectExpression<T, Q, O>, OrderExpression<T, Q>, GroupExpression<T, O>, JoinOperator<T, Q>, ResultStream<T>,
        SubQueryOperator<T, Q>, AggregateFunction<T, O> {

    /**
     * Gets wrapped entity {@link Class} instance
     * 
     * @return {@link Class} of entity type T
     */
    Class<T> getEntityType();

    // ======================================================================//

    /**
     * Appends to generated query prefix custom clause
     * 
     * @param clause
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    Q appendPrefix(Object clause);

    /**
     * Appends multiply prefixes simultaneously
     * 
     * @param clauses
     */
    default void appendPrefixes(Object... clauses) {
        ObjectUtils.nonNullObjects(clauses, c -> CollectionUtils.forEach(c, (i, s) -> appendPrefix(s)));
    }

    /**
     * Appends to generated FROM clause
     * 
     * @param clause
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    Q appendFrom(Object clause);

    /**
     * Appends FROM clause to query
     * 
     * @param clauses
     */
    default void appendFromClause(Object... clauses) {
        ObjectUtils.nonNullObjects(clauses, c -> CollectionUtils.forEach(c, (i, s) -> appendFrom(s)));
    }

    /**
     * Gets generated JPA query for element count
     * 
     * @return {@link String} JPA query
     */
    String countSql();
}
