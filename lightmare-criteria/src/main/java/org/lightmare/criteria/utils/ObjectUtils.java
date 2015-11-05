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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
     * Checks if passed object is not null
     *
     * @param data
     * @return <code>boolean</code>
     */
    public static boolean notNull(Object data) {
	return (data != null);
    }

    /**
     * Checks if not a single object passed objects is not null
     *
     * @param datas
     * @return <code>boolean</code>
     */
    public static boolean notNullAll(Object... datas) {

	boolean valid = notNull(datas);

	if (valid) {
	    int length = datas.length;
	    Object data;
	    for (int i = CollectionUtils.FIRST_INDEX; i < length && valid; i++) {
		data = datas[i];
		valid = notNull(data);
	    }
	}

	return valid;
    }

    /**
     * Checks if parameters not equals
     *
     * @param x
     * @param y
     * @return <code>boolean</code>
     */
    public static <X, Y> boolean notEquals(X x, Y y) {
	return (!x.equals(y));
    }

    /**
     * Checks if passed {@link Object}s are not null and not equals each other
     *
     * @param data1
     * @param data2
     * @return <code>boolean</code>
     */
    public static boolean notNullNotEquals(Object data1, Object data2) {
	return notNullAll(data1, data2) && notEquals(data1, data2);
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
     * Simply locks passed {@link Lock} object
     *
     * @param lock
     */
    public static void lock(Lock lock) {
	lock.lock();
    }

    /**
     * Locks passed {@link Lock} object for passed time in appropriate
     * {@link TimeUnit} instance
     *
     * @param lock
     * @return <code>boolean</code>
     * @throws InterruptedException
     */
    public static boolean tryLock(Lock lock, Long time, TimeUnit unit) throws IOException {

	boolean locked;

	try {
	    locked = lock.tryLock(time, unit);
	} catch (InterruptedException ex) {
	    throw new IOException(ex);
	}

	return locked;
    }

    /**
     * Locks passed {@link Lock} object
     *
     * @param lock
     * @return <code>boolean</code>
     * @throws IOException
     */
    public static boolean tryLock(Lock lock) {
	return lock.tryLock();
    }

    /**
     * Unlocks passed {@link Lock} instance
     *
     * @param lock
     */
    public static void unlock(Lock lock) {

	if (lock instanceof ReentrantLock) {
	    // if passed lock instance of ReentrantLock then first defines if
	    // locked by current thread
	    ReentrantLock reentrantLock = ObjectUtils.cast(lock);
	    if (reentrantLock.isHeldByCurrentThread()) {
		lock.unlock();
	    }
	} else {
	    lock.unlock();
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
     * For de - serialization of byte array in java type ({@link Object}) with
     * java native serialization API
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
