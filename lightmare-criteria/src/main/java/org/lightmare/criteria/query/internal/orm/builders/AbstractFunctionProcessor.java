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
package org.lightmare.criteria.query.internal.orm.builders;

import java.io.Serializable;
import java.util.function.Consumer;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.ORMFunction;
import org.lightmare.criteria.query.internal.orm.links.Operators.Brackets;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Implementation of
 * {@link org.lightmare.criteria.query.internal.orm.ORMFunction} for functional
 * expression processing
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * 
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 * @param <O>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
public abstract class AbstractFunctionProcessor<T, Q extends QueryStream<T, ? super Q>, O extends QueryStream<Object[], ? super O>>
        extends AbstractQueryStream<T, Q, O> implements ORMFunction<T> {

    protected QueryTuple functionTuple;

    protected AbstractFunctionProcessor(final LayerProvider provider, final Class<T> entityType) {
        super(provider, entityType);
    }

    /**
     * Generates {@link org.lightmare.criteria.tuples.QueryTuple} by field name
     * 
     * @param field
     */
    private void generateByField(Serializable field) {
        functionTuple = resolve(field);
        appendFieldName(functionTuple);
    }

    /**
     * Generates function expression from value
     * 
     * @param value
     */
    private void generateByValue(Object value) {
        appendBody(value);
    }

    /**
     * Generates functional expression
     * 
     * @param value
     */
    private void generate(Object value) {

        if (value instanceof EntityField<?, ?>) {
            ObjectUtils.cast(value, Serializable.class, this::generateByField);
        } else {
            generateByValue(value);
        }
    }

    /**
     * Starts functional expression
     * 
     * @param operator
     */
    private void startFunction(String operator) {
        appendBody(operator).appendBody(Brackets.OPEN);
    }

    /**
     * End function expression
     */
    private void endFunction() {
        appendBody(Brackets.CLOSE);
    }

    /**
     * Adds numeric function body
     * 
     * @param x
     * @param operator
     */
    private void appendNumericFunction(Object x, String operator) {

        startFunction(operator);
        generate(x);
        endFunction();
    }

    /**
     * Adds numeric function
     * 
     * @param x
     * @param y
     * @param operator
     */
    private void appendNumericFunction(Object x, Object y, String operator) {

        startFunction(operator);
        generate(x);
        appendBody(StringUtils.COMMA).appendBody(StringUtils.SPACE);
        generate(y);
        endFunction();
    }

    /**
     * Validates and generates {@link String} function query part
     * 
     * @param value
     */
    private void appendAndGenerate(Object value) {
        appendBody(StringUtils.COMMA).appendBody(StringUtils.SPACE);
        generate(value);
    }

    /**
     * Adds text function
     * 
     * @param x
     * @param y
     * @param z
     * @param operator
     */
    private void appendTextFunction(Object x, Object y, Object z, String operator) {

        startFunction(operator);
        generate(x);
        Consumer<Object> appendMethod = this::appendAndGenerate;
        ObjectUtils.nonNull(y, appendMethod);
        ObjectUtils.nonNull(z, appendMethod);
        endFunction();
    }

    @Override
    public ORMFunction<T> operateDate(String operator) {
        appendBody(operator);
        return this;
    }

    @Override
    public <S, F> ORMFunction<T> operateNumeric(EntityField<S, F> x, String operator) {
        appendNumericFunction(x, operator);
        return this;
    }

    @Override
    public ORMFunction<T> operateNumeric(Object x, Object y, String operator) {
        appendNumericFunction(x, y, operator);
        return this;
    }

    @Override
    public ORMFunction<T> operateText(String operator, Object x, Object y, Object z) {
        appendTextFunction(x, y, z, operator);
        return this;
    }

    /**
     * Appends pattern with space character if valid
     * 
     * @param pattern
     */
    private void appendPatternTail(Object pattern) {
        appendBody(StringUtils.SPACE);
        generate(pattern);
    }

    /**
     * Appends last parameter with space character if valid
     * 
     * @param y
     */
    private void appendLastTextParameter(Object y) {
        appendBody(StringUtils.SPACE);
        generate(y);
    }

    @Override
    public ORMFunction<T> generateText(String function, String prefix, Object x, String pattern, Object y) {

        startFunction(function);
        StringUtils.valid(prefix, this::appendBody);
        ObjectUtils.nonNull(x, this::generate);
        ObjectUtils.nonNull(pattern, this::appendPatternTail);
        ObjectUtils.nonNull(y, this::appendLastTextParameter);
        endFunction();

        return this;
    }
}
