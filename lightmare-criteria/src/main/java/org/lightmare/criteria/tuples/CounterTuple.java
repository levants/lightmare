package org.lightmare.criteria.tuples;

import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Tuple for parameter and alias suffixes
 * 
 * @author Levan Tsinadze
 *
 */
public class CounterTuple {

    private int alias;

    private int parameter = CollectionUtils.SINGLETON;

    private CounterTuple() {
    }

    public static CounterTuple get() {
        return new CounterTuple();
    }

    public int getAndIncrementAlias() {
        return alias++;
    }

    private int getAndIncrementParameter() {
        return parameter++;
    }

    public Pair<String, Integer> getAndIncrement(String name) {

        Pair<String, Integer> pair;

        int count = getAndIncrementParameter();
        String countName = StringUtils.concat(name, StringUtils.UNDERSCORE, count);
        pair = Pair.of(countName, count);

        return pair;
    }
}
