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

import org.lightmare.criteria.cache.MethodCache;
import org.lightmare.criteria.lambda.LambdaInfo;
import org.lightmare.criteria.orm.ColumnProcessor;
import org.lightmare.criteria.orm.EntityProcessor;
import org.lightmare.criteria.orm.GenericProcessor;
import org.lightmare.criteria.tuples.QueryTuple;
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

    private static final int SINGLE_ARG = 1;

    private static final int FIRST = 0;

    private static final String THIS_PT = "this";

    // Getter method prefix
    private static final String GET = "get";

    // Setter method prefix
    private static final String SET = "set";

    private static final int BEGIN_INDEX = GET.length();

    // Error messages
    private static final String UNRESOLVABLE_ERROR = "Unresolvable field name";

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
     * Resolves entity name
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
     * resolves argument {@link Type}s for method descriptor
     * 
     * @param desc
     * @return {@link Type}[] array of argument {@link Type}s
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
        return (Objects.nonNull(argumentTypes) && argumentTypes.length == SINGLE_ARG
                && Type.VOID_TYPE.equals(returnType));
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
     * Sets reflection meta data to passed tuple
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
     * Resolves field name from instruction instance
     * 
     * @param instruction
     * @param verbose
     * @return {@link QueryTuple} for resolved field and query part
     * @throws IOException
     */
    private static QueryTuple resolve(MethodInsnNode node) throws IOException {

        QueryTuple tuple;

        String desc = node.desc;
        String methodName = node.name;
        if (valid(desc, methodName)) {
            String fieldName = resolveFieldName(methodName);
            String entityName = resolveEntityName(node.owner);
            String[] arguments = resolveArgumentsTypes(desc);
            tuple = QueryTuple.of(entityName, methodName, arguments, fieldName);
            setMetaData(tuple);
        } else {
            tuple = null;
        }

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
     * Gets entity name from {@link MethodNode} es its first variable
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
     * Resolves field name from instruction instance
     * 
     * @param instruction
     * @param verbose
     * @return {@link QueryTuple} for resolved field and query part
     * @throws IOException
     */
    private static QueryTuple resolve(MethodNode node) throws IOException {

        QueryTuple tuple;

        String desc = node.desc;
        String name = node.name;
        if (valid(desc, name)) {
            String fieldName = resolveFieldName(name);
            String entityName = resolveEntityName(node);
            String[] arguments = resolveArgumentsTypes(desc);
            tuple = QueryTuple.of(entityName, name, arguments, fieldName);
            setMetaData(tuple);
        } else {
            tuple = null;
        }

        return tuple;
    }

    /**
     * Validates if {@link AbstractInsnNode} is instance of
     * {@link MethodInsnNode} then resolves appropriated {@link QueryTuple} from
     * it
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
        for (int i = FIRST; (i < size && tuple == null); ++i) {
            AbstractInsnNode instruction = instructions.get(i);
            validateAndResolve(instruction);
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
     * Resolves entity parameters from {@link MethodNode} and instructions
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
     * Resolved field name, getter method name and entity type from lambda
     * argument
     * 
     * @param lambda
     * @return {@link QueryTuple} for resolved field and query part
     */
    public static QueryTuple resolve(LambdaInfo lambda) {

        QueryTuple tuple;

        List<MethodNode> methods = MethodCache.getMethods(lambda);
        if (Objects.nonNull(methods)) {
            MethodNode methodNode = CollectionUtils.getFirstValid(methods, c -> validate(c, lambda));
            tuple = resolveRecursively(methodNode);
        } else {
            throw new RuntimeException(UNRESOLVABLE_ERROR);
        }

        return tuple;
    }
}
