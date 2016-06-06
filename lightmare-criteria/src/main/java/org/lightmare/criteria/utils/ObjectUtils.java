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
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Utility class to help with general object checks and modifications
 *
 * @author Levan Tsinadze
 */
public abstract class ObjectUtils extends FunctionUtils {

    /**
     * Checks if passed boolean value is not true
     *
     * @param statement
     * @return <code>boolean</code> validation result
     */
    public static boolean notTrue(boolean statement) {
        return !statement;
    }

    /**
     * Validates if passed {@link java.util.function.BooleanSupplier} returns
     * <code>true</code> and runs
     * {@link org.lightmare.criteria.utils.Functions.Command} implementation
     * 
     * @param predicate
     * @param consumer
     * @return <code>boolean</code> validation result
     */
    public static boolean valid(BooleanSupplier predicate, Command consumer) {

        boolean valid = getAsBoolean(predicate);

        if (valid) {
            execute(consumer);
        }

        return valid;
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

        boolean valid = test(predicate, value);

        if (valid) {
            accept(consumer, value);
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

        boolean valid = reverceTest(predicate, value);

        if (valid) {
            accept(consumer, value);
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
        T value = get(supplier);
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

        K value = get(supplier);
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
     * If passed {@link java.util.function.Function} result on passed value is
     * not <code>null</code> then calls
     * {@link java.util.function.Function#apply(Object)} method and returns it's
     * result or <code>null</code> in other case in an other case
     * 
     * @param value
     * @param supplier
     * @param function
     * @return <code>T</code> value from one {@link java.util.function.Function}
     *         or <code>null</code>
     */
    public static <V, R, T> T applyNonNull(V value, Function<V, R> supplier, Function<R, T> function) {
        return ifNonNull(() -> supplier.apply(value), function);
    }

    /**
     * If passed {@link java.util.function.Function} result on passed value is
     * not <code>null</code> then calls
     * {@link java.util.function.Function#apply(Object)} method and returns it's
     * result or calls other {@link java.util.function.Function#apply(Object)}
     * in an other case
     * 
     * @param value
     * @param supplier
     * @param function
     * @param elseFunction
     * @return <code>T</code> value from one {@link java.util.function.Function}
     *         or from other
     */
    public static <V, R, T> T applyNonNull(V value, Function<V, R> supplier, Function<R, T> function,
            Function<R, T> elseFunction) {
        return ifNonNull(() -> supplier.apply(value), function, elseFunction);
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

        T value = get(supplier);
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
     * {@link java.util.function.BiConsumer} implementation
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
     * {@link java.util.function.BiConsumer} implementation
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
     * Gets value from supplier and cast to appropriated value
     * 
     * @param supplier
     * @return <code>T</code> casted to type instance
     */
    public static <T, R> T getAndCast(Supplier<R> supplier) {

        T result;

        R value = get(supplier);
        result = cast(value);

        return result;
    }

    /**
     * Gets value from supplier and cast to appropriated value
     * 
     * @param argument
     * @param function
     * @return <code>T</code> casted to type instance
     */
    public static <T, U, R> T applyAndCast(U argument, Function<U, R> function) {

        T result;

        R value = apply(argument, function);
        result = cast(value);

        return result;
    }

    /**
     * Casts passed {@link Object} to generic parameter and runs
     * {@link java.util.function.Consumer} implementation
     * 
     * @param instance
     * @param type
     * @param consumer
     */
    private static <T> void castAndAccept(Object instance, Class<T> type, Consumer<T> consumer) {
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
        nonNull(instance, c -> castAndAccept(c, type, consumer));
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
            castAndAccept(instance, type, consumer);
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
        return applyQuietly(value, c -> {

            byte[] bytes;

            try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bout)) {
                out.writeObject(c);
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
        return applyQuietly(bytes, c -> {

            Object value;

            try (InputStream bin = new ByteArrayInputStream(c); ObjectInputStream in = new ObjectInputStream(bin)) {
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
            result = get(supplier);
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
            result = get(supplier);
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
        return ifNull(supplier, c -> get(initSupplier));
    }

    /**
     * Calls {@link java.util.function.Function} for passed argument and
     * validates on <code>null</code> and if it is returns default
     * {@link java.util.function.Function} provided value
     * 
     * @param argument1
     * @param argument2
     * @param supplier
     * @param initSupplier
     * @return T value
     */
    public static <T, R> R callOrInit(T argument1, T argument2, Function<T, R> supplier, Function<T, R> initSupplier) {

        R result = apply(argument1, supplier);

        if (result == null) {
            result = apply(argument2, initSupplier);
        }

        return result;
    }

    /**
     * Calls {@link java.util.function.Function} for passed argument and
     * validates on <code>null</code> and if it is returns default
     * {@link java.util.function.Function} provided value
     * 
     * @param argument
     * @param supplier
     * @param initSupplier
     * @return T value
     */
    public static <T, R> R callOrInit(T argument, Function<T, R> supplier, Function<T, R> initSupplier) {
        return callOrInit(argument, argument, supplier, initSupplier);
    }
}
