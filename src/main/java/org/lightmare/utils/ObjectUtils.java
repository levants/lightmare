package org.lightmare.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lightmare.utils.reflect.MetaUtils;

/**
 * Utility class to help with general object checks
 * 
 * @author levan
 * 
 */
public class ObjectUtils {

    public static final int NOT_EXISTING_INDEX = -1;

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
	    for (int i = 0; i < length && valid; i++) {
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
     * Checks if passed objjects are not null and not equals each other
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

	Class<T> wrapper = MetaUtils.getWrapper(castClass);

	T value = wrapper.cast(data);

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
	    ReentrantLock reentrantLock = ObjectUtils.cast(lock,
		    ReentrantLock.class);
	    if (reentrantLock.isHeldByCurrentThread()) {
		lock.unlock();
	    }
	} else {
	    lock.unlock();
	}
    }

    /**
     * Checks if passed {@link Closeable} instance is not null and if not calls
     * {@link Closeable#close()} method
     * 
     * @param closeable
     * @throws IOException
     */
    public static void close(Closeable closeable) throws IOException {

	if (ObjectUtils.notNull(closeable)) {
	    closeable.close();
	}
    }

    /**
     * Checks if passed array of {@link Closeable}'s is valid and closes all of
     * them
     * 
     * @param closeables
     * @throws IOException
     */
    public static void closeAll(Closeable... closeables) throws IOException {

	if (CollectionUtils.valid(closeables)) {
	    for (Closeable closeable : closeables) {
		close(closeable);
	    }
	}
    }
}
