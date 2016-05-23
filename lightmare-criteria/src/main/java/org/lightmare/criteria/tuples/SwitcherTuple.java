package org.lightmare.criteria.tuples;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Wrapper class to switch operations
 * 
 * @author Levan Tsinadze
 *
 */
public class SwitcherTuple {

    private boolean firstValidation;

    private SwitcherTuple() {
        firstValidation = Boolean.TRUE;
    }

    public static SwitcherTuple get() {
        return new SwitcherTuple();
    }

    /**
     * Returns <code>boolean</code> flag value and if it is <code>true</code>
     * changes it
     * 
     * @return <code>boolean</code> validation result
     */
    public boolean validateAndSwitch() {

        boolean valid;

        if (firstValidation) {
            valid = Boolean.TRUE;
            firstValidation = Boolean.FALSE;
        } else {
            valid = Boolean.FALSE;
        }

        return valid;
    }

    /**
     * Validates and applies passed function
     * 
     * @param value
     * @param function
     * @return result from function
     */
    public <V, R> R validateAndApply(V value, Function<V, R> function, Function<V, R> elseFunction) {

        R result;

        if (firstValidation) {
            result = function.apply(value);
            firstValidation = Boolean.FALSE;
        } else {
            result = elseFunction.apply(value);
        }

        return result;
    }

    /**
     * Validates and applies passed supplier
     * 
     * @param function
     * @param elseFunction
     * @return result from function
     */
    public <T> T validateAndGet(Supplier<T> function, Supplier<T> elseFunction) {

        T result;

        if (firstValidation) {
            result = function.get();
            firstValidation = Boolean.FALSE;
        } else {
            result = elseFunction.get();
        }

        return result;
    }
}
