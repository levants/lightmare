/*
 * Lightmare-criteria, JPA-QL query generator using lambda expressions
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
package org.lightmare.criteria.meta;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ClassUtils;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Adds reflection data to {@link org.lightmare.criteria.tuples.QueryTuple}
 * instance
 * 
 * @author Levan Tsinadze
 *
 */
public class EntityProcessor {

    private static final Logger LOG = Logger.getLogger(EntityProcessor.class);

    /**
     * Resolves {@link java.lang.reflect.Method} argument types
     * 
     * @param tuple
     * @return {@link Class} array by names for argument types
     */
    private static Class<?>[] getArgumentTypes(QueryTuple tuple) {

        Class<?>[] argumentTypes;

        String[] names = tuple.getArguments();
        if (CollectionUtils.isEmpty(names)) {
            argumentTypes = new Class<?>[] {};
        } else {
            argumentTypes = CollectionUtils.map(names, new Class<?>[names.length], ClassUtils::classForName);
        }

        return argumentTypes;
    }

    /**
     * Validates if {@link java.lang.reflect.Method} is getter or setter for
     * {@link java.beans.PropertyDescriptor} instance
     * 
     * @param method
     * @param decriptor
     * @return <code>boolean</code> validation result
     */
    private static boolean validateField(Method method, PropertyDescriptor decriptor) {
        return (method.equals(decriptor.getWriteMethod()) || method.equals(decriptor.getReadMethod()));
    }

    /**
     * Gets {@link java.util.Optional} of {@link java.beans.PropertyDescriptor}
     * for field by getter or setter {@link java.lang.reflect.Method} instance
     * 
     * @param method
     * @param properties
     * @return
     */
    private static Optional<PropertyDescriptor> find(Method method, PropertyDescriptor[] properties) {
        return Stream.of(properties).filter(c -> validateField(method, c)).findAny();
    }

    /**
     * If resolved name not equals {@link java.beans.PropertyDescriptor}
     * provided name then switches this names in passed
     * {@link org.lightmare.criteria.tuples.QueryTuple} instance
     * 
     * @param descriptor
     * @param tuple
     */
    private static void setFieldName(PropertyDescriptor descriptor, QueryTuple tuple) {
        String realName = descriptor.getDisplayName();
        ObjectUtils.notEquals(tuple.getFieldName(), realName, (x, y) -> tuple.setFieldName(y));
    }

    /**
     * Corrects resolved {@link java.lang.reflect.Field} name and sets
     * {@link java.lang.reflect.Method} to passed
     * {@link org.lightmare.criteria.tuples.QueryTuple} instance
     * 
     * @param type
     * @param method
     * 
     * @return {@link java.beans.PropertyDescriptor} for
     *         {@link java.lang.reflect.Method}
     */
    private static PropertyDescriptor getProperField(Class<?> type, Method method) {

        PropertyDescriptor descriptor;

        try {
            BeanInfo benInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] properties = benInfo.getPropertyDescriptors();
            Optional<PropertyDescriptor> optional = find(method, properties);
            if (optional.isPresent()) {
                descriptor = optional.get();
            } else {
                descriptor = null;
            }
        } catch (IntrospectionException ex) {
            descriptor = null;
            LOG.error(ex.getMessage(), ex);
        }

        return descriptor;
    }

    /**
     * Corrects resolved {@link java.lang.reflect.Field} name and sets
     * {@link java.lang.reflect.Method} to passed
     * {@link org.lightmare.criteria.tuples.QueryTuple} instance if type
     * parameter is not null
     * 
     * @param method
     * @param tuple
     */
    private static void setProperField(Method method, QueryTuple tuple) {

        tuple.setMethod(method);
        Class<?> type = method.getDeclaringClass();
        PropertyDescriptor descriptor = null;
        while (Objects.nonNull(type) && descriptor == null) {
            descriptor = getProperField(type, method);
            if (descriptor == null) {
                type = type.getSuperclass();
            } else {
                setFieldName(descriptor, tuple);
            }
        }

    }

    /**
     * Sets {@link java.lang.reflect.Method} and {@link java.lang.reflect.Field}
     * by names to passed wrapper
     * 
     * @param tuple
     */
    public static void setMethodAndField(QueryTuple tuple) {

        Class<?> entityType = tuple.getEntityType();
        Class<?>[] argumentTypes = getArgumentTypes(tuple);
        Method method = ClassUtils.findMethod(entityType, tuple.getMethodName(), argumentTypes);
        ObjectUtils.nonNull(method, c -> setProperField(c, tuple));
        Field field = ClassUtils.findField(entityType, tuple.getFieldName());
        ObjectUtils.nonNull(field, tuple::setField);
    }

    /**
     * Sets {@link Class}, {@link java.lang.reflect.Method} and
     * {@link java.lang.reflect.Field} by names to wrapper
     * 
     * @param tuple
     */
    public static void setEntityType(QueryTuple tuple) {

        String className = tuple.getEntityName();
        Class<?> entityType = ClassUtils.classForName(className);
        tuple.setEntityType(entityType);
    }
}
