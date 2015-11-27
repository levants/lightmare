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

import java.util.Objects;

import org.lightmare.criteria.functions.HavingConsumer;
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

    HavingExpression<T> appendHaving(Object operator);

    HavingExpression<T> operate(String operator, Number value);

    HavingExpression<T> operate(String operator, Number value1, Number value2);

    default HavingExpression<T> equal(Number value) {
        return operate(Operators.EQ, value);
    }

    default HavingExpression<T> notEqual(Number value) {
        return operate(Operators.EQ, value);
    }

    default HavingExpression<T> gt(Number value) {
        return operate(Operators.GREATER, value);
    }

    default HavingExpression<T> greaterThen(Number value) {
        return gt(value);
    }

    default HavingExpression<T> lt(Number value) {
        return operate(Operators.LESS, value);
    }

    default HavingExpression<T> lessThen(Number value) {
        return lt(value);
    }

    default HavingExpression<T> ge(Number value) {
        return operate(Operators.GREATER_OR_EQ, value);
    }

    default HavingExpression<T> greaterThenOrEqualTo(Number value) {
        return ge(value);
    }

    default HavingExpression<T> le(Number value) {
        return operate(Operators.LESS_OR_EQ, value);
    }

    default HavingExpression<T> lessThenOrEqualTo(Number value) {
        return le(value);
    }

    default HavingExpression<T> between(Number value1, Number value2) {
        return operate(Operators.BETWEEN, value1, value2);
    }

    default HavingExpression<T> notBetween(Number value1, Number value2) {
        return operate(Operators.NOT_BETWEEN, value1, value2);
    }

    default HavingExpression<T> and() {
        return appendHaving(Operators.AND);
    }

    default HavingExpression<T> or() {
        return appendHaving(Operators.OR);
    }

    default HavingExpression<T> openBracket() {
        return appendHaving(Operators.OPEN_BRACKET);
    }

    default HavingExpression<T> closeBracket() {
        return appendHaving(Operators.CLOSE_BRACKET);
    }

    default HavingExpression<T> brackets(HavingConsumer<T> consumer) {

        if (Objects.nonNull(consumer)) {
            openBracket();
            consumer.accept(this);
            closeBracket();
        }

        return this;
    }
}
