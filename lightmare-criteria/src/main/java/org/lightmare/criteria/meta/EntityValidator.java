package org.lightmare.criteria.meta;

import javax.persistence.MappedSuperclass;

import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ClassUtils;

/**
 * Validates entity {@link Class} for
 * {@link org.lightmare.criteria.tuples.QueryTuple} resolved from method
 * reference
 * 
 * @author Levan Tsinadze
 *
 */
public class EntityValidator {

    /**
     * Validates if resolved {@link Class} is not match with entity
     * {@link Class} and should be switched further processing
     * 
     * @param type
     * @param resolved
     * @return <code>boolean</code> validation result
     */
    private static boolean classMismatch(Class<?> type, Class<?> resolved) {
        return (ClassUtils.isOnlyAssignable(resolved, type) && ClassUtils.notAnnotated(type, MappedSuperclass.class));
    }

    /**
     * Validates if resolved entity {@link Class} and entity {@link Class} not
     * matches
     * 
     * @param type
     * @param resolved
     * @return <code>boolean</code> validation result
     */
    private static boolean typeMismatched(Class<?> type, Class<?> resolved) {
        return (resolved.isInterface() || classMismatch(type, resolved));
    }

    /**
     * Validates if {@link Class} in
     * {@link org.lightmare.criteria.tuples.QueryTuple} and entity {@link Class}
     * not matches
     * 
     * @param type
     * @param tuple
     * @return <code>boolean</code> validation result
     */
    public static boolean typeMismatched(Class<?> type, QueryTuple tuple) {
        return typeMismatched(type, tuple.getEntityType());
    }
}
