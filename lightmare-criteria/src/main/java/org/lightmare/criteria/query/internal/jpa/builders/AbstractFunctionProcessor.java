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

    protected AbstractFunctionProcessor(final EntityManager em, final Class<T> entityType, final String alias) {
        super(em, entityType, alias);
    }

    private void generateByField(Serializable field) {

        QueryTuple tuple = compose(field);
        appendFieldName(tuple);
    }

    private void generateByValue(Object value) {
        appendBody(value);
    }

    private void generate(Object value) {

        if (value instanceof EntityField<?, ?>) {
            Serializable field = ObjectUtils.cast(value);
            generateByField(field);
        } else {
            generateByValue(value);
        }
    }

    private void startFunction(String operator) {

        newLine();
        appendBody(operator);
        openBracket();
    }

    private void appendNumericFunction(Object x, String operator) {

        startFunction(operator);
        generate(x);
        closeBracket();
    }

    private void appendNumericFunction(Object x, Object y, String operator) {

        startFunction(operator);
        generate(x);
        appendBody(StringUtils.COMMA).appendBody(StringUtils.SPACE);
        generate(y);
        closeBracket();
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
