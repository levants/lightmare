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
package org.lightmare.criteria.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utility class to work with {@link Collection} and {@link Map} implementations
 *
 * @author Levan Tsinadze
 */
public abstract class CollectionUtils {

    // First index of array
    public static final int FIRST_INDEX = 0;

    // Second index of array
    public static final int SECOND_INDEX = 1;

    // Index of not existing data in collection
    public static final int NOT_EXISTING_INDEX = -1;

    // Length of empty array
    public static final int EMPTY_ARRAY_LENGTH = 0;

    // Length of empty array or set
    public static final int EMPTY = 0;

    // Length of array / collection / map with single element
    public static final int SINGLTON_LENGTH = 1;

    // Empty array of objects
    public static final Object[] EMPTY_ARRAY = {};

    /**
     * Functional interface with exceptions on method
     * 
     * @author Levan Tsinadze
     * 
     * @param <T>
     *            method parameter type
     * @param <R>
     *            return type
     */
    @FunctionalInterface
    public static interface Mapper<T, R> {

        /**
         * Function to supply type mapping
         * 
         * @param value
         * @return R instance
         * @throws IOException
         */
        R apply(T value) throws IOException;
    }

    /**
     * Checks if passed {@link Collection} instance is not empty
     *
     * @param collection
     * @return <code>boolean</code> validation result
     */
    public static boolean notEmpty(Collection<?> collection) {
        return !collection.isEmpty();
    }

    /**
     * Checks passed {@link Collection} instance on null and on emptiness
     * returns true if it is not null and is not empty
     *
     * @param collection
     * @return <code>boolean</code> validation result
     */
    public static boolean valid(Collection<?> collection) {
        return (collection != null && !collection.isEmpty());
    }

    /**
     * Checks passed {@link Map} instance on null and emptiness returns true if
     * it is not null and is not empty
     *
     * @param map
     * @return <code>boolean</code> validation result
     */
    public static boolean valid(Map<?, ?> map) {
        return (map != null && !map.isEmpty());
    }

    /**
     * Checks if passed {@link Map} instance is null or is empty
     *
     * @param map
     * @return <code>boolean</code> validation result
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return !valid(map);
    }

    /**
     * Checks if passed {@link Collection} instance is null or is empty
     *
     * @param collection
     * @return <code>boolean</code> validation result
     */
    public static boolean isEmpty(Collection<?> collection) {
        return !valid(collection);
    }

    /**
     * Checks if there is null or empty {@link Collection} instance is passed
     * collections
     *
     * @param collections
     * @return <code>boolean</code> validation result
     */
    public static boolean invalidAll(Collection<?>... collections) {
        return !valid(collections);
    }

    /**
     * Validates elements of array
     * 
     * @param array
     * @param validator
     * @return <code>boolean</code> validation result
     */
    public static <T> boolean validAll(T[] array, Predicate<T> validator) {

        boolean valid = Objects.nonNull(array);

        if (valid) {
            valid = Stream.of(array).allMatch(validator);
        }

        return valid;
    }

    /**
     * Checks if each of passed {@link Map} instances is not null and is not
     * empty
     *
     * @param maps
     * @return <code>boolean</code> validation result
     */
    public static boolean validAll(Map<?, ?>... maps) {
        return validAll(maps, CollectionUtils::valid);
    }

    /**
     * Checks if passed array of {@link Object}'s instances is not null and is
     * not empty
     *
     * @param array
     * @return <code>boolean</code> validation result
     */
    public static boolean valid(Object[] array) {
        return (array != null && array.length > EMPTY_ARRAY_LENGTH);
    }

    /**
     * Checks if passed array is null or empty
     *
     * @param array
     * @return <code>boolean</code> validation result
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == EMPTY;
    }

    /**
     * Checks if each of passed {@link Collection} instances is not null and is
     * not empty
     *
     * @param collections
     * @return <code>boolean</code> validation result
     */
    public static boolean validAll(Collection<?>... collections) {
        return validAll(collections, CollectionUtils::valid);
    }

