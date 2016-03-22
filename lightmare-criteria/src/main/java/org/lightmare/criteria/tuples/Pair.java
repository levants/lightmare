package org.lightmare.criteria.tuples;

import java.util.function.Function;

/**
 * Tuple of two elements
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            first element type parameter
 * @param <U>
 *            second element type parameter
 */
public class Pair<T, U> {

    private final T first;

    private final U second;

    private Pair(final T first, final U second) {
        this.first = first;
        this.second = second;
    }

    public static <T, U> Pair<T, U> of(final T first, final U second) {
        return new Pair<T, U>(first, second);
    }

    /**
     * Calls passed getter from second element of pair
     * 
     * @param getter
     * @return R from first getter
     */
    public <R> R firstGetter(Function<T, R> getter) {
        return getter.apply(first);
    }

    /**
     * Calls passed getter from first element of pair
     * 
     * @param getter
     * @return R from second getter
     */
    public <R> R secondGetter(Function<U, R> getter) {
        return getter.apply(second);
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }
}
