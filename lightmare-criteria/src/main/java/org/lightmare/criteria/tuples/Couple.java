/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
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
public class Couple<T, U> {

    private final T first;

    private final U second;

    private Couple(final T first, final U second) {
        this.first = first;
        this.second = second;
    }

    public static <T, U> Couple<T, U> of(final T first, final U second) {
        return new Couple<T, U>(first, second);
    }

    /**
     * Calls passed getter from second element of couple
     * 
     * @param getter
     * @return R from first getter
     */
    public <R> R firstGetter(Function<T, R> getter) {
        return getter.apply(first);
    }

    /**
     * Calls passed getter from first element of couple
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
