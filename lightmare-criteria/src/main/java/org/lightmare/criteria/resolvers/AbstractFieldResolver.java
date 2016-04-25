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
package org.lightmare.criteria.resolvers;

import java.beans.Introspector;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.lightmare.criteria.meta.EntityProcessor;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.tuples.ResolverTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;
import org.objectweb.asm.Type;

/**
 * Abstract class to resolve entity field and entity names for JPA query
 * {@link org.lightmare.criteria.lambda.LambdaInfo} parameters
 * 
 * @author Levan Tsinadze
 *
 */
class AbstractFieldResolver {

    // Getter method prefix
    private static final String GET = "get";

    // Getter / setter method prefix end index
    private static final int START_INDEX = GET.length();

    // Debug message pattern
    private static final String DEBUG_MESSAGE_PATTERN = " - %s.%s";

    private static final Logger LOG = Logger.getLogger(AbstractFieldResolver.class);

    /**
     * Logs resolved entity parameters if appropriated log level enabled
     * 
     * @param resolveType
     * @param tuple
     */
    private static void logMessage(String resolveType, QueryTuple tuple) {

        if (LOG.isInfoEnabled()) {
            String tupleInfo = String.format(DEBUG_MESSAGE_PATTERN, tuple.getEntityName(), tuple.getMethodName());
            String message = StringUtils.concat(resolveType, tupleInfo);
            LOG.info(message);
        }
    }

    /**
     * Logs passed message on DEBUG level
     * 
     * @param message
     */
    protected static void debug(String message, QueryTuple tuple) {
        ObjectUtils.nonNull(tuple, c -> logMessage(message, c));
    }

    /**
     * Resolves field name from method name
     * 
     * @param methodName
     * @return {@link String} field name
     */
    private static String resolveFieldName(String methodName) {

        String fieldName;

        String raw = methodName.substring(START_INDEX);
        fieldName = Introspector.decapitalize(raw);

        return fieldName;
    }

    /**
     * Resolves entity name from method owner
     * 
     * @param owner
     * @return {@link String} entity name
     */
    protected static String resolveEntityName(String owner) {
        return Type.getObjectType(owner).getClassName();
    }

    /**
     * Validates getter method by arguments and return type
     * 
     * @param methodType
     * @return <code>boolean</code> validation result
     */
    private static boolean validGetter(Type methodType) {
        return (CollectionUtils.isEmpty(methodType.getArgumentTypes())
                && ObjectUtils.notEquals(Type.VOID_TYPE, methodType.getReturnType()));
    }

    /**
     * Validates if resolved method is valid getter for entity field
     * 
     * @param desc
     * @param name
     * @return <code>boolean</code> validation result
     */
    private static boolean valid(String desc, String methodName) {

        boolean valid;

        Type methodType = Type.getMethodType(desc);
        valid = (methodName.startsWith(GET) && validGetter(methodType));

        return valid;
    }

    /**
     * Validates if resolved method is setter or getter in
     * {@link org.lightmare.criteria.tuples.ResolverTuple} for entity field
     * 
     * @param resolverTuple
     * @return <code>boolean</code> validation result
     */
    private static boolean valid(ResolverTuple<?> resolverTuple) {
        return valid(resolverTuple.getDesc(), resolverTuple.getName());
    }

    /**
     * Sets reflection data ( {@link java.lang.reflect.Method},
     * {@link java.lang.reflect.Field} etc ) to tuple
     * 
     * @param tuple
     */
    private static void setMetaData(QueryTuple tuple) {
        EntityProcessor.setEntityType(tuple);
    }

    /**
     * Sets entity {@link Class} and class name for generic type argument
     * resolvers
     * 
     * @param tuple
     */
    public static void setGenericData(QueryTuple tuple) {
        EntityProcessor.setGenericData(tuple);
    }

    /**
     * Resolves if passed {@link org.lightmare.criteria.tuples.ResolverTuple} is
     * valid
     * 
     * @param resolverTuple
     * @param nameResolver
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} resolved from
     *         {@link org.lightmare.criteria.tuples.ResolverTuple} instance
     */
    private static <T> QueryTuple resolveIfValid(ResolverTuple<T> resolverTuple, Function<T, String> nameResolver) {

        QueryTuple tuple;

        String methodName = resolverTuple.getName();
        String fieldName = resolveFieldName(methodName);
        String entityName = nameResolver.apply(resolverTuple.getType());
        tuple = QueryTuple.of(entityName, methodName, fieldName);
        setMetaData(tuple);

        return tuple;
    }

    /**
     * Resolves field name from method descriptor, method name and owner
     * instance
     * 
     * @param resolverTuple
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for resolved
     *         field, entity and JPA query part
     */
    protected static <T> QueryTuple resolveFromTuple(ResolverTuple<T> resolverTuple, Function<T, String> nameResolver) {
        return ObjectUtils.ifIsValid(resolverTuple, AbstractFieldResolver::valid, c -> resolveIfValid(c, nameResolver));
    }
}
