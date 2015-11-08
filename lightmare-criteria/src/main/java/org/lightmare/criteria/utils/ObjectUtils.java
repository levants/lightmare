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
     * Checks if not a single object passed objects is not null
     *
     * @param values
     * @return <code>boolean</code> validation result
     */
    public static boolean notNullAll(Object... values) {
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
     * @param data
     * @return <code>T</code> casted to type instance
     */
    public static <T> T cast(Object data) {

	@SuppressWarnings("unchecked")
	T value = (T) data;

	return value;
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

	byte[] data;

	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	ObjectOutputStream out = new ObjectOutputStream(stream);
	try {
	    out.writeObject(value);
	    data = stream.toByteArray();
	} finally {
	    IOUtils.closeAll(stream, out);
	}

	return data;
    }

    /**
     * For de - serialization of byte array in java type ({@link Object}) with
     * java native serialization API
     * 
     * @param data
     * @return {@link Object}
     * @throws IOException
     */
    public static Object deserialize(byte[] data) throws IOException {

	Object value;

	try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
	    value = in.readObject();
	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	}

	return value;
    }
}
