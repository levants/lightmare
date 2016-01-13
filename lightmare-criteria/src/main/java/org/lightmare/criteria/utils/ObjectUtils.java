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
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class to help with general object checks / lock / modification
 *
 * @author Levan Tsinadze
 */
public abstract class ObjectUtils {

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
     * Validates if passed {@link Object} ins not <code>null</code> and runs
     * {@link java.util.function.Consumer} implementation
     * 
     * @param value
     * @param consumer
     * @return <code>boolean</code>
     */
    public static <T> boolean nonNull(T value, Consumer<T> consumer) {

        boolean valid = Objects.nonNull(value);

        if (valid) {
            consumer.accept(value);
        }

        return valid;
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
     * Checks if not a single object passed objects is not <code>null</code>
     *
     * @param values
     * @return <code>boolean</code> validation result
     */
    public static boolean nonNullAll(Object... values) {
        return CollectionUtils.validAll(values, Objects::nonNull);
    }

    /**
     * Checks if parameters not equals
     *
     * @param x
     * @param y
     * @return <code>boolean</code>
     */
    public static <X, Y> boolean notEquals(X x, Y y) {
        return (!Objects.equals(x, y));
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
     * Serializes java type ({@link Object}) to byte array with java native
     * serialization API
     * 
     * @param value
     * @return <code>byte</code> array serialized object
     */
    public static byte[] serialize(Object value) {

        byte[] bytes;

        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bout)) {
            out.writeObject(value);
            bytes = bout.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return bytes;
    }

    /**
     * For reads byte array as {@link Object} with java native serialization API
     * methods
     * 
     * @param bytes
     * @return {@link Object}
     */
    private static Object readToObject(byte[] bytes) {

        Object value;

        try (InputStream bin = new ByteArrayInputStream(bytes); ObjectInputStream in = new ObjectInputStream(bin)) {
            value = in.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            throw new RuntimeException(ex);
        }

        return value;
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
        if (Objects.nonNull(raw)) {
            value = cast(raw);
        } else {
            value = null;
        }

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

        T result = supplier.get();

        if (result == null) {
            result = initSupplier.get();
        }

        return result;
    }
}
