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
import org.lightmare.criteria.lambda.LambdaData;
import org.lightmare.criteria.orm.ColumnProcessor;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
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

    private static final String GET = "get";

    private static final String SET = "set";

    private static final int BEGIN_INDEX = GET.length();

    // Error messages
    private static final String UNRESOLVABLE_ERROR = "Unresolvable field name";

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

    private static boolean validSetter(Type returnType, Type[] argumentTypes) {
	return (Objects.nonNull(argumentTypes) && argumentTypes.length == SINGLE_ARG
		&& Type.VOID_TYPE.equals(returnType));
    }

    private static boolean validGetter(Type returnType, Type[] argumentTypes) {
	return (CollectionUtils.isEmpty(argumentTypes) && ObjectUtils.notEquals(Type.VOID_TYPE, returnType));
    }

    private static boolean valid(MethodInsnNode node) {

	boolean valid;

	Type methodType = Type.getMethodType(node.desc);
	Type returnType = methodType.getReturnType();
	Type[] argumentTypes = methodType.getArgumentTypes();
	String name = node.name;
	if (name.startsWith(GET)) {
	    valid = validGetter(returnType, argumentTypes);
	} else if (name.startsWith(SET)) {
	    valid = validSetter(returnType, argumentTypes);
	} else {
	    valid = Boolean.FALSE;
	}

	return valid;
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

	if (valid(node)) {
	    String fieldName = resolveFieldName(node.name);
	    String entityName = resolveEntityName(node.owner);
	    tuple = new QueryTuple(entityName, node.name, fieldName);
	    ColumnProcessor.setTemporalType(tuple);
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
	    if (instruction instanceof MethodInsnNode) {
		MethodInsnNode node = ObjectUtils.cast(instruction);
		tuple = resolve(node);
	    }
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
    public static boolean validate(MethodNode methodNode, LambdaData lambda) {

	boolean valid;

	String lambdaName = lambda.getImplMethodName();
	String lambdaSignature = lambda.getImplMethodSignature();
	valid = (methodNode.name.equals(lambdaName) && methodNode.desc.equals(lambdaSignature));

	return valid;
    }

    /**
     * Resolved field name, getter method name and entity type from lambda
     * argument
     * 
     * @param lambda
     * @param verbose
     * @return {@link QueryTuple} for resolved field and query part
     * @throws IOException
     */
    public static QueryTuple resolve(LambdaData lambda) throws IOException {

	QueryTuple tuple;

	List<MethodNode> methods = MethodCache.getMethods(lambda);
	if (Objects.nonNull(methods)) {
	    MethodNode methodNode = methods.stream().filter(c -> validate(c, lambda)).findFirst().get();
	    methodNode.visitCode();
	    InsnList instructions = methodNode.instructions;
	    tuple = resolve(instructions);
	} else {
	    throw new IOException(UNRESOLVABLE_ERROR);
	}

	return tuple;
    }
}
