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
import org.lightmare.criteria.functions.HavingConsumer;
import org.lightmare.criteria.functions.SelectConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.SelectExpression.Select;

/**
 * Generates group by JPA query part
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface GroupExpression<T> {

    /**
     * Generates HAVING clause for appropriated group by expression
     * 
     * @param consumer
     */
    void having(HavingConsumer<T> consumer);

    /**
     * Group aggregate functions by fields
     * 
     * @param select
     * @return {@link org.lightmare.criteria.query.QueryStream} for
     *         {@link Object} array
     */
    QueryStream<Object[]> groupBy(Select select);

    /**
     * Grouping expression with consumer
     * 
     * @param select
     * @return {@link org.lightmare.criteria.query.QueryStream} for
     *         {@link Object} array
     */
    QueryStream<Object[]> group(SelectConsumer select);

    /**
     * Group by fields
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.QueryStream} for
     *         {@link Object} array
     */
    <F> QueryStream<Object[]> groupBy(EntityField<T, F> field);
}
