package org.lightmare.linq.resolvers;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.lightmare.linq.jpa.ColumnProcessor;
import org.lightmare.linq.tuples.QueryTuple;
import org.lightmare.utils.ObjectUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Resolver class to initialize field name from lambda call
 * 
 * @author Levan Tsinadze
 *
 */
public class FieldResolver {

    private static final int ZERO_FLAGS = 0;

    private static final int FIRST = 0;

    private static final int BEGIN_INDEX = "get".length();

    private static final char SUB_CLASS = '$';

    private static final char PACKAGE_SEP = '.';

    private static final Logger LOG = Logger.getLogger(FieldResolver.class);

    public static void printVerbose(boolean verbose, Consumer<Boolean> consumer) {

	if (verbose) {
	    consumer.accept(verbose);
	}
    }

    private static String resolveFieldName(String methodName) {

	String fieldName = methodName.substring(BEGIN_INDEX);

	String first = String.valueOf(fieldName.charAt(FIRST));
	fieldName = fieldName.replaceFirst(first, first.toLowerCase());

	return fieldName;
    }

    private static String resolveEntityName(String owner) {

	String entityName;

	Type entityType = Type.getObjectType(owner);
	entityName = entityType.getClassName().replace(SUB_CLASS, PACKAGE_SEP);

	return entityName;
    }

    private static void logMethod(Type entityType, Type returnType, MethodInsnNode node) {

	LOG.info(entityType.getClassName());
	LOG.info(returnType.getClassName());
	LOG.info(resolveFieldName(node.name));
    }

    private static boolean valid(MethodInsnNode node, boolean werbose) {

	boolean valid;

	Type type = Type.getMethodType(node.desc);
	Type entityType = Type.getObjectType(node.owner);
	Type returnType = type.getReturnType();
	Type[] argumentTypes = type.getArgumentTypes();
	valid = (argumentTypes.length == FIRST && ObjectUtils.notEquals(Type.VOID_TYPE, returnType));
	printVerbose(werbose, c -> logMethod(entityType, returnType, node));

	return valid;

    }

    private static QueryTuple resolve(AbstractInsnNode instruction, boolean verbose) throws IOException {

	QueryTuple tuple;

	MethodInsnNode node = (MethodInsnNode) instruction;
	if (valid(node, verbose)) {
	    String fieldName = resolveFieldName(node.name);
	    String entityName = resolveEntityName(node.owner);
	    tuple = new QueryTuple(entityName, node.name, fieldName);
	    ColumnProcessor.setTemporalType(tuple);
	} else {
	    tuple = null;
	}

	return tuple;
    }

    private static QueryTuple resolve(InsnList instructions, boolean verbose) throws IOException {

	QueryTuple tuple = null;

	int size = instructions.size();
	for (int i = FIRST; (i < size && tuple == null); ++i) {
	    AbstractInsnNode instruction = instructions.get(i);
	    if (instruction instanceof MethodInsnNode) {
		MethodInsnNode node = ObjectUtils.cast(instruction);
		tuple = resolve(node, verbose);
	    }
	}

	return tuple;
    }

    public static boolean validate(MethodNode methodNode, SerializedLambda lambda) {

	boolean valid;

	String lambdaName = lambda.getImplMethodName();
	String lambdaSignature = lambda.getImplMethodSignature();
	valid = (methodNode.name.equals(lambdaName) && methodNode.desc.equals(lambdaSignature));

	return valid;
    }

    public static QueryTuple resolve(SerializedLambda lambda, boolean verbose) throws IOException {

	QueryTuple tuple;

	ClassReader reader = new ClassReader(lambda.getImplClass());
	ClassNode node = new ClassNode(Opcodes.ASM5);
	reader.accept(node, ZERO_FLAGS);
	@SuppressWarnings("unchecked")
	List<MethodNode> methods = node.methods;
	if (Objects.nonNull(methods)) {
	    MethodNode methodNode = methods.stream().filter(c -> validate(c, lambda)).findFirst().get();
	    methodNode.visitCode();
	    InsnList instructions = methodNode.instructions;
	    tuple = resolve(instructions, verbose);
	} else {
	    throw new IOException("Unresolvable field name");
	}

	return tuple;
    }
}
