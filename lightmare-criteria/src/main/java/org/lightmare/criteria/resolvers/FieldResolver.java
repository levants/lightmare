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

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.lightmare.criteria.cache.MethodCache;
import org.lightmare.criteria.lambda.LambdaInfo;
import org.lightmare.criteria.meta.ColumnProcessor;
import org.lightmare.criteria.meta.EntityProcessor;
import org.lightmare.criteria.meta.GenericProcessor;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.tuples.ResolverTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Resolver class to initialize field name from lambda function
 * 
 * @author Levan Tsinadze
 *
 */
public class FieldResolver {

    private static final int FIRST = 0;

    private static final String THIS_PT = "this";

    // Getter method prefix
    private static final String GET = "get";

    // Setter method prefix
    private static final String SET = "set";

    private static final int BEGIN_INDEX = GET.length();

    // Error messages
    private static final String UNRESOLVABLE_FIELD_ERROR = "Unresolvable field name";

    /**
     * Resolves field name from method name
     * 
     * @param methodName
     * @return {@link String} field name
     */
    private static String resolveFieldName(String methodName) {

        String fieldName = methodName.substring(BEGIN_INDEX);

        String first = String.valueOf(fieldName.charAt(FIRST));
        fieldName = fieldName.replaceFirst(first, first.toLowerCase());

        return fieldName;
    }

    /**
     * Resolves entity name from method owner
     * 
     * @param owner
     * @return {@link String} entity name
     */
    private static String resolveEntityName(String owner) {

        String entityName;

        Type entityType = Type.getObjectType(owner);
        entityName = entityType.getClassName();

        return entityName;
    }

    /**
     * Generate type names array from argument types
     * 
     * @param argumentTypes
     * @return {@link String} array of argument type names
     * @throws IOException
     */
    private static String[] mapToNames(Type[] argumentTypes) throws IOException {

        String[] arguments;

        if (argumentTypes == null) {
            arguments = null;
        } else if (CollectionUtils.isEmpty(argumentTypes)) {
            arguments = new String[] {};
        } else {
            arguments = new String[argumentTypes.length];
            CollectionUtils.map(argumentTypes, arguments, Type::getClassName);
        }

        return arguments;
    }

