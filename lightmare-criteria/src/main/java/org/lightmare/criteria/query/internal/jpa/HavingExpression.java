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
package org.lightmare.criteria.query.internal.jpa;

import org.lightmare.criteria.query.internal.jpa.links.Operators;

/**
 * Covers HAVING clause
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface HavingExpression<T> {

    <N extends Number> HavingExpression<T> operate(String operator, N value);

    <N extends Number> HavingExpression<T> operate(String operator, N value1, N value2);

    default <N extends Number> HavingExpression<T> equal(N value) {
        return operate(Operators.EQ, value);
    }

    default <N extends Number> HavingExpression<T> notEqual(N value) {
        return operate(Operators.EQ, value);
    }

    default <N extends Number> HavingExpression<T> gt(N value) {
        return operate(Operators.GREATER, value);
    }

    default <N extends Number> HavingExpression<T> greaterThen(N value) {
        return gt(value);
    }

    default <N extends Number> HavingExpression<T> lt(N value) {
        return operate(Operators.LESS, value);
    }

    default <N extends Number> HavingExpression<T> lessThen(N value) {
        return lt(value);
    }

    default <N extends Number> HavingExpression<T> ge(N value) {
        return operate(Operators.GREATER_OR_EQ, value);
    }

    default <N extends Number> HavingExpression<T> greaterThenOrEqualTo(N value) {
        return ge(value);
    }

    default <N extends Number> HavingExpression<T> le(N value) {
        return operate(Operators.LESS_OR_EQ, value);
    }

    default <N extends Number> HavingExpression<T> lessThenOrEqualTo(N value) {
        return le(value);
    }

    default <N extends Number> HavingExpression<T> between(N value1, N value2) {
        return operate(Operators.BETWEEN, value1, value2);
    }

    default <N extends Number> HavingExpression<T> notBetween(N value1, N value2) {
        return operate(Operators.NOT_BETWEEN, value1, value2);
    }
}
