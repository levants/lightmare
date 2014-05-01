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
package org.lightmare.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lightmare.utils.reflect.MetaUtils;

/**
 * Utility class to help with general object checks / lock / modification
 * 
 * @author Levan Tsinadze
 * @since 0.0.34-SNAPSHOT
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

	boolean valid = !x.equals(y);

	return valid;
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
     * Cats passed {@link Object} to generic parameter
     * 
     * @param data
     * @return <code>T</code>
     */
    public static <T> T cast(Object data) {

	@SuppressWarnings("unchecked")
	T value = (T) data;

	return value;
    }

    /**
     * Cats passed {@link Object} to generic parameter
     * 
     * @param data
     * @param castClass
     * @return <code>T</code>
     */
    public static <T> T cast(Object data, Class<T> castClass) {

	T value;

	Class<T> wrapper = MetaUtils.getWrapper(castClass);
	value = wrapper.cast(data);

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
    public static boolean tryLock(Lock lock, Long time, TimeUnit unit)
	    throws IOException {

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
	    ReentrantLock reentrantLock = ObjectUtils.cast(lock,
		    ReentrantLock.class);
	    if (reentrantLock.isHeldByCurrentThread()) {
		lock.unlock();
	    }
	} else {
	    lock.unlock();
	}
    }
}