    /**
     * resolves argument {@link org.objectweb.asm.Type}s for method descriptor
     * 
     * @param desc
     * @return {@link org.objectweb.asm.Type}[] array of argument
     *         {@link org.objectweb.asm.Type}s
     * @throws IOException
     */
    private static String[] resolveArgumentsTypes(String desc) throws IOException {

        String[] arguments;

        Type methodType = Type.getMethodType(desc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        arguments = mapToNames(argumentTypes);

        return arguments;
    }

    /**
     * Validates setter method by name, arguments and return type
     * 
     * @param returnType
     * @param argumentTypes
     * @return <code>boolean</code> validation result
     */
    private static boolean validSetter(Type returnType, Type[] argumentTypes) {
        return (CollectionUtils.singleton(argumentTypes) && Type.VOID_TYPE.equals(returnType));
    }

    /**
     * Validates getter method by name, arguments and return type
     * 
     * @param returnType
     * @param argumentTypes
     * @return <code>boolean</code> validation result
     */
    private static boolean validGetter(Type returnType, Type[] argumentTypes) {
        return (CollectionUtils.isEmpty(argumentTypes) && ObjectUtils.notEquals(Type.VOID_TYPE, returnType));
    }

    /**
     * Validates if resolved method is setter or getter for entity field
     * 
     * @param desc
     * @param name
     * @return <code>boolean</code> validation result
     */
    private static boolean valid(String desc, String methodName) {

        boolean valid;

        Type methodType = Type.getMethodType(desc);
        Type returnType = methodType.getReturnType();
        Type[] argumentTypes = methodType.getArgumentTypes();
        if (methodName.startsWith(GET)) {
            valid = validGetter(returnType, argumentTypes);
        } else if (methodName.startsWith(SET)) {
            valid = validSetter(returnType, argumentTypes);
        } else {
            valid = Boolean.FALSE;
        }

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
     * @throws IOException
     */
    private static void setMetaData(QueryTuple tuple) throws IOException {

        EntityProcessor.setMetaData(tuple);
        ColumnProcessor.setTemporalType(tuple);
        GenericProcessor.setGenericType(tuple);
    }

    /**
     * Resolves field name from method descriptor, method name and owner
     * instance
     * 
     * @param resolverTuple
     * @return {@link QueryTuple} for resolved field and query part
     * @throws IOException
     */
    private static <T> QueryTuple resolve(ResolverTuple<T> resolverTuple, Function<T, String> nameResolver)
            throws IOException {

        QueryTuple tuple;

        String desc = resolverTuple.getDesc();
        String methodName = resolverTuple.getName();
        if (valid(resolverTuple)) {
            String fieldName = resolveFieldName(methodName);
            String entityName = nameResolver.apply(resolverTuple.getType());
            String[] arguments = resolveArgumentsTypes(desc);
            tuple = QueryTuple.of(entityName, methodName, arguments, fieldName);
            setMetaData(tuple);
        } else {
            tuple = null;
        }

        return tuple;
    }

    /**
     * Resolves field name from instruction instance
     * 
     * @param instruction
     * @param verbose
     * @return {@link QueryTuple} for resolved field and query part
     * @throws IOException
     */
    private static QueryTuple resolve(MethodInsnNode node) throws IOException {

        QueryTuple tuple;

        ResolverTuple<String> resolverTyple = ResolverTuple.of(node.desc, node.name, node.owner);
        tuple = resolve(resolverTyple, FieldResolver::resolveEntityName);

        return tuple;
    }

    /**
     * Removes semicolons from entity name
     * 
     * @param name
     * @return {@link String} entity name
     */
    private static String clearEntityName(String name) {

        String validName;

        Type type = Type.getType(name);
        validName = type.getClassName();

        return validName;
    }

    /**
     * Resolves entity name from local variable
     * 
     * @param variable
     * @return {@link String} entity name
     */
    private static String resolveEntityName(LocalVariableNode variable) {

        String entityName;

        if (THIS_PT.equals(variable.name)) {
            String raw = variable.desc;
            entityName = clearEntityName(raw);
        } else {
            entityName = null;
        }

        return entityName;
    }

    /**
     * Gets entity name from {@link org.objectweb.asm.tree.MethodNode} as its
     * first variable
     * 
     * @param node
     * @return {@link String} entity name
     */
    private static String resolveEntityName(MethodNode node) {

        String entityName;

        List<LocalVariableNode> variables = ObjectUtils.cast(node.localVariables);
        if (variables == null || variables.isEmpty()) {
            entityName = null;
        } else {
            LocalVariableNode variable = CollectionUtils.getFirst(variables);
            entityName = resolveEntityName(variable);
        }

        return entityName;
    }

    /**
     * Resolves field name from {@link org.objectweb.asm.tree.MethodNode}
     * instance
     * 
     * @param instruction
     * @param verbose
     * @return {@link QueryTuple} for resolved field and query part
     * @throws IOException
     */
    private static QueryTuple resolve(MethodNode node) throws IOException {

        QueryTuple tuple;

        ResolverTuple<MethodNode> resolverTyple = ResolverTuple.of(node.desc, node.name, node);
        tuple = resolve(resolverTyple, FieldResolver::resolveEntityName);

        return tuple;
    }

    /**
     * Validates if passed {@link org.objectweb.asm.tree.AbstractInsnNode} is
     * instance of {@link MethodInsnNode} then resolves appropriated
     * {@link QueryTuple} from it
     * 
     * @param instruction
     * @return
     * @throws IOException
     */
    private static QueryTuple validateAndResolve(AbstractInsnNode instruction) throws IOException {

        QueryTuple tuple;

        if (instruction instanceof MethodInsnNode) {
            MethodInsnNode node = ObjectUtils.cast(instruction);
            tuple = resolve(node);
        } else {
            tuple = null;
        }

        return tuple;
    }

    /**
     * Resolves appropriated instructions for field recognition
     * 
     * @param instructions
     * @param verbose
     * @return {@link QueryTuple} for resolved field and query part
     * @throws IOException
     */
    private static QueryTuple resolve(InsnList instructions) throws IOException {

        QueryTuple tuple = null;

        int size = instructions.size();
        AbstractInsnNode instruction;
        for (int i = FIRST; (i < size && tuple == null); ++i) {
            instruction = instructions.get(i);
            tuple = validateAndResolve(instruction);
        }

        return tuple;
    }

    /**
     * Validates method and signature for field resolver
     * 
     * @param methodNode
     * @param lambda
     * @return </code>boolean</code> validation result
     */
    public static boolean validate(MethodNode methodNode, LambdaInfo lambda) {

        boolean valid;

        String lambdaName = lambda.getImplMethodName();
        String lambdaSignature = lambda.getImplMethodSignature();
        valid = (methodNode.name.equals(lambdaName) && methodNode.desc.equals(lambdaSignature));

        return valid;
    }

    /**
     * Resolves entity parameters from {@link org.objectweb.asm.tree.MethodNode}
     * and instructions
     * 
     * @param methodNode
     * @return {@link QueryTuple} from method
     * @throws IOException
     */
    private static QueryTuple resolveMethod(MethodNode methodNode) throws IOException {

        QueryTuple tuple = resolve(methodNode);

        if (tuple == null) {
            methodNode.visitCode();
            InsnList instructions = methodNode.instructions;
            tuple = resolve(instructions);
        }

        return tuple;
    }

    /**
     * Resolves entity parameters from {@link MethodNode} and instructions
     * 
     * @param methodNode
     * @return {@link QueryTuple} from method
     */
    private static QueryTuple resolveRecursively(MethodNode methodNode) {

        QueryTuple tuple;

        try {
            tuple = resolveMethod(methodNode);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return tuple;
    }

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
        if (Objects.nonNull(desc) && Objects.equals(implClass, desc.getInternalName())) {
            String implDesc = lambda.getImplMethodSignature();
            String methodName = lambda.getImplMethodName();
            ResolverTuple<String> resolverTyple = ResolverTuple.of(implDesc, methodName, implClass);
            tuple = resolve(resolverTyple, FieldResolver::resolveEntityName);
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
    private static QueryTuple resolveDirectQuietly(LambdaInfo lambda) {

        QueryTuple tuple;

        try {
            tuple = resolveDirect(lambda);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return tuple;
    }

    /**
     * Resolved field name, getter method name and entity type from lambda
     * argument by bytecode analysis
     * 
     * @param lambda
     * @return {@link QueryTuple} for resolved field and query part
     */
    private static QueryTuple resolveFromBytecode(LambdaInfo lambda) {

        QueryTuple tuple;

        List<MethodNode> methods = MethodCache.getMethods(lambda);
        if (Objects.nonNull(methods)) {
            MethodNode methodNode = CollectionUtils.getFirstValid(methods, c -> validate(c, lambda));
            tuple = resolveRecursively(methodNode);
        } else {
            throw new RuntimeException(UNRESOLVABLE_FIELD_ERROR);
        }

        return tuple;
    }

    /**
     * Resolved field name, getter method name and entity type from lambda
     * argument
     * 
     * @param lambda
     * @return {@link QueryTuple} for resolved field and query part
     */
    public static QueryTuple resolve(LambdaInfo lambda) {

        QueryTuple tuple = resolveDirectQuietly(lambda);

        if (tuple == null) {
            tuple = resolveFromBytecode(lambda);
        }

        return tuple;
    }
}
