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
package org.lightmare.criteria.query.internal;

import java.io.Serializable;
import java.util.function.BiConsumer;

import org.lightmare.criteria.query.QueryResolver;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryResolver} of JPA
 * criteria queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface CriteriaQueryResolver<T> extends QueryResolver<T> {

    /**
     * Operates on resolved field by expression with other resolved field
     * 
     * @param tuple1
     * @param tuple2
     * @param expression
     */
    default void operate(QueryTuple tuple1, QueryTuple tuple2, BiConsumer<QueryTuple, QueryTuple> expression) {
        expression.accept(tuple1, tuple2);
    }

    /**
     * Resolves and operates on fields by passed expression
     * 
     * @param field1
     * @param field2
     * @param expression
     * @return {@link org.lightmare.criteria.tuples.QueryTuple} for passed first
     *         lambda function
     */
    default <V> QueryTuple resolveAndOperateField(Serializable field1, Serializable field2,
            BiConsumer<QueryTuple, QueryTuple> expression) {

        QueryTuple tuple1 = compose(field1);
        QueryTuple tuple2 = compose(field2);
        operate(tuple1, tuple2, expression);

        return tuple1;
    }
}
