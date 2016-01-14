package org.lightmare.criteria.tuples;

/**
 * Parameter suffix by counter
 * 
 * @author Levan Tsinadze
 *
 */
public class SuffixTuple {

    private int counter;

    private SuffixTuple() {
    }

    public static SuffixTuple get() {
        return new SuffixTuple();
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
