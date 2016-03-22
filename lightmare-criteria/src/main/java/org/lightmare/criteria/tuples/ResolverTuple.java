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
package org.lightmare.criteria.tuples;

import org.lightmare.criteria.lambda.LambdaInfo;

/**
 * Tuple for entity field (column) resolver
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            type parameter for entity name resolvers
 */
public class ResolverTuple<T> {

    private final String desc;

    private final String name;

    private final T type;

    private ResolverTuple(final String desc, final String name, final T type) {
        this.desc = desc;
        this.name = name;
        this.type = type;
    }

    /**
     * Generates instance from method description
     * 
     * @param desc
     * @param name
     * @param type
     * @return @link org.lightmare.criteria.tuples.ResolverTuple} from method
     *         description
     */
    public static <T> ResolverTuple<T> of(final String desc, final String name, final T type) {
        return new ResolverTuple<T>(desc, name, type);
    }

    /**
     * Generates instance from serialized lambda parameters
     * 
     * @param lambda
     * @param type
     * @return {@link org.lightmare.criteria.tuples.ResolverTuple} from
     *         serialized lambda parameters
     */
    public static <T> ResolverTuple<T> of(final LambdaInfo lambda, final T type) {
        return new ResolverTuple<T>(lambda.getImplMethodSignature(), lambda.getImplMethodName(), type);
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public T getType() {
        return type;
    }
}
