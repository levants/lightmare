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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utility class to work with {@link java.util.Collection} and
 * {@link java.util.Map} implementations
 *
 * @author Levan Tsinadze
 */
public abstract class CollectionUtils {

    // First index of array
    public static final int FIRST = 0;

    // Second index of array
    public static final int SECOND = 1;

    // Length of empty array or set
    public static final int EMPTY = 0;

    // Length of array / collection / map with single element
    public static final int SINGLETON = 1;

    /**
     * Checks if passed {@link java.util.Collection} instance is not empty
     *
     * @param collection
     * @return <code>boolean</code> validation result
     */
    public static boolean notEmpty(Collection<?> collection) {
        return ObjectUtils.notTrue(collection.isEmpty());
    }

    /**
     * Checks passed {@link java.util.Collection} instance on <code>null</code>
     * and on emptiness returns true if it is not <code>null</code> and is not
     * empty
     *
     * @param collection
     * @return <code>boolean</code> validation result
     */
    public static boolean valid(Collection<?> collection) {
        return (Objects.nonNull(collection) && notEmpty(collection));
    }

    /**
     * Checks passed {@link java.util.Collection} instance on <code>null</code>
     * and emptiness and executes {@link java.util.function.Consumer}
     * implementation
     * 
     * @param collection
     * @param consumer
     */
    public static <E> void valid(Collection<E> collection, Consumer<Collection<E>> consumer) {
        ObjectUtils.valid(collection, CollectionUtils::valid, consumer);
    }

    /**
     * Checks passed {@link java.util.Map} instance on <code>null</code> and
     * emptiness
     *
     * @param map
     * @return <code>boolean</code> validation result
     */
    public static boolean valid(Map<?, ?> map) {
        return (Objects.nonNull(map) && ObjectUtils.notTrue(map.isEmpty()));
    }

    /**
     * Checks passed {@link java.util.Map} instance on <code>null</code> and
     * emptiness and executes {@link java.util.function.Consumer} implementation
     * 
     * @param map
     * @param consumer
     */
    public static <K, V> void valid(Map<K, V> map, Consumer<Map<K, V>> consumer) {
        ObjectUtils.valid(map, CollectionUtils::valid, consumer);
    }

    /**
     * Checks if passed {@link java.util.Map} instance is <code>null</code> or
     * is empty
     *
     * @param map
     * @return <code>boolean</code> validation result
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return !valid(map);
    }

    /**
     * Checks if passed {@link java.util.Collection} instance is
     * <code>null</code> or is empty
     *
     * @param collection
     * @return <code>boolean</code> validation result
     */
    public static boolean isEmpty(Collection<?> collection) {
        return !valid(collection);
    }

    /**
     * Checks if there is <code>null</code> or empty
     * {@link java.util.Collection} instance is passed collections
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
        return (Objects.nonNull(array) && Stream.of(array).allMatch(validator));
    }

    /**
     * Checks if each of passed {@link java.util.Map} instances is not
     * <code>null</code> and is not empty
     *
     * @param maps
     * @return <code>boolean</code> validation result
     */
    public static boolean validAll(Map<?, ?>... maps) {
        return validAll(maps, CollectionUtils::valid);
    }

    /**
     * Checks if passed array of {@link Object}'s instances is not
     * <code>null</code> and is not empty
     *
     * @param array
     * @return <code>boolean</code> validation result
     */
    public static boolean valid(Object[] array) {
        return (Objects.nonNull(array) && array.length > EMPTY);
    }

    /**
     * Checks passed array on <code>null</code> and emptiness and executes
     * {@link java.util.function.Consumer} implementation
     * 
     * @param array
     * @param consumer
     */
    public static <E> void valid(E[] array, Consumer<E[]> consumer) {
        ObjectUtils.valid(array, CollectionUtils::valid, consumer);
    }

    /**
     * Checks if passed array is <code>null</code> or empty
     *
     * @param array
     * @return <code>boolean</code> validation result
     */
    public static <T> boolean isEmpty(T[] array) {
        return (array == null || array.length == EMPTY);
    }

