/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
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
package org.lightmare.utils.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.lightmare.utils.IOUtils;

/**
 * Reads write java objects from {@link Byte} array with java native
 * serialization
 * 
 * @author Levan Tsinadze
 * @since 0.0.62-SNAPSHO
 */
public abstract class NativeSerializer {

    /**
     * Serializes java type ({@link Object}) to byte array with java native
     * serialization API
     * 
     * @param value
     * @return byte[]
     * @throws IOException
     */
    public static byte[] serialize(Object value) throws IOException {

	byte[] data;

	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	ObjectOutputStream objectStream = new ObjectOutputStream(stream);

	try {
	    objectStream.writeObject(value);
	    data = stream.toByteArray();
	} finally {
	    IOUtils.closeAll(stream, objectStream);
	}

	return data;
    }

    /**
     * For deserialization of byte array in java type ({@link Object}) with java
     * native serialization API
     * 
     * @param data
     * @return {@link Object}
     * @throws IOException
     */
    public static Object deserialize(byte[] data) throws IOException {

	Object value;

	ByteArrayInputStream stream = new ByteArrayInputStream(data);
	ObjectInputStream objectStream = new ObjectInputStream(stream);

	try {
	    value = objectStream.readObject();
	} catch (ClassNotFoundException ex) {
	    throw new IOException(ex);
	} finally {
	    IOUtils.closeAll(stream, objectStream);
	}

	return value;
    }
}
