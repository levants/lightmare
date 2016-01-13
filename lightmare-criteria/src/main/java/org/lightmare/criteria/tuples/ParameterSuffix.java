package org.lightmare.criteria.tuples;

/**
 * Parameter suffox by counter
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

    public void increment() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }

    /**
     * Gets value of counter and increments
     * 
     * @return <code>int</code> incremented value
     */
    public int getAndAdd() {

        int value = getCounter();
        increment();

        return value;
    }
}
