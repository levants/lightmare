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
 * Abstract class to resolve entity and field from
 * {@link org.lightmare.criteria.lambda.LambdaInfo} parameters directly
 * 
 * @author Levan Tsinadze
 *
 */
abstract class DirectFieldResolver extends BytecodeFieldResolver {

    // Debug messages
    private static final String DEBUG_MESSAGE_DIR = "Resolved directly";

    /**
     * Gets entity type from descriptor
     * 
     * @param lambda
     * @return {@link org.objectweb.asm.Type} entity type
     */
    private static Type getFromDescription(LambdaInfo lambda) {

        Type type;

        String signature = lambda.getInstantiatedMethodType();
        Type[] types = Type.getArgumentTypes(signature);
        type = ObjectUtils.ifIsValid(types, CollectionUtils::singleton, CollectionUtils::getFirst);

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
     * Validates implementation method kind (for invoke virtual and invoke
     * interface)
     * 
     * @param lambda
     * @return <code>boolean</code> validation result
     */
    private static boolean validateMethodKind(LambdaInfo lambda) {

        boolean valid;

        int kind = lambda.getImplMethodKind();
        valid = ((kind == MethodHandleInfo.REF_invokeVirtual) || (kind == MethodHandleInfo.REF_invokeInterface));

        return valid;
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
     * Resolves {@link org.lightmare.criteria.tuples.QueryTuple} from lambda
     * parameters are valid
     * 
     * @param lambda
     * @param desc
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} if resolved
     */
    private static QueryTuple resolveFromValidLambda(LambdaInfo lambda, Type desc) {

        QueryTuple tuple;

        String implDesc = lambda.getImplMethodSignature();
        String methodName = lambda.getImplMethodName();
        String entityName = desc.getInternalName();
        ResolverTuple<String> resolverTyple = ResolverTuple.of(implDesc, methodName, entityName);
        tuple = resolveFromTuple(resolverTyple, DirectFieldResolver::resolveEntityName);
        debug(DEBUG_MESSAGE_DIR, tuple);

        return tuple;
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
        tuple = ObjectUtils.ifIsValid(lambda, l -> validateClassName(implClass, desc),
                c -> resolveFromValidLambda(c, desc));

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
        return ObjectUtils.ifIsValid(lambda, DirectFieldResolver::validateLambda,
                DirectFieldResolver::resolveFromLambda);
    }
}