    /**
     * Checks if each of passed {@link java.util.Collection} instances is not
     * <code>null</code> and is not empty
     *
     * @param collections
     * @return <code>boolean</code> validation result
     */
    public static boolean validAll(Collection<?>... collections) {
        return validAll(collections, CollectionUtils::valid);
    }

    /**
     * Checks if each of passed {@link Object} array instances is not
     * <code>null</code> and is not empty
     *
     * @param arrays
     * @return <code>boolean</code> validation result
     */
    public static boolean validAll(Object[]... arrays) {
        return validAll(arrays, CollectionUtils::valid);
    }

    /**
     * Opposite to {@link java.util.Map#containsKey(Object)} method for passed
     * {@link java.util.Map} map and K key
     *
     * @param map
     * @param key
     * @return <code>boolean</code> validation result
     */
    public static <K, V> boolean notContains(Map<K, V> map, K key) {
        return (key == null || isEmpty(map) || ObjectUtils.notTrue(map.containsKey(key)));
    }

    /**
     * Opposite to {@link java.util.Collection#contains(Object)} method for
     * passed {@link java.util.Collection} collection and E element
     *
     * @param collection
     * @param element
     * @return <code>boolean</code> validation result
     */
    public static <E> boolean notContains(Collection<E> collection, E element) {
        return ((element == null || isEmpty(collection)) || ObjectUtils.notTrue(collection.contains(element)));
    }

    /**
     * Peaks first element from list or passed default value if list is
     * <code>null</code> or empty
     *
     * @param list
     * @param defaultValue
     * @return T first value of list or passed default value
     */
    private static <T> T getFirstFromList(List<T> list, T defaultValue) {

        T value;

        if (valid(list)) {
            value = list.get(FIRST);
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
     * @return T first element from {@link java.util.Collection} of default
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
     * @return T first element from {@link java.util.Collection} of default
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
     * Peaks first element from {@link java.util.Collection} instance
     *
     * @param collection
     * @return T first element or default value
     */
    public static <T> T getFirst(Collection<T> collection) {
        return getFirst(collection, null);
    }

    /**
     * Peaks first element from array or passed default value if array is
     * <code>null</code> or empty
     *
     * @param values
     * @param defaultValue
     * @return T first element or default value
     */
    public static <T> T getFirst(T[] values, T defaultValue) {

        T value;

        if (valid(values)) {
            value = values[FIRST];
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
     * Calls {@link java.util.function.BiConsumer} for each array member
     * 
     * @param array
     * @param consumer
     */
    private static <T> void forEachValue(T[] array, BiConsumer<Integer, T> consumer) {

        int length = array.length;
        T value;
        for (int i = FIRST; i < length; i++) {
            value = array[i];
            consumer.accept(i, value);
        }
    }

    /**
     * Validates array and calls {@link java.util.function.BiConsumer} for each
     * it's member
     * 
     * @param array
     * @param consumer
     */
    public static <T> void forEach(T[] array, BiConsumer<Integer, T> consumer) {

        if (valid(array)) {
            forEachValue(array, consumer);
        }
    }

    /**
     * Copies each element from passed array by mapped call to other array
     * 
     * @param from
     * @param to
     * @param mapper
     */
    public static <T, R> R[] map(T[] from, R[] to, Function<? super T, ? extends R> mapper) {

        int length = from.length;
        T value;
        R mapped;
        for (int i = CollectionUtils.FIRST; i < length; i++) {
            value = from[i];
            mapped = mapper.apply(value);
            to[i] = mapped;
        }

        return to;
    }

    /**
     * Gets first valid element from {@link java.util.Collection} for which
     * {@link java.util.function.Predicate} holds
     * 
     * @param collection
     * @param predicate
     * @return E first chosen element by {@link java.util.function.Predicate}
     */
    public static <E> E getFirstValid(Collection<E> collection, Predicate<E> predicate) {
        return collection.stream().filter(predicate::test).findFirst().get();
    }

    /**
     * Validates if array contains single object
     * 
     * @param array
     * @return <code>boolean</code> validation result
     */
    public static <T> boolean singleton(T[] array) {
        return (Objects.nonNull(array) && (array.length == SINGLETON));
    }
}
