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

import org.lightmare.criteria.query.internal.orm.links.Dates;

/**
 * Date and time function expressions
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface DateFunction<T> {

    /**
     * Generates appropriate date and time function expression
     * 
     * @param operator
     * @return {@link org.lightmare.criteria.query.internal.orm.ORMFunction}
     *         current instance
     */
    ORMFunction<T> operateDate(String operator);

    /**
     * Create expression to return current date function.
     *
     * @return {@link org.lightmare.criteria.query.internal.orm.ORMFunction}
     *         current instance
     */
    default ORMFunction<T> currentDate() {
        return operateDate(Dates.CURRENT_DATE);
    }

    /**
     * Create expression to return current TIMESTAMP function.
     *
     * @return {@link org.lightmare.criteria.query.internal.orm.ORMFunction}
     *         current instance
     */
    default ORMFunction<T> currentTimestamp() {
        return operateDate(Dates.CURRENT_TIMESTAMP);
    }

    /**
     * Create expression to return current time function.
     *
     * @return {@link org.lightmare.criteria.query.internal.orm.ORMFunction}
     *         current instance
     */
    default ORMFunction<T> currentTime() {
        return operateDate(Dates.CURRENT_TIME);
    }
}
