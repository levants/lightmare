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

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.HavingConsumer;
import org.lightmare.criteria.functions.SelectConsumer;
import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.query.orm.SelectExpression.Select;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Generates group by JPA query part
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.LambdaStream} implementation
 *            parameter
 */
public interface GroupExpression<T, Q extends LambdaStream<Object[], ?>> {

    /**
     * Generates HAVING clause for appropriated group by expression
     * 
     * @param consumer
     */
    void having(HavingConsumer consumer);

    /**
     * Group aggregate functions by fields
     * 
     * @param select
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     *         for {@link Object} array
     */
    Q groupBy(Select select);

    /**
     * Group aggregate functions by fields with HAVING clause
     * 
     * @param select
     * @param consumer
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     *         for {@link Object} array
     */
    default Q groupBy(Select select, HavingConsumer consumer) {

        Q stream = groupBy(select);

        GroupExpression<Object[], ?> sql = ObjectUtils.cast(stream);
        sql.having(consumer);

        return stream;
    }

    /**
     * Grouping expression with consumer
     * 
     * @param select
     * @return {@link org.lightmare.criteria.query.LambdaStream} for
     *         {@link Object} array
     */
    Q group(SelectConsumer select);

    /**
     * Grouping expression with consumer and HAVING clause
     * 
     * @param select
     * @param consumer
     * @return {@link org.lightmare.criteria.query.LambdaStream} for
     *         {@link Object} array
     */
    default Q group(SelectConsumer select, HavingConsumer consumer) {

        Q stream = group(select);

        GroupExpression<Object[], ?> sql = ObjectUtils.cast(stream);
        sql.having(consumer);

        return stream;
    }

    /**
     * Group by field
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.LambdaStream} for
     *         {@link Object} array
     */
    <F> Q groupBy(EntityField<T, F> field);

    /**
     * Group by field with HAVING clause
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.LambdaStream} for
     *         {@link Object} array
     */
    default <F> Q groupBy(EntityField<T, F> field, HavingConsumer consumer) {

        Q stream = groupBy(field);

        GroupExpression<Object[], ?> sql = ObjectUtils.cast(stream);
        sql.having(consumer);

        return stream;
    }
}
