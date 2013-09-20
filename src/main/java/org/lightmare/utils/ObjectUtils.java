package org.lightmare.utils;

import java.io.Closeable;
import java.io.IOException;

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
