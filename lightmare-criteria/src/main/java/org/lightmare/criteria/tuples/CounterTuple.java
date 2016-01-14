package org.lightmare.criteria.tuples;

/**
 * Tuple for parameter and alias suffixes
 * 
 * @author Levan Tsinadze
 *
 */
public class CounterTuple {

    private final SuffixTuple aliasSuffix;

    private final SuffixTuple parameterSuffix;

    private CounterTuple() {
        this.aliasSuffix = SuffixTuple.get();
        this.parameterSuffix = SuffixTuple.get();
    }

    public static CounterTuple get() {
        return new CounterTuple();
    }

    public SuffixTuple getAliasSuffix() {
        return aliasSuffix;
    }

    public SuffixTuple getParameterSuffix() {
        return parameterSuffix;
    }

    public int getAndIncrementAlias() {
        return aliasSuffix.getAndIncrement();
    }

    public int getAndIncrementParameter() {
        return parameterSuffix.getAndIncrement();
    }
}
