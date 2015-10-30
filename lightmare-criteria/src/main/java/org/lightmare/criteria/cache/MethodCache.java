package org.lightmare.criteria.cache;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.lightmare.criteria.lambda.LambdaData;
import org.lightmare.criteria.resolvers.CachedClassReader;
import org.lightmare.utils.ObjectUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Cache for class methods
 * 
 * @author Levan Tsinadze
 *
 */
public class MethodCache {

    private static final ConcurrentMap<String, List<MethodNode>> METHOD_NODES = new ConcurrentHashMap<>();

    private static final int ZERO_FLAGS = 0;

    /**
     * Resolves {@link List} of {@link MethodNode} from passed class file name
     * 
     * @param typeName
     * @return {@link List} of {@link MethodNode}
     * @throws IOException
     */

    private static List<MethodNode> resolveMethods(String typeName) throws IOException {

	List<MethodNode> methods;

	ClassReader reader = CachedClassReader.get(typeName);
	ClassNode node = new ClassNode(Opcodes.ASM5);
	reader.accept(node, ZERO_FLAGS);
	methods = ObjectUtils.cast(node.methods);

	return methods;
    }

    /**
     * Gets {@link List} of resolved {@link MethodNode}s from cache by class
     * file name or if absent resolves and caches
     * 
     * @param typeName
     * @return {@link List} of {@link MethodNode} methods
     * @throws IOException
     */
    public static List<MethodNode> getMethods(String typeName) throws IOException {

	List<MethodNode> methods;

	if (METHOD_NODES.containsKey(typeName)) {
	    methods = METHOD_NODES.get(typeName);
	} else {
	    methods = resolveMethods(typeName);
	    METHOD_NODES.putIfAbsent(typeName, methods);
	}

	return methods;
    }

    /**
     * Gets {@link List} of resolved {@link MethodNode}s from cache by class
     * file name from passed {@link LambdaData} or if absent resolves and caches
     * 
     * @param lambda
     * @return {@link List} of {@link MethodNode} methods
     * @throws IOException
     */
    public static List<MethodNode> getMethods(LambdaData lambda) throws IOException {

	List<MethodNode> methods;

	String typeName = lambda.getImplClass();
	methods = getMethods(typeName);

	return methods;
    }
}
