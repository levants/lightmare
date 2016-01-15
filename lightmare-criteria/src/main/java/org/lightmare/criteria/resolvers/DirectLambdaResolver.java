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

import java.lang.invoke.MethodHandleInfo;
import java.util.Objects;

import org.lightmare.criteria.lambda.LambdaInfo;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.tuples.ResolverTuple;
import org.lightmare.criteria.utils.ClassUtils;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;
import org.objectweb.asm.Type;

/**
 * Abstract class to resolve entity field from
 * {@link org.lightmare.criteria.lambda.LambdaInfo} directly
 * 
 * @author Levan Tsinadze
 *
 */
abstract class DirectLambdaResolver extends AbstractFieldResolver {

    // Debug messages
    private static final String DEBUG_MESSAGE_DIR = "Resolved directly";

    /**
     * Gets type from descriptor
     * 
     * @param lambda
     * @return {@link org.objectweb.asm.Type} entity type
     */
    private static Type getFromDescription(LambdaInfo lambda) {

        Type type;

        String signature = lambda.getInstantiatedMethodType();
        Type[] types = Type.getArgumentTypes(signature);
        type = ObjectUtils.ifValid(types, CollectionUtils::singleton, CollectionUtils::getFirst);

        return type;
    }

    /**
     * Validates lambda for direct resolve
     * 
     * @param implClassName
     * @param descType
     * @return <code>boolean</code> validation result
     */
    private static boolean validateClassAndType(String implClassName, Type descType) {

        boolean valid = Objects.equals(implClassName, descType.getInternalName());

        if (ObjectUtils.notTrue(valid)) {
            Type implType = Type.getObjectType(implClassName);
            Class<?> implClass = ClassUtils.classForName(implType.getClassName());
            Class<?> descClass = ClassUtils.classForName(descType.getClassName());
            valid = implClass.isAssignableFrom(descClass);
        }

        return valid;
    }

    /**
     * Validates lambda for direct resolve
     * 
     * @param implClassName
     * @param descType
     * @return <code>boolean</code> validation result
     */
    private static boolean validateClassName(String implClassName, Type descType) {
        return (Objects.nonNull(descType) && validateClassAndType(implClassName, descType));
    }

    /**
     * Validates implementation method kind
     * 
     * @param lambda
     * @return <code>boolean</code> validation result
     */
    private static boolean validateMethodKind(LambdaInfo lambda) {
        return (lambda.getImplMethodKind() == MethodHandleInfo.REF_invokeVirtual);
    }

    /**
     * Validates lambda for direct resolve
     * 
     * @param lambda
     * @return <code>boolean</code> validation result
     */
    private static boolean validateSignature(LambdaInfo lambda) {
        return ObjectUtils.notEquals(lambda.getImplMethodSignature(), lambda.getInstantiatedMethodType());
    }

    /**
     * Validates lambda for direct resolve
     * 
     * @param lambda
     * @return <code>boolean</code> validation result
     */
    private static boolean validateLambda(LambdaInfo lambda) {
        return (validateMethodKind(lambda) && validateSignature(lambda));
    }

    /**
     * Resolves entity field and method from
     * {@link org.lightmare.criteria.lambda.LambdaInfo} object fields directly
     * 
     * @param lambda
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} if resolved
     */
    private static QueryTuple resolveFromLambda(LambdaInfo lambda) {

        QueryTuple tuple;

        String implClass = lambda.getImplClass();
        Type desc = getFromDescription(lambda);
        if (validateClassName(implClass, desc)) {
            String implDesc = lambda.getImplMethodSignature();
            String methodName = lambda.getImplMethodName();
            String entityName = desc.getInternalName();
            ResolverTuple<String> resolverTyple = ResolverTuple.of(implDesc, methodName, entityName);
            tuple = resolve(resolverTyple, DirectLambdaResolver::resolveEntityName);
            debug(DEBUG_MESSAGE_DIR, tuple);
        } else {
            tuple = null;
        }

        return tuple;
    }

    /**
     * Resolves entity field and method from
     * {@link org.lightmare.criteria.lambda.LambdaInfo} fields directly
     * 
     * @param lambda
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} if resolved
     */
    protected static QueryTuple resolveDirectly(LambdaInfo lambda) {
        return ObjectUtils.ifValid(lambda, DirectLambdaResolver::validateLambda,
                DirectLambdaResolver::resolveFromLambda);
    }
}
