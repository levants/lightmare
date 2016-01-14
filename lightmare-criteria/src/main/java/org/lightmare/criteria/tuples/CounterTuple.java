package org.lightmare.criteria.tuples;

/**
 * Tuple for parameter and alias suffixes
 * 
 * @author Levan Tsinadze
 *
 */
public class CounterTuple {

    private int alias;

    private int parameter;

    private CounterTuple() {
    }

    public static CounterTuple get() {
        return new CounterTuple();
    }

    public int getAndIncrementAlias() {
        return alias++;
    }

    public int getAndIncrementParameter() {
        return parameter++;
    }
}
