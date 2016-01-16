package org.lightmare.criteria.resolvers;

import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.tuples.ResolverTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Abstract class to resolve entity entity and field from
 * {@link org.lightmare.criteria.lambda.LambdaInfo} parameters by byetecode
 * analysis
 * 
 * @author Levan Tsinadze
 *
 */
abstract class BytecodeFieldResolver extends AbstractFieldResolver {

    /**
     * Resolves field name from instruction instance
     * 
     * @param instruction
     * @param verbose
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for resolved
     *         field and query part
     */
    private static QueryTuple resolveFromInstruction(MethodInsnNode node) {

        QueryTuple tuple;

        ResolverTuple<String> resolverTyple = ResolverTuple.of(node.desc, node.name, node.owner);
        tuple = resolveFromTuple(resolverTyple, AbstractFieldResolver::resolveEntityName);

        return tuple;
    }

    /**
     * Resolves appropriated {@link org.lightmare.criteria.tuples.QueryTuple}
     * from instructions
     * 
     * @param instruction
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} from instruction
     */
    private static QueryTuple resolveValidInsNode(AbstractInsnNode instruction) {

        QueryTuple tuple;

        MethodInsnNode node = ObjectUtils.cast(instruction);
        tuple = resolveFromInstruction(node);

        return tuple;
    }

    /**
     * Validates if passed {@link org.objectweb.asm.tree.AbstractInsnNode} is
     * {@link org.objectweb.asm.tree.MethodInsnNode} instance
     * 
     * @param instruction
     * @return <code>boolean</code> validation result
     */
    private static boolean isInstructionNode(AbstractInsnNode instruction) {
        return (instruction instanceof MethodInsnNode);
    }

    /**
     * Validates if passed {@link org.objectweb.asm.tree.AbstractInsnNode} is
     * instance of {@link org.objectweb.asm.tree.MethodInsnNode} then resolves
     * appropriated {@link org.lightmare.criteria.tuples.QueryTuple} from it
     * 
     * @param instruction
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} from instruction
     */
    private static QueryTuple validateAndResolve(AbstractInsnNode instruction) {
        return ObjectUtils.ifValid(instruction, BytecodeFieldResolver::isInstructionNode,
                BytecodeFieldResolver::resolveValidInsNode);
    }

    /**
     * Resolves appropriated instructions for field recognition
     * 
     * @param instructions
     * @param verbose
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for resolved
     *         field and query part
     */
    private static QueryTuple resolveFromInstructions(InsnList instructions) {

        QueryTuple tuple = null;

        int size = instructions.size();
        AbstractInsnNode instruction;
        for (int i = CollectionUtils.FIRST_INDEX; (i < size && tuple == null); ++i) {
            instruction = instructions.get(i);
            tuple = validateAndResolve(instruction);
        }

        return tuple;
    }

    /**
     * Gets instructions from {@link org.objectweb.asm.tree.MethodNode} and
     * resolves entity field
     * 
     * @param methodNode
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} from
     *         instructions
     */
    protected static QueryTuple getAndResolveFromIns(MethodNode methodNode) {

        QueryTuple tuple;

        methodNode.visitCode();
        InsnList instructions = methodNode.instructions;
        tuple = resolveFromInstructions(instructions);

        return tuple;
    }
}
