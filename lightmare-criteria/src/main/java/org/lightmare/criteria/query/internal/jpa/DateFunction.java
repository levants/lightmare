package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.query.internal.jpa.links.Dates;

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
     * @return {@link JPAFunction} current instance
     */
    JPAFunction<T> operateDate(String operator);

    /**
     * Create expression to return current date function.
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> currentDate() {
        return operateDate(Dates.CURRENT_DATE);
    }

    /**
     * Create expression to return current TIMESTAMP function.
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> currentTimestamp() {
        return operateDate(Dates.CURRENT_TIMESTAMP);
    }

    /**
     * Create expression to return current time function.
     *
     * @return {@link JPAFunction} current instance
     */
    default JPAFunction<T> currentTime() {
        return operateDate(Dates.CURRENT_TIME);
    }
}
