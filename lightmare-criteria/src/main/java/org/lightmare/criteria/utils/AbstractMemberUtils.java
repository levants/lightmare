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

import java.io.IOException;
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
     * Validates for next iteration class member search methods
     * 
     * @param type
     * @param member
     * @return <code>boolean</code> validation result
     */
    private static boolean validate(Member member, Class<?> type) {
        return ((member == null) && Objects.nonNull(type));
    }

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
    private static interface MemberSupplier<M extends Member, E extends ReflectiveOperationException> {

        /**
         * Function method to get appropriated {@link Member} from {@link Class}
         * by name
         * 
         * @param type
         * @param memberName
         * @return {@link Member} from {@link Class}
         * @throws E
         * @throws SecurityException
         */
        M getMember(Class<?> type, String memberName) throws E, SecurityException;
    }

    /**
     * Gets super class for passed class for instant exception
     * 
     * @param type
     * @param ex
     * @return {@link Class} super class for passed class
     * @throws IOException
     */
    private static <E extends ReflectiveOperationException> Class<?> getSuperType(Class<?> type, E ex)
            throws IOException {

        Class<?> superType;

        if ((ex instanceof NoSuchMethodException) || (ex instanceof NoSuchFieldException)) {
            superType = type.getSuperclass();
        } else {
            throw new IOException(ex);
        }

        return superType;
    }

    /**
     * Field {@link Member} in passed {@link Class} or it's parents
     * 
     * @param type
     * @param memberName
     * @param supplier
     * @return {@link Member} in type hierarchy
     * @throws IOException
     */
    private static <T extends Member, E extends ReflectiveOperationException> T findMember(Class<?> type,
            String memberName, MemberSupplier<T, E> supplier) throws IOException {

        T member = null;

        Class<?> superType = type;
        while (validate(member, superType)) {
            try {
                member = supplier.getMember(type, memberName);
            } catch (ReflectiveOperationException ex) {
                superType = getSuperType(superType, ex);
            } catch (SecurityException ex) {
                throw new IOException(ex);
            }
        }

        return member;
    }

    /**
     * Finds passed {@link Class}'s or one of it's super-classes {@link Method}
     * with appropriated name and parameters
     * 
     * @param type
     * @param methodName
     * @param parameters
     * @return {@link Method} for type
     * @throws IOException
     */
    public static Method findMethod(Class<?> type, String methodName, Class<?>... parameters) throws IOException {
        return findMember(type, methodName, (t, m) -> t.getDeclaredMethod(m, parameters));
    }

    /**
     * Finds passed {@link Class}'s or one of th's superclass {@link Field} with
     * appropriated name
     * 
     * @param type
     * @param fieldName
     * @return {@link Field} for type
     * @throws IOException
     */
    public static Field findField(Class<?> type, String fieldName) throws IOException {
        return findMember(type, fieldName, (t, f) -> t.getDeclaredField(f));
    }

    /**
     * Common method to invoke {@link Method} with reflection
     *
     * @param method
     * @param instance
     * @param arguments
     * @return {@link Object}
     * @throws IOException
     */
    public static Object invoke(Method method, Object instance, Object... arguments) throws IOException {

        Object value;

        try {
            makeAccessible(method);
            value = method.invoke(instance, arguments);
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            throw new IOException(ex);
        } catch (InvocationTargetException ex) {
            throw unwrap(ex);
        }

        return value;
    }
}
