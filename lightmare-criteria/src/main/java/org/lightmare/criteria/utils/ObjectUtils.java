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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Utility class to help with general object checks and modifications
 *
 * @author Levan Tsinadze
 */
public abstract class ObjectUtils {

    /**
     * Wraps exceptions and errors to avoid throwable
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            argument type
     * @param <E>
     *            error type
     */
    @FunctionalInterface
    public static interface ErrorWrapperConsumer<T, E extends Exception> {

        /**
         * Accepts passed value and throws appropriated exception
         * 
         * @param value
         * @throws E
         */
        void accept(T value) throws E;
    }

    /**
     * Wraps exceptions and errors to avoid throwable
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            result type
     * @param <E>
     *            error type
     */
    @FunctionalInterface
    public static interface ErrorWrapperSupplier<T, E extends Exception> {

        /**
         * Get value and throws appropriated exception
         * 
         * @param value
         * @throws E
         */
        T get() throws E;
    }

    /**
     * Calls consumer implementation and wraps errors
     * 
     * @param value
     * @param consumer
     */
    public static <T, E extends Exception> void acceptWrap(T value, ErrorWrapperConsumer<T, E> consumer) {

        try {
            consumer.accept(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Calls supplier implementation and wraps errors
     * 
     * @param supplier
     * @return T result from supplier
     */
    public static <T, E extends Exception> T getWrap(ErrorWrapperSupplier<T, E> supplier) {

        T result;

        try {
            result = supplier.get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }

    /**
     * Checks if passed boolean value is not true
     *
     * @param statement
     * @return <code>boolean</code>
     */
    public static boolean notTrue(boolean statement) {
        return !statement;
    }

    /**
     * Validates if passed {@link java.util.function.Predicate} is not
     * <code>null</code> returns <code>true</code> for passed value
     * 
     * @param predicate
     * @param value
     * @return <code>boolean</code> validation result
     */
    public static <T> boolean test(Predicate<T> predicate, T value) {
        return (Objects.nonNull(predicate) && predicate.test(value));
    }

    /**
     * Validates if passed {@link java.util.function.Predicate} returns
     * <code>true</code> for passed value and runs
     * {@link java.util.function.Consumer} implementation
     * 
     * @param value
     * @param predicate
     * @param consumer
     * @return <code>boolean</code> validation result
     */
    public static <T> boolean valid(T value, Predicate<T> predicate, Consumer<T> consumer) {

        boolean valid = (test(predicate, value) && Objects.nonNull(consumer));

        if (valid) {
            consumer.accept(value);
        }

        return valid;
    }

    /**
     * Validates if passed {@link java.util.function.Predicate} returns
     * <code>false</code> for passed value and runs
     * {@link java.util.function.Consumer} implementation
     * 
     * @param value
     * @param predicate
     * @param consumer
     * @return <code>boolean</code> validation result
     */
    public static <T> boolean invalid(T value, Predicate<T> predicate, Consumer<T> consumer) {

        boolean valid = (Objects.nonNull(predicate) && ObjectUtils.notTrue(predicate.test(value))
                && Objects.nonNull(consumer));

        if (valid) {
            consumer.accept(value);
        }

        return valid;
    }

    /**
     * Validates if passed {@link Object} is not <code>null</code> and runs
     * {@link java.util.function.Consumer} implementation
     * 
     * @param value
     * @param consumer
     * @return <code>boolean</code> validation result
     */
    public static <T> boolean nonNull(T value, Consumer<T> consumer) {
        return valid(value, Objects::nonNull, consumer);
    }

    /**
     * If value from {@link java.util.function.Supplier} is not
     * <code>null</code> then calls
     * {@link java.util.function.Consumer#accept(Object)} method
     * 
     * @param supplier
     * @param consumer
     */
    public static <T> void ifNotNull(Supplier<T> supplier, Consumer<T> consumer) {
        T value = supplier.get();
        nonNull(value, consumer);
    }

    /**
     * Validates if value provided by {@link java.util.function.Supplier} gives
     * valid {@link java.util.function.Predicate} results and calls passed
     * {@link java.util.function.Function} or else
     * {@link java.util.function.Function} implementations
     * 
     * @param supplier
     * @param predicate
     * @param function
     * @param elseFunction
     * @return T value from {@link java.util.function.Function}s
     */
    public static <K, T> T ifValid(Supplier<K> supplier, Predicate<K> predicate, Function<K, T> function,
            Function<K, T> elseFunction) {

        T result;

        K value = supplier.get();
        if (Objects.nonNull(function) && test(predicate, value)) {
            result = function.apply(value);
        } else {
            result = elseFunction.apply(value);
        }

        return result;
    }

    /**
     * If {@link java.util.function.Predicate#test(Object)} is valid for passed
     * value and calls {@link java.util.function.Function#apply(Object)} method
     * and returns it's result or calls other
     * {@link java.util.function.Function#apply(Object)} in an other case
     * 
     * @param value
     * @param predicate
     * @param function
     * @param elseFunction
     * @return <code>T</code> value from one {@link java.util.function.Function}
     *         or from other
     */
    public static <K, T> T ifIsValid(K value, Predicate<K> predicate, Function<K, T> function,
            Function<K, T> elseFunction) {
        return ifValid(() -> value, predicate, function, elseFunction);
    }

    /**
     * If {@link java.util.function.Predicate#test(Object)} is valid for passed
     * value and calls {@link java.util.function.Function#apply(Object)} method
     * and returns it's result or <code>null</code> in an other case
     * 
     * @param value
     * @param predicate
     * @param function
     * @return <code>T</code> value from {@link java.util.function.Function} or
     *         <code>null</code>
     */
    public static <K, T> T ifIsValid(K value, Predicate<K> predicate, Function<K, T> function) {
        return ifIsValid(value, predicate, function, c -> null);
    }

    /**
     * If value is not <code>null</code> then calls
     * {@link java.util.function.Function#apply(Object)} method and returns it's
     * result or <code>null</code> in an other case
     * 
     * @param value
     * @param function
     * @return <code>T</code> value from {@link java.util.function.Function} or
     *         <code>null</code>
     */
    public static <K, T> T ifIsNotNull(K value, Function<K, T> function) {
        return ifIsValid(value, Objects::nonNull, function);
    }

    /**
     * If passed {@link java.util.function.Supplier} provided value is not
     * <code>null</code> then calls
     * {@link java.util.function.Function#apply(Object)} method and returns it's
     * result or calls other {@link java.util.function.Function#apply(Object)}
     * in an other case
     * 
     * @param supplier
     * @param function
     * @param elseFunction
     * @return <code>T</code> value from one {@link java.util.function.Function}
     *         or from other
     */
    public static <K, T> T ifNonNull(Supplier<K> supplier, Function<K, T> function, Function<K, T> elseFunction) {
        return ifValid(supplier, Objects::nonNull, function, elseFunction);
    }

    /**
     * If passed {@link java.util.function.Supplier} provided value is not
     * <code>null</code> then calls
     * {@link java.util.function.Function#apply(Object)} method and returns it's
     * result or <code>null</code> in other case in an other case
     * 
     * @param supplier
     * @param function
     * @return <code>T</code> value from one {@link java.util.function.Function}
     *         or <code>null</code>
     */
    public static <K, T> T ifNonNull(Supplier<K> supplier, Function<K, T> function) {
        return ifNonNull(supplier, function, c -> null);
    }

    /**
     * Validates if value from {@link java.util.function.Supplier} returns
     * <code>null</code> value and calls
     * {@link java.util.function.Function#apply(Object)} method and returns it's
     * result or calls other {@link java.util.function.Function#apply(Object)}
     * in an other case
     * 
     * @param supplier
     * @param function
     * @param elseFunction
     * @return <code>T</code> value from one {@link java.util.function.Function}
     *         or from other
     */
    public static <K, T> T ifNull(Supplier<K> supplier, Function<K, T> function, Function<K, T> elseFunction) {
        return ifValid(supplier, c -> c == null, function, elseFunction);
    }

    /**
     * If value from {@link java.util.function.Supplier} is <code>null</code>
     * then calls {@link java.util.function.Function#apply(Object)} method and
     * returns it's result
     * 
     * @param supplier
     * @param function
     * @return <code>T</code> value from one {@link java.util.function.Function}
     */
    public static <T> T ifNull(Supplier<T> supplier, Function<T, T> function) {

        T result;

        T value = supplier.get();
        result = ifIsNull(value, function, c -> value);

        return result;
    }

    /**
     * Validates if value from passed value is <code>null</code> value and calls
     * {@link java.util.function.Function#apply(Object)} method and returns it's
     * result or calls other {@link java.util.function.Function#apply(Object)}
     * in an other case
     * 
     * @param value
     * @param function
     * @param elseFunction
     * @return <code>T</code> value from one {@link java.util.function.Function}
     *         or from other
     */
    public static <K, T> T ifIsNull(K value, Function<K, T> function, Function<K, T> elseFunction) {
        return ifNull(() -> value, function, elseFunction);
    }

    /**
     * Checks if not a single object passed objects is not <code>null</code>
     *
     * @param values
     * @return <code>boolean</code> validation result
     */
    public static boolean nonNullAll(Object... values) {
        return CollectionUtils.validAll(values, Objects::nonNull);
    }

    /**
     * Checks if parameters equals and calls
     * {@link java.util.function.BiConsumer#accept(Object, Object)} method
     * 
     * @param x
     * @param y
     * @param consumer
     */
    public static <X, Y> void equals(X x, Y y, BiConsumer<X, Y> consumer) {

        if (Objects.nonNull(consumer) && Objects.equals(x, y)) {
            consumer.accept(x, y);
        }
    }

    /**
     * Checks if parameters not equals
     *
     * @param x
     * @param y
     * @return <code>boolean</code>
     */
    public static <X, Y> boolean notEquals(X x, Y y) {
        return notTrue(Objects.equals(x, y));
    }

    /**
     * Checks if parameters not equals and calls
     * {@link java.util.function.BiConsumer#accept(Object, Object)} method
     *
     * @param x
     * @param y
     * @param consumer
     */
    public static <X, Y> void notEquals(X x, Y y, BiConsumer<X, Y> consumer) {

        if (Objects.nonNull(consumer) && notEquals(x, y)) {
            consumer.accept(x, y);
        }
    }

    /**
     * Casts passed {@link Object} to generic parameter
     *
     * @param instance
     * @return <code>T</code> casted to type instance
     */
    public static <T> T cast(Object instance) {
        @SuppressWarnings("unchecked")
        T value = (T) instance;
        return value;
    }

    /**
     * Validates if {@link java.util.function.Consumer} is not <code>null</code>
     * and if not calls {@link java.util.function.Consumer#accept(Object)}
     * method accepts for passed value
     * 
     * @param consumer
     * @param value
     */
    public static <T> void accept(Consumer<T> consumer, T value) {
        ObjectUtils.nonNull(consumer, c -> c.accept(value));
    }

    /**
     * Casts passed {@link Object} to generic parameter and runs
     * {@link java.util.function.Consumer} implementation
     * 
     * @param instance
     * @param type
     * @param consumer
     */
    private static <T> void castAndApply(Object instance, Class<T> type, Consumer<T> consumer) {
        T value = type.cast(instance);
        accept(consumer, value);
    }

    /**
     * Casts passed {@link Object} to generic parameter and if it's not
     * <code>null</code> runs {@link java.util.function.Consumer} implementation
     * 
     * @param instance
     * @param type
     * @param consumer
     */
    public static <T> void cast(Object instance, Class<T> type, Consumer<T> consumer) {
        nonNull(instance, c -> castAndApply(c, type, consumer));
    }

    /**
     * Casts passed {@link Object} to generic parameter if it's not
     * <code>null</code> and is instance of this parameter and runs
     * {@link java.util.function.Consumer} implementation
     * 
     * @param instance
     * @param type
     * @param consumer
     */
    public static <T> void castIfValid(Object instance, Class<T> type, Consumer<T> consumer) {

        if (Objects.nonNull(instance) && type.isInstance(instance)) {
            castAndApply(instance, type, consumer);
        }
    }

    /**
     * Serializes java {@link Object} to byte array with java native
     * serialization API
     * 
     * @param value
     * @return <code>byte</code> array serialized object
     */
    public static byte[] serialize(Object value) {
        return getWrap(() -> {

            byte[] bytes;

            try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bout)) {
                out.writeObject(value);
                bytes = bout.toByteArray();
            }

            return bytes;
        });
    }

    /**
     * For reads byte array as {@link Object} with java native serialization API
     * methods
     * 
     * @param bytes
     * @return {@link Object}
     */
    private static Object readToObject(byte[] bytes) {
        return getWrap(() -> {

            Object value;

            try (InputStream bin = new ByteArrayInputStream(bytes); ObjectInputStream in = new ObjectInputStream(bin)) {
                value = in.readObject();
            }

            return value;
        });
    }

    /**
     * For reads of byte array as java type appropriated type with java native
     * serialization API methods
     * 
     * @param bytes
     * @return {@link Object}
     */
    public static <T> T deserialize(byte[] bytes) {

        T value;

        Object raw = readToObject(bytes);
        value = ifIsNotNull(raw, ObjectUtils::cast);

        return value;
    }

    /**
     * Validates passed value on <code>null</code> and if it is returns
     * {@link java.util.function.Supplier} provided value
     * 
     * @param value
     * @param supplier
     * @return T value
     */
    public static <T> T thisOrDefault(T value, Supplier<T> supplier) {

        T result;

        if (value == null) {
            result = supplier.get();
        } else {
            result = value;
        }

        return result;
    }

    /**
     * Validates passed value on <code>null</code> and if it is returns
     * {@link java.util.function.Supplier} provided value and calls setter
     * {@link java.util.function.Consumer} implementation
     * 
     * @param value
     * @param supplier
     * @param setter
     * @return T value
     */
    public static <T> T thisOrDefault(T value, Supplier<T> supplier, Consumer<T> setter) {

        T result;

        if (value == null) {
            result = supplier.get();
            accept(setter, result);
        } else {
            result = value;
        }

        return result;
    }

    /**
     * Gets value from {@link java.util.function.Supplier} and validates on
     * <code>null</code> and if it is returns default
     * {@link java.util.function.Supplier} provided value
     * 
     * @param supplier
     * @param initSupplier
     * @return T value
     */
    public static <T> T getOrInit(Supplier<T> supplier, Supplier<T> initSupplier) {
        return ifNull(supplier, c -> initSupplier.get());
    }
}
