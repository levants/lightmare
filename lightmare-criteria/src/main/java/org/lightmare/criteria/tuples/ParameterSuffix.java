package org.lightmare.criteria.tuples;

/**
 * Parameter suffix by counter
 * 
 * @author Levan Tsinadze
 *
 */
public class ParameterSuffix {

    private int counter;

    private ParameterSuffix() {
    }

    public static ParameterSuffix get() {
        return new ParameterSuffix();
    }

    /**
     * Gets value of counter and increments it for next call
     * 
     * @return <code>int</code> value before increment
     */
    public int getAndIncrement() {
        int value = counter++;
        return value;
    }
}
