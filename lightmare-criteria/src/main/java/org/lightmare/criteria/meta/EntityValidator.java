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
     * Validates if entity {@link Class} is not annotated with
     * {@link javax.persistence.MappedSuperclass} annotation
     * 
     * @param type
     * @return
     */
    private static boolean notMappedSuperclass(Class<?> type) {
        return ClassUtils.notAnnotated(type, MappedSuperclass.class);
    }

    /**
     * Validates if {@link Class} in
     * {@link org.lightmare.criteria.tuples.QueryTuple} and entity {@link Class}
     * not matches
     * 
     * @return <code>boolean</code> validation result
     */
    public static boolean typeMismatched(Class<?> type, QueryTuple tuple) {

        boolean valid;

        Class<?> resolved = tuple.getEntityType();
        valid = (ClassUtils.isOnlyAssignable(resolved, type) && notMappedSuperclass(resolved));

        return valid;
    }
}
