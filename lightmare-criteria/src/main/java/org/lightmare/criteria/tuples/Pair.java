package org.lightmare.criteria.tuples;

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

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }
}
