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

    /**
     * Name count tuple
     * 
     * @author Levan Tsinadze
     *
     */
    public static class NameCount {

        private final String name;

        private final int count;

        public NameCount(final String name, final int count) {
            this.count = count;
            this.name = StringUtils.concat(name, StringUtils.UNDERSCORE, count);
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }
    }

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

    public NameCount getAndIncrement(String name) {
        return new NameCount(name, getAndIncrementParameter());
    }
}
