package org.lightmare.utils.reflect;

import org.lightmare.utils.ObjectUtils;

/**
 * Container class for primitives
 * 
 * @author Levan Tsinadze
 *
 */
public class Primitives {

    // default values for primitives
    public static final byte BYTE_DEF = 0;

    public static final boolean BOOLEAN_DEF = Boolean.FALSE;

    public static final char CHAR_DEF = '\u0000';

    public static final short SHORT_DEF = 0;

    public static final int INT_DEF = 0;

    public static final long LONG_DEF = 0L;

    public static final float FLOAT_DEF = 0F;

    public static final double DOUBLE_DEF = 0D;

    /**
     * Returns default values if passed class is primitive else returns null
     * 
     * @param clazz
     * @return Object
     */
    public static Object getDefault(Class<?> clazz) {

	Object value;

	if (clazz.isPrimitive()) {

	    if (clazz.equals(byte.class)) {
		value = BYTE_DEF;
	    } else if (clazz.equals(boolean.class)) {
		value = BOOLEAN_DEF;
	    } else if (clazz.equals(char.class)) {
		value = CHAR_DEF;
	    } else if (clazz.equals(short.class)) {
		value = SHORT_DEF;
	    } else if (clazz.equals(int.class)) {
		value = INT_DEF;
	    } else if (clazz.equals(long.class)) {
		value = LONG_DEF;
	    } else if (clazz.equals(float.class)) {
		value = FLOAT_DEF;
	    } else if (clazz.equals(double.class)) {
		value = DOUBLE_DEF;
	    } else {
		value = null;
	    }
	} else {
	    value = null;
	}

	return value;
    }

    /**
     * Gets wrapper class if passed class is a primitive type
     * 
     * @param type
     * @return {@link Class}<T>
     */
    public static <T> Class<T> getWrapper(Class<?> type) {

	Class<T> wrapper;

	if (type.isPrimitive()) {
	    if (type.equals(byte.class)) {
		wrapper = ObjectUtils.cast(Byte.class);
	    } else if (type.equals(boolean.class)) {
		wrapper = ObjectUtils.cast(Boolean.class);
	    } else if (type.equals(char.class)) {
		wrapper = ObjectUtils.cast(Character.class);
	    } else if (type.equals(short.class)) {
		wrapper = ObjectUtils.cast(Short.class);
	    } else if (type.equals(int.class)) {
		wrapper = ObjectUtils.cast(Integer.class);
	    } else if (type.equals(long.class)) {
		wrapper = ObjectUtils.cast(Long.class);
	    } else if (type.equals(float.class)) {
		wrapper = ObjectUtils.cast(Float.class);
	    } else if (type.equals(double.class)) {
		wrapper = ObjectUtils.cast(Double.class);
	    } else {
		wrapper = ObjectUtils.cast(type);
	    }
	} else {
	    wrapper = ObjectUtils.cast(type);
	}

	return wrapper;
    }
}
