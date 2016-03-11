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
package org.lightmare.criteria.query.providers;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.QueryExpression;
import org.lightmare.criteria.query.internal.orm.links.Operators;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Main interface with query construction methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 */
public interface JpaQueryStream<T> extends QueryStream<T, JpaQueryStream<T>>, QueryExpression<T> {

    /**
     * Generates query part for embedded entity fields
     * 
     * @param field
     * @param consumer
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         current instance
     */
    <F> JpaQueryStream<T> embedded(EntityField<T, F> field, QueryConsumer<F, JpaQueryStream<F>> consumer);

    default JpaQueryStream<T> appendOperator(Object operator) {
        return appendBody(operator);
    }

    @Override
    default JpaQueryStream<T> and() {
        return appendOperator(Operators.AND);
    }

    @Override
    default JpaQueryStream<T> or() {
        return appendOperator(Operators.OR);
    }

    /**
     * Appends query body with operator and value
     * 
     * @param value
     * @param operator
     * @return {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         current instance
     */
    default JpaQueryStream<T> appendOperator(Object value, Object operator) {
        return appendBody(value).appendBody(StringUtils.SPACE).appendBody(operator);
    }

    default JpaQueryStream<T> where() {
        return this;
    }

    @Override
    default JpaQueryStream<T> openBracket() {
        return QueryExpression.super.openBracket();
    }
}