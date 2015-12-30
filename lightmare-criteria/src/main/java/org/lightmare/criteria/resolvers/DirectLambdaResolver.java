package org.lightmare.criteria.resolvers;

import java.io.IOException;
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
        type = CollectionUtils.getFirst(types);

        return type;
    }

    /**
     * Validates lambda for direct resolve
     * 
     * @param implClassName
     * @param descType
     * @return <code>boolean</code> validation result
     * @throws IOException
     */
    private static boolean validate(String implClassName, Type descType) throws IOException {

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
    private static boolean validate(LambdaInfo lambda) {
        return (validateMethodKind(lambda) && validateSignature(lambda));
    }

    /**
     * Resolves entity field and method from {@link LambdaInfo} object fields
     * directly
     * 
     * @param lambda
     * @return {@link QueryTuple} if resolved
     * @throws IOException
     */
    private static QueryTuple resolveDirect(LambdaInfo lambda) throws IOException {

        QueryTuple tuple;

        String implClass = lambda.getImplClass();
        Type desc = getFromDescription(lambda);
        if (Objects.nonNull(desc) && validate(implClass, desc)) {
            String implDesc = lambda.getImplMethodSignature();
            String methodName = lambda.getImplMethodName();
            String entityName = desc.getInternalName();
            ResolverTuple<String> resolverTyple = ResolverTuple.of(implDesc, methodName, entityName);
            tuple = resolve(resolverTyple, DirectLambdaResolver::resolveEntityName);
        } else {
            tuple = null;
        }

        return tuple;
    }

    /**
     * Resolves entity field and method from {@link LambdaInfo} object fields
     * directly
     * 
     * @param lambda
     * @return {@link QueryTuple} if resolved
     * @throws IOException
     */
    private static QueryTuple validateAndResolveDirect(LambdaInfo lambda) throws IOException {

        QueryTuple tuple;

        if (validate(lambda)) {
            tuple = resolveDirect(lambda);
        } else {
            tuple = null;
        }

        return tuple;
    }

    /**
     * Resolves entity field and method from {@link LambdaInfo} object fields
     * directly without throwing exceptions
     * 
     * @param lambda
     * @return {@link QueryTuple} if resolved
     */
    protected static QueryTuple resolveDirectQuietly(LambdaInfo lambda) {

        QueryTuple tuple;

        try {
            tuple = validateAndResolveDirect(lambda);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return tuple;
    }
}
