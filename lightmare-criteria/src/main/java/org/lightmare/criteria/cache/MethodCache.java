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
     */

    private static List<MethodNode> resolveMethods(String typeName) {

	List<MethodNode> methods;

	try {
	    ClassReader reader = CachedClassReader.get(typeName);
	    ClassNode node = new ClassNode(Opcodes.ASM5);
	    reader.accept(node, ZERO_FLAGS);
	    methods = ObjectUtils.cast(node.methods);
	} catch (IOException ex) {
	    throw new RuntimeException(ex);
	}

	return methods;
    }

    /**
     * Gets {@link List} of resolved {@link MethodNode}s from cache by class
     * file name or if absent resolves and caches
     * 
     * @param typeName
     * @return {@link List} of {@link MethodNode} methods
     */
    public static List<MethodNode> getMethods(String typeName) {

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
     */
    public static List<MethodNode> getMethods(LambdaData lambda) {

	List<MethodNode> methods;

	String typeName = lambda.getImplClass();
	methods = getMethods(typeName);

	return methods;
    }
}
