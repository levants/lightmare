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

    JPAFunction<T> operateDate(String operator);

    /**
     * Create expression to return current date.
     *
     * @return current date
     */
    default JPAFunction<T> currentDate() {
        return operateDate(Dates.CURRENT_DATE);
    }

    /**
     * Create expression to return current timestamp.
     *
     * @return current timestamp
     */
    default JPAFunction<T> currentTimestamp() {
        return operateDate(Dates.CURRENT_TIMESTAMP);
    }

    /**
     * Create expression to return current time.
     *
     * @return current time
     */
    default JPAFunction<T> currentTime() {
        return operateDate(Dates.CURRENT_TIME);
    }
}
