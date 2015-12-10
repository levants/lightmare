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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;
import java.util.function.Consumer;

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
     * Validates if passed {@link Object} ins not null and runs consumer
     * implementation
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
     * Checks if not a single object passed objects is not null
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
     * Validates if consumer is not null and accepts it
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
     * Casts passed {@link Object} to generic parameter and if it's not null
     * runs {@link java.util.function.Consumer} implementation
     * 
     * @param instance
     * @param type
     * @param consumer
     */
    public static <T> void cast(Object instance, Class<T> type, Consumer<T> consumer) {
        nonNull(instance, c -> castAndApply(c, type, consumer));
    }

    /**
     * Casts passed {@link Object} to generic parameter if it's not null and is
     * instance of this parameter and runs {@link java.util.function.Consumer}
     * implementation
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
     * @return <code>byte</code>[] serialized object
     * @throws IOException
     */
    public static byte[] serialize(Object value) throws IOException {

        byte[] bytes;

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(stream)) {
            out.writeObject(value);
            bytes = stream.toByteArray();
        }

        return bytes;
    }

    /**
     * For de - serialization of byte array in java type ({@link Object}) with
     * java native serialization API methods
     * 
     * @param bytes
     * @return {@link Object}
     * @throws IOException
     */
    public static Object deserialize(byte[] bytes) throws IOException {

        Object value;

        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            value = in.readObject();
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        }

        return value;
    }

    /**
     * For de - serialization of byte array in java type appropriated type with
     * java native serialization API methods
     * 
     * @param bytes
     * @return {@link Object}
     * @throws IOException
     */
    public static <T> T deserializeToType(byte[] bytes) throws IOException {

        T value;

        Object raw = deserialize(bytes);
        value = cast(raw);

        return value;
    }
}
