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
package org.lightmare.criteria.query.internal.jpa.builders;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.internal.jpa.JPAFunction;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Implementation of {@link JPAFunction} for functional expression processing
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public abstract class AbstractFunctionProcessor<T> extends AbstractQueryStream<T> implements JPAFunction<T> {

    protected QueryTuple functionTuple;

    private static final char OPEN_PRACKET = '(';

    protected AbstractFunctionProcessor(final EntityManager em, final Class<T> entityType, final String alias) {
        super(em, entityType, alias);
    }

    /**
     * Generates {@link QueryTuple} by field name
     * 
     * @param field
     */
    private void generateByField(Serializable field) {
        functionTuple = compose(field);
        appendFieldName(functionTuple);
    }

    /**
     * Generates function al expression from value
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
            Serializable field = ObjectUtils.cast(value);
            generateByField(field);
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
        appendBody(operator).appendBody(OPEN_PRACKET);
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
        closeBracket();
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
        closeBracket();
    }

    @Override
    public JPAFunction<T> operateDate(String operator) {
        appendBody(operator);
        return this;
    }

    @Override
    public <S, F> JPAFunction<T> operateNumeric(EntityField<S, F> x, String operator) {
        appendNumericFunction(x, operator);
        return this;
    }

    @Override
    public JPAFunction<T> operateNumeric(Object x, Object y, String operator) {
        appendNumericFunction(x, y, operator);
        return this;
    }
}