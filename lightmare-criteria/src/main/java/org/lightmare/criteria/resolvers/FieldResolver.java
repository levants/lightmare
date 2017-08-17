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

import java.util.List;
import java.util.Objects;

import org.lightmare.criteria.cache.MethodCache;
import org.lightmare.criteria.lambda.LambdaInfo;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.tuples.ResolverTuple;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Resolver class to resolve field and entity name from lambda expression for
 * JPA query directly by {@link org.lightmare.criteria.lambda.LambdaInfo}
 * parameters or by <b><i> byetecode </i></b> analysis
 * 
 * @author Levan Tsinadze
 *
 */
public class FieldResolver extends DirectFieldResolver {

    private static final String THIS_PT = "this";

    // Debug messages
    private static final String DEBUG_MESSAGE_BYT = "Resolved from bytecode";

    // Error message
    private static final String UNRESOLVABLE_FIELD_ERROR = "Unresolvable field: ";

    /**
     * Removes semicolons from entity name
     * 
     * @param variable
     * @return {@link String} entity name
     */
    private static String clearEntityName(LocalVariableNode variable) {
        return Type.getType(variable.desc).getClassName();
    }

    /**
     * Validates if passed {@link org.objectweb.asm.LocalVariableNode} points to
     * <code>this</code> object
     * 
     * @param variable
     * @return <code>boolean</code> validation result
     */
    private static boolean pointsTo(LocalVariableNode variable) {
        return THIS_PT.equals(variable.name);
    }

    /**
     * Resolves entity name from local variable
     * 
     * @param variable
     * @return {@link String} entity name
     */
    private static String resolveEntityName(LocalVariableNode variable) {
        return ObjectUtils.ifIsValid(variable, FieldResolver::pointsTo, FieldResolver::clearEntityName);
    }

    /**
     * Gets entity name from {@link org.objectweb.asm.tree.LocalVariableNode}
     * retrieved as {@link java.util.List} first element
     * 
     * @param variables
     * @return {@link String} entity name
     */
    private static String resolveEntityName(List<LocalVariableNode> variables) {

        String entityName;

        LocalVariableNode variable = CollectionUtils.getFirst(variables);
        entityName = resolveEntityName(variable);

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
        entityName = ObjectUtils.ifIsValid(variables, CollectionUtils::valid, FieldResolver::resolveEntityName);

        return entityName;
    }

    /**
     * Resolves field name from {@link org.objectweb.asm.tree.MethodNode}
     * instance
     * 
     * @param node
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for resolved
     *         field and query part
     */
    private static QueryTuple resolveFromMethod(MethodNode node) {

        QueryTuple tuple;

        ResolverTuple<MethodNode> resolverTyple = ResolverTuple.of(node.desc, node.name, node);
        tuple = resolveFromTuple(resolverTyple, FieldResolver::resolveEntityName);

        return tuple;
    }

    /**
     * Validates method and signature for field resolver
     * 
     * @param methodNode
     * @param lambda
     * @return </code>boolean</code> validation result
     */
    private static boolean validateMethod(MethodNode methodNode, LambdaInfo lambda) {

        boolean valid;

        String lambdaName = lambda.getImplMethodName();
        String lambdaSignature = lambda.getImplMethodSignature();
        valid = (Objects.equals(methodNode.name, lambdaName) && Objects.equals(methodNode.desc, lambdaSignature));

        return valid;
    }

    /**
     * Resolves entity parameters from {@link org.objectweb.asm.tree.MethodNode}
     * and instructions
     * 
     * @param methodNode
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} from method
     */
    private static QueryTuple resolveRecursively(MethodNode methodNode) {
        return ObjectUtils.callOrInit(methodNode, FieldResolver::resolveFromMethod,
                FieldResolver::getAndResolveFromIns);
    }

    /**
     * Resolved field name, getter method name and entity type from lambda
     * argument by <b><i>bytecode</i></b> analysis
     * 
     * @param lambda
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for resolved
     *         field and query part
     */
    private static QueryTuple resolveFromBytecode(LambdaInfo lambda) {

        QueryTuple tuple;

        List<MethodNode> methods = MethodCache.getMethods(lambda);
        if (Objects.nonNull(methods)) {
            MethodNode methodNode = CollectionUtils.getFirstValid(methods, c -> validateMethod(c, lambda));
            tuple = resolveRecursively(methodNode);
            debug(DEBUG_MESSAGE_BYT, tuple);
        } else {
            String error = StringUtils.concat(UNRESOLVABLE_FIELD_ERROR, lambda);
            throw new RuntimeException(error);
        }

        return tuple;
    }

    /**
     * Resolved field name, getter method name and entity type from lambda
     * function parameters from {@link org.lightmare.criteria.lambda.LambdaInfo}
     * or from <b><i> bytecode </i></b> analysis
     * 
     * @param lambda
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for resolved
     *         field and query part
     */
    private static QueryTuple chooseAndResolve(LambdaInfo lambda) {
        return ObjectUtils.callOrInit(lambda, FieldResolver::resolveDirectly, FieldResolver::resolveFromBytecode);
    }

    /**
     * Resolved field name, getter method name and entity type from lambda
     * function parameters
     * 
     * @param lambda
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for resolved
     *         field and query part
     */
    public static QueryTuple resolve(LambdaInfo lambda) {

        QueryTuple tuple = chooseAndResolve(lambda);
        ObjectUtils.nonNull(tuple, FieldResolver::setGenericData);

        return tuple;
    }
}
