package org.lightmare.criteria.utils;

import org.objectweb.asm.Type;

/**
 * Utility class for <b><i>bytecode</i></b> analysis
 * 
 * @author Levan Tsinadze
 *
 */
abstract class BytecodeUtils {

    // Getter method prefix
    private static final String GET = "get";

    // Getter / setter method prefix end index
    public static final int GETTER_START_INDEX = GET.length();

    /**
     * Resolves entity name from method owner
     * 
     * @param owner
     * @return {@link String} entity name
     */
    public static String resolveEntityName(String owner) {
        return Type.getObjectType(owner).getClassName();
    }

    /**
     * Validates if method has no arguments
     * 
     * @param methodType
     * @return <code>boolean</code> validation result
     */
    private static boolean isArgumentFree(Type methodType) {
        return CollectionUtils.isEmpty(methodType.getArgumentTypes());
    }

    /**
     * Validates if method is not VOID return type
     * 
     * @param methodType
     * @return <code>boolean</code> validation result
     */
    private static boolean notVoid(Type methodType) {
        return ObjectUtils.notEquals(Type.VOID_TYPE, methodType.getReturnType());
    }

    /**
     * Validates getter method by arguments and return type
     * 
     * @param methodType
     * @return <code>boolean</code> validation result
     */
    private static boolean validGetter(Type methodType) {
        return (isArgumentFree(methodType) && notVoid(methodType));
    }

    /**
     * Validates if resolved method is valid getter for entity field
     * 
     * @param desc
     * @param methodName
     * @return <code>boolean</code> validation result
     */
    public static boolean validGetter(String desc, String methodName) {

        boolean valid;

        Type methodType = Type.getMethodType(desc);
        valid = (methodName.startsWith(GET) && validGetter(methodType));

        return valid;
    }
}
