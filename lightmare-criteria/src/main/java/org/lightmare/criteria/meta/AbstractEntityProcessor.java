package org.lightmare.criteria.meta;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ClassUtils;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Abstract utility class to fill reflection data to
 * {@link org.lightmare.criteria.tuples.QueryTuple} with entity {@link Class}
 * parameters
 * 
 * @author Levan Tsinadze
 *
 */
abstract class AbstractEntityProcessor {

    private static final Logger LOG = Logger.getLogger(AbstractEntityProcessor.class);

    /**
     * Validates if field can and should resolved from {@link Class} parameter
     * 
     * @param type
     * @return <code>boolean</code> validation result
     */
    public static boolean fieldResolvable(Class<?> type) {
        return ClassUtils.notInterface(type);
    }

    /**
     * Validates if {@link java.lang.reflect.Method} is getter for
     * {@link java.beans.PropertyDescriptor} instance
     * 
     * @param method
     * @param decriptor
     * @return <code>boolean</code> validation result
     */
    private static boolean validateField(Method method, PropertyDescriptor decriptor) {
        return Objects.equals(method, decriptor.getReadMethod());
    }

    /**
     * Gets {@link java.util.Optional} of {@link java.beans.PropertyDescriptor}
     * for field by getter or setter {@link java.lang.reflect.Method} instance
     * 
     * @param method
     * @param properties
     * @return {@link java.util.Optional} of
     *         {@link java.beans.PropertyDescriptor}
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
        ObjectUtils.notEquals(tuple.getFieldName(), descriptor.getDisplayName(), (x, y) -> tuple.setFieldName(y));
    }

    /**
     * Corrects resolved {@link java.lang.reflect.Field} name and sets
     * {@link java.lang.reflect.Method} to passed
     * {@link org.lightmare.criteria.tuples.QueryTuple} instance
     * 
     * @param type
     * @param method
     * @param tuple
     */
    private static void setProperField(Class<?> type, Method method, QueryTuple tuple) {

        try {
            BeanInfo benInfo = Introspector.getBeanInfo(type, Object.class, Introspector.USE_ALL_BEANINFO);
            PropertyDescriptor[] properties = benInfo.getPropertyDescriptors();
            Optional<PropertyDescriptor> optional = find(method, properties);
            optional.ifPresent(c -> setFieldName(c, tuple));
        } catch (IntrospectionException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Corrects resolved {@link java.lang.reflect.Field} name and sets
     * {@link java.lang.reflect.Method} to passed
     * {@link org.lightmare.criteria.tuples.QueryTuple} instance if type
     * parameter is not <code>null</code>
     * 
     * @param method
     * @param tuple
     */
    protected static void setProperField(Method method, QueryTuple tuple) {
        tuple.setMethod(method);
        ObjectUtils.valid(method.getDeclaringClass(), AbstractEntityProcessor::fieldResolvable,
                c -> setProperField(c, method, tuple));
    }
}
