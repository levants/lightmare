package org.lightmare.linq.resolvers;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.lightmare.linq.query.FieldVisitor;
import org.lightmare.linq.tuples.QueryTuple;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class FieldResolver {

	private static final int ZERO_FLAGS = 0;

	private static final int BEGIN_INDEX = "get".length();

	private static final char SUB_CLASS = '$';

	private static final char PACKAGE_SEP = '.';

	private static void printWerbose(boolean werbose, Consumer<Boolean> consumer) {

		if (werbose) {
			consumer.accept(werbose);
		}
	}

	private static String resolveFieldName(String methodName) {

		String fieldName = methodName.substring(BEGIN_INDEX);

		String first = String.valueOf(fieldName.charAt(0));
		fieldName = fieldName.replaceFirst(first, first.toLowerCase());

		return fieldName;
	}

	private static String resolveEntityName(String owner) {

		String entityName;

		Type entityType = Type.getObjectType(owner);
		entityName = entityType.getClassName().replace(SUB_CLASS, PACKAGE_SEP);

		return entityName;
	}

	private static boolean valid(MethodInsnNode node, boolean werbose) {

		boolean valid;

		Type type = Type.getMethodType(node.desc);
		String entityName = node.owner;
		Type entityType = Type.getObjectType(entityName);
		Type returnType = type.getReturnType();
		Type[] argumentTypes = type.getArgumentTypes();
		valid = argumentTypes.length == 0 && !Type.VOID_TYPE.equals(returnType);
		printWerbose(werbose, c -> {
			System.out.println(entityType.getClassName());
			System.out.println(returnType.getClassName());
			System.out.println(resolveFieldName(node.name));
		});

		return valid;
	}

	private static QueryTuple resolve(InsnList instructions, boolean werbose) {

		QueryTuple tuple = null;

		boolean next = Boolean.TRUE;
		int size = instructions.size();
		for (int i = 0; i < size && next; ++i) {
			AbstractInsnNode instruction = instructions.get(i);
			if (instruction instanceof MethodInsnNode) {
				MethodInsnNode node = (MethodInsnNode) instruction;
				next = !valid(node, werbose);
				if (!next) {
					String fieldName = resolveFieldName(node.name);
					String entityName = resolveEntityName(node.owner);
					tuple = new QueryTuple(entityName, fieldName);
				}
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

	public static QueryTuple resolve(SerializedLambda lambda, boolean werbose) throws IOException {

		QueryTuple tuple;

		ClassVisitor visitor = new FieldVisitor(Opcodes.ASM5);
		ClassReader reader = new ClassReader(lambda.getImplClass());
		reader.accept(visitor, ZERO_FLAGS);
		ClassNode node = new ClassNode(Opcodes.ASM5);
		reader.accept(node, ZERO_FLAGS);
		@SuppressWarnings("unchecked")
		List<MethodNode> methods = node.methods;
		if (Objects.nonNull(methods)) {
			MethodNode methodNode = methods.stream().filter(c -> validate(c, lambda)).findFirst().get();
			printWerbose(werbose, c -> System.out.format("%s %s", methodNode.name, methodNode.desc));
			methodNode.visitCode();
			InsnList instructions = methodNode.instructions;
			tuple = resolve(instructions, werbose);
		} else {
			throw new IOException("Unresolvabla field name");
		}

		return tuple;
	}
}
