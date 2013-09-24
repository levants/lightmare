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
 * @author Levan
 * 
 */
public abstract class NativeSerializer {

    /**
     * Serializes java type ({@link Object}) to byte array with java native
     * serialization api
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
     * Deserializes byte array in java type ({@link Object}) with java native
     * serialization api
     * 
     * @param data
     * @return {@link Object}
     * @throws IOException
     */
    public static Object deserialize(byte[] data) throws IOException {

	ByteArrayInputStream stream = new ByteArrayInputStream(data);
	ObjectInputStream objectStream = new ObjectInputStream(stream);
	try {

	    Object value = objectStream.readObject();

	    return value;

	} catch (ClassNotFoundException ex) {

	    throw new IOException(ex);

	} finally {
	    IOUtils.closeAll(stream, objectStream);
	}
    }
}
