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

/**
 * Container class for primitives
 * 
 * @author Levan Tsinadze
 *
 */
abstract class Primitives {

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
     * Gets default primitive
     * 
     * @param clazz
     * @return {@link Object} default instance of class
     */
    private static Object getPrimitive(Class<?> type) {

        Object value;

        if (type.equals(byte.class)) {
            value = BYTE_DEF;
        } else if (type.equals(boolean.class)) {
            value = BOOLEAN_DEF;
        } else if (type.equals(char.class)) {
            value = CHAR_DEF;
        } else if (type.equals(short.class)) {
            value = SHORT_DEF;
        } else if (type.equals(int.class)) {
            value = INT_DEF;
        } else if (type.equals(long.class)) {
            value = LONG_DEF;
        } else if (type.equals(float.class)) {
            value = FLOAT_DEF;
        } else if (type.equals(double.class)) {
            value = DOUBLE_DEF;
        } else {
            value = null;
        }

        return value;
    }

    /**
     * Returns default values if passed class is primitive else returns
     * <code>null</code>
     * 
     * @param type
     * @return {@link Object} default instance of class
     */
    public static Object getDefault(Class<?> type) {
        return ObjectUtils.ifValid(type, c -> c.isPrimitive(), Primitives::getPrimitive);
    }

    /**
     * Gets wrapper class if passed class is a primitive type
     * 
     * @param type
     * @return {@link Class}<T> wrapper
     */
    private static <T> Class<T> getPrimitiveWrapper(Class<?> type) {

        Class<T> wrapper;

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

        return wrapper;
    }

    /**
     * Gets wrapper class if passed class is a primitive type
     * 
     * @param type
     * @return {@link Class}<T> wrapper
     */
    public static <T> Class<T> getWrapper(Class<?> type) {

        Class<T> wrapper;

        if (type.isPrimitive()) {
            wrapper = getPrimitiveWrapper(type);
        } else {
            wrapper = ObjectUtils.cast(type);
        }

        return wrapper;
    }
}
