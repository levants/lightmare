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
package org.lightmare.criteria.query.providers.jpa;

import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.builders.AbstractQueryStream;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Utility class for JPA queries
 * 
 * @author Levan Tsinadze
 *
 */
abstract class JpaUtils {

    /**
     * Initializes {@link org.lightmare.criteria.query.LambdaStream}
     * implementation implementation for JOIN expression
     * 
     * @param parent
     * @param alias
     * @param joinType
     * @return L {@link org.lightmare.criteria.query.LambdaStream}
     *         implementation
     */
    protected static <P, E, S extends LambdaStream<E, ? super S>> S initJoinQuery(AbstractQueryStream<P, ?, ?> parent,
            String alias, Class<E> joinType) {

        S joinQuery;

        if (alias == null) {
            joinQuery = ObjectUtils.applyAndCast(joinType, c -> new JpaJoinProcessor<E, P>(parent, c));
        } else {
            joinQuery = ObjectUtils.applyAndCast(joinType, c -> new JpaJoinProcessor<E, P>(parent, alias, c));
        }

        return joinQuery;
    }

    /**
     * Initializes {@link org.lightmare.criteria.query.LambdaStream}
     * implementation implementation for sub query expressions
     * 
     * @param parent
     * @param subType
     * @return L {@link org.lightmare.criteria.query.LambdaStream}
     *         implementation
     */
    protected static <P, E, S extends QueryStream<E, ? super S>> S initSubQuery(AbstractQueryStream<P, ?, ?> parent,
            Class<E> subType) {
        S query = ObjectUtils.applyAndCast(subType, c -> new JpaSubQueryStream<E, P>(parent, c));
        return query;
    }
}
