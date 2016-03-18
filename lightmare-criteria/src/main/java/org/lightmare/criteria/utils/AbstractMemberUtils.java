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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Abstract class for class members
 * 
 * @author Levan Tsinadze
 *
 */
abstract class AbstractMemberUtils extends AbstractClassUtils {

    /**
     * Supplier for find member method
     * 
     * @author Levan Tsinadze
     *
     * @param <M>
     *            member type
     * @param <E>
     *            exception type
     */
    @FunctionalInterface
    private static interface MemberSupplier<M extends Member> {

        /**
         * Function method to get appropriated {@link java.lang.reflect.Member}
         * from {@link Class} by name
         * 
         * @param type
         * @param memberName
         * @return {@link java.lang.reflect.Member} from {@link Class}
         * @throws NoSuchMethodException
         * @throws NoSuchFieldException
         * @throws SecurityException
         */
        M getMember(Class<?> type, String memberName)
                throws NoSuchMethodException, NoSuchFieldException, SecurityException;
    }

    /**
     * Tuple for {@link java.lang.reflect.Member} and appropriated {@link Class}
     * instance
     * 
     * @author Levan Tsinadze
     *
     * @param <T>
     *            member type parameter
     */
    private static class MemberTuple<T extends Member> {

        private T member;

        private Class<?> type;

        private final String memberName;

        private MemberTuple(Class<?> type, final String memberName) {
            this.type = type;
            this.memberName = memberName;
        }

        private static <T extends Member> MemberTuple<T> of(Class<?> type, final String memberName) {
            return new MemberTuple<T>(type, memberName);
        }

        private boolean valid() {
            return ((this.member == null) && Objects.nonNull(this.type));
        }

        private void setSuperType() {
            type = ObjectUtils.ifIsNull(member, c -> type.getSuperclass(), t -> type);
        }

        private T getMember() {
            return member;
        }
    }

    /**
     * Sets parameters for next validation and iteration
     * 
     * @param tuple
     * @param supplier
     */
    private static <T extends Member> void iterate(MemberTuple<T> tuple, MemberSupplier<T> supplier) {

        try {
            tuple.member = supplier.getMember(tuple.type, tuple.memberName);
        } catch (NoSuchMethodException | NoSuchFieldException ex) {
            tuple.setSuperType();
        }
    }

    /**
     * Finds {@link java.lang.reflect.Member} in passed {@link Class} or it's
     * parents
     * 
     * @param type
     * @param memberName
     * @param supplier
     * @return {@link org.lightmare.criteria.utils.AbstractMemberUtils.MemberTuple}
     *         in type hierarchy
     */
    private static <T extends Member> MemberTuple<T> findMember(Class<?> type, String memberName,
            MemberSupplier<T> supplier) {

        MemberTuple<T> tuple = MemberTuple.of(type, memberName);

        while (tuple.valid()) {
            iterate(tuple, supplier);
        }

        return tuple;
    }

    /**
     * Finds {@link java.lang.reflect.Method} and containing parent
     * {@link Class} instance
     * 
     * @param type
     * @param methodName
     * @param parameters
     * @return {@link org.lightmare.criteria.utils.AbstractMemberUtils.MemberTuple}
     *         for {@link java.lang.reflect.Method}
     */
    private static MemberTuple<Method> findMethodAndType(Class<?> type, String methodName, Class<?>... parameters) {
        return findMember(type, methodName, (t, m) -> t.getDeclaredMethod(m, parameters));
    }

    /**
     * Finds passed {@link Class}'s or one of it's super-classes
     * {@link java.lang.reflect.Method} with appropriated name and parameters
     * 
     * @param type
     * @param methodName
     * @param parameters
     * @return {@link java.lang.reflect.Method} for type
     */
    public static Method findMethod(Class<?> type, String methodName, Class<?>... parameters) {

        Method method;

        MemberTuple<Method> tuple = findMethodAndType(type, methodName, parameters);
        method = tuple.getMember();

        return method;
    }

    /**
     * Finds passed {@link Class}'s or one of th's superclass
     * {@link java.lang.reflect.Field} with appropriated name
     * 
     * @param type
     * @param fieldName
     * @return {@link java.lang.reflect.Field} for type
     */
    public static Field findField(Class<?> type, String fieldName) {

        Field field;

        MemberTuple<Field> tuple = findMember(type, fieldName, Class::getDeclaredField);
        field = tuple.getMember();

        return field;
    }

    /**
     * Creates new instance for passed {@link Class} without throws declaration
     * 
     * @param type
     * @return T new instance of {@link Class}
     */
    public static <T> T newInstance(Class<T> type) {
        return ObjectUtils.apply(type, c -> {

            T instance;

            Constructor<T> constructor = c.getConstructor();
            makeAccessible(constructor);
            instance = constructor.newInstance();

            return instance;
        });
    }

    /**
     * Sets appropriated value to {@link java.lang.reflect.Field} of object
     * instance
     * 
     * @param field
     * @param instance
     * @param value
     */
    public static void set(Field field, Object instance, Object value) {
        ObjectUtils.call(instance, value, field::set);
    }

    /**
     * Common method to invoke {@link java.lang.reflect.Method} with reflection
     *
     * @param method
     * @param instance
     * @param arguments
     * @return {@link Object} result by {@link java.lang.reflect.Method}
     *         invocation
     */
    public static <T> T invoke(Method method, Object instance, Object... arguments) {

        T value;

        try {
            makeAccessible(method);
            Object raw = method.invoke(instance, arguments);
            value = ObjectUtils.cast(raw);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw unwrap(ex);
        }

        return value;
    }
}
