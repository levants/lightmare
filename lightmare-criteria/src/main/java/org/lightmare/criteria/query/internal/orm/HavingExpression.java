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
package org.lightmare.criteria.query.internal.orm;

import java.util.Objects;
import java.util.function.Consumer;

import org.lightmare.criteria.functions.HavingConsumer;
import org.lightmare.criteria.query.internal.orm.links.Operators;
import org.lightmare.criteria.query.internal.orm.links.Operators.Brackets;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Covers HAVING clause
 * 
 * @author Levan Tsinadze
 *
 */
public interface HavingExpression {

    /**
     * Generates HAVING clause for JPA query
     * 
     * @param operator
     * @return {@link org.lightmare.criteria.query.internal.orm.HavingExpression}
     *         current instance
     */
    HavingExpression appendHaving(Object operator);

    /**
     * Replaces last character in HAVING expression
     * 
     * @param supposed
     * @return {@link org.lightmare.criteria.query.internal.orm.HavingExpression}
     *         current instance
     */
    HavingExpression replaceHavingNewLine(char supposed);

    /**
     * Generates HAVING clause for JPA query with parameter and operator
     * 
     * @param operator
     * @param value
     * @return {@link org.lightmare.criteria.query.internal.orm.HavingExpression}
     *         current instance
     */
    HavingExpression operate(String operator, Number value);

    /**
     * Generates HAVING clause for JPA query with parameters and operator
     * 
     * @param operator
     * @param value1
     * @param value2
     * @return {@link org.lightmare.criteria.query.internal.orm.HavingExpression}
     *         current instance
     */
    HavingExpression operate(String operator, Number value1, Number value2);

    default HavingExpression equal(Number value) {
        return operate(Operators.EQ, value);
    }

    default HavingExpression notEqual(Number value) {
        return operate(Operators.EQ, value);
    }

    default HavingExpression gt(Number value) {
        return operate(Operators.GREATER, value);
    }

    default HavingExpression greaterThan(Number value) {
        return gt(value);
    }

    default HavingExpression lt(Number value) {
        return operate(Operators.LESS, value);
    }

    default HavingExpression lessThan(Number value) {
        return lt(value);
    }

    default HavingExpression ge(Number value) {
        return operate(Operators.GREATER_OR_EQ, value);
    }

    default HavingExpression greaterThanOrEqualTo(Number value) {
        return ge(value);
    }

    default HavingExpression le(Number value) {
        return operate(Operators.LESS_OR_EQ, value);
    }

    default HavingExpression lessThanOrEqualTo(Number value) {
        return le(value);
    }

    default HavingExpression between(Number value1, Number value2) {
        return operate(Operators.BETWEEN, value1, value2);
    }

    default HavingExpression notBetween(Number value1, Number value2) {
        return operate(Operators.NOT_BETWEEN, value1, value2);
    }

    default HavingExpression and() {
        return appendHaving(Operators.AND);
    }

    default HavingExpression or() {
        return appendHaving(Operators.OR);
    }

    default HavingExpression openBracket() {
        return appendHaving(Operators.OPEN_BRACKET);
    }

    default HavingExpression closeBracket() {
        return appendHaving(Operators.CLOSE_BRACKET);
    }

    default HavingExpression brackets(HavingConsumer consumer) {

        if (Objects.nonNull(consumer)) {
            openBracket();
            consumer.accept(this);
            replaceHavingNewLine(Brackets.CLOSE);
            appendHaving(StringUtils.NEWLINE);
        }

        return this;
    }

    /**
     * Generates AND / OR connector and sets brackets
     * 
     * @param connector
     * @param consumer
     * @return {@link org.lightmare.criteria.query.internal.orm.HavingExpression}
     *         current instance
     */
    default HavingExpression brackets(Consumer<HavingExpression> connector, HavingConsumer consumer) {

        HavingExpression stream;

        ObjectUtils.accept(connector, this);
        stream = brackets(consumer);

        return stream;
    }

    /**
     * AND clause in lambda expression manner
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.internal.orm.HavingExpression}
     *         current instance
     */
    default HavingExpression and(HavingConsumer consumer) {
        return brackets(HavingExpression::and, consumer);
    }

    /**
     * OR clause in lambda expression manner
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.internal.orm.HavingExpression}
     *         current instance
     */
    default HavingExpression or(HavingConsumer consumer) {
        return brackets(HavingExpression::or, consumer);
    }
}