    /**
     * Checks if each of passed {@link Object} array instances is not null and
     * is not empty
     *
     * @param arrays
     * @return <code>boolean</code> validation result
     */
    public static boolean validAll(Object[]... arrays) {
        return validAll(arrays, CollectionUtils::valid);
    }

    /**
     * Opposite to {@link Map#containsKey(Object)} method for passed {@link Map}
     * map and K key
     *
     * @param map
     * @param key
     * @return <code>boolean</code> validation result
     */
    public static <K, V> boolean notContains(Map<K, V> map, K key) {
        return (key == null || isEmpty(map) || ObjectUtils.notTrue(map.containsKey(key)));
    }

    /**
     * Opposite to {@link Collection#contains(Object)} method for passed
     * {@link Collection} collection and E element
     *
     * @param collection
     * @param element
     * @return <code>boolean</code> validation result
     */
    public static <E> boolean notContains(Collection<E> collection, E element) {
        return ((element == null || isEmpty(collection)) || ObjectUtils.notTrue(collection.contains(element)));
    }

    /**
     * Peaks first element from list or passed default value if list is null or
     * empty
     *
     * @param list
     * @param defaultValue
     * @return T first value of list or passed default value
     */
    private static <T> T getFirstFromList(List<T> list, T defaultValue) {

        T value;

        if (valid(list)) {
            value = list.get(FIRST_INDEX);
        } else {
            value = defaultValue;
        }

        return value;
    }

    /**
     * Peaks first element from collection
     * 
     * @param collection
     * @param defaultValue
     * @return T first element from {@link Collection} of default
     */
    private static <T> T getFirstFromCollection(Collection<T> collection, T defaultValue) {

        T value;

        if (collection instanceof List<?>) {
            List<T> list = ObjectUtils.cast(collection);
            value = getFirstFromList(list, defaultValue);
        } else {
            value = collection.iterator().next();
        }

        return value;
    }

    /**
     * Peaks first element from collection
     *
     * @param collection
     * @return T first element from {@link Collection} of default
     */
    public static <T> T getFirst(Collection<T> collection, T defaultValue) {

        T value;

        if (valid(collection)) {
            value = getFirstFromCollection(collection, defaultValue);
        } else {
            value = defaultValue;
        }

        return value;
    }

    /**
     * Peaks first element from collection
     *
     * @param collection
     * @return T first element or default value
     */
    public static <T> T getFirst(Collection<T> collection) {
        return getFirst(collection, null);
    }

    /**
     * Peaks first element from array or passed default value if array is null
     * or empty
     *
     * @param values
     * @param defaultValue
     * @return T first element or default value
     */
    public static <T> T getFirst(T[] values, T defaultValue) {

        T value;

        if (valid(values)) {
            value = values[FIRST_INDEX];
        } else {
            value = defaultValue;
        }

        return value;
    }

    /**
     * Peaks first element from array
     *
     * @param values
     * @return T first element of array
     */
    public static <T> T getFirst(T[] values) {
        return getFirst(values, null);
    }

    /**
     * Copies passed array's each element after mapped call to other array
     * 
     * @param from
     * @param to
     * @param mapper
     * @throws IOException
     */
    public static <T, R> void map(T[] from, R[] to, Mapper<? super T, ? extends R> mapper) throws IOException {

        int length = from.length;
        T value;
        R mapped;
        for (int i = CollectionUtils.FIRST_INDEX; i < length; i++) {
            value = from[i];
            mapped = mapper.apply(value);
            to[i] = mapped;
        }
    }

    /**
     * Gets first valid element from {@link Collection} for which
     * {@link Predicate} holds
     * 
     * @param collection
     * @param predicate
     * @return E first chosen element by {@link Predicate}
     */
    public static <E> E getFirstValid(Collection<E> collection, Predicate<E> predicate) {
        return collection.stream().filter(c -> predicate.test(c)).findFirst().get();
    }

    /**
     * Validates if array contains single object
     * 
     * @param array
     * @return <code>boolean</code> validation result
     */
    public static <T> boolean singleton(T[] array) {
        return (Objects.nonNull(array) && (array.length == SINGLTON_LENGTH));
    }
}
