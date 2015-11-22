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

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Abstract class to process functional expression
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
abstract class AbstractFunctionExpression<T> extends AbstractFunctionProcessor<T> {

    protected AbstractFunctionExpression(final EntityManager em, final Class<T> entityType, final String alias) {
        super(em, entityType, alias);
    }

    /**
     * Starts function expression
     * 
     * @param function
     * @param operator
     */
    private void startFunctionExpression(FunctionConsumer<T> function, String operator) {

        newLine();
        appendOperator();
        function.accept(this);
        appendBody(operator);
    }

    @Override
    public <F> QueryStream<T> operateColumn(FunctionConsumer<T> function, String operator, EntityField<T, F> field) {

        startFunctionExpression(function, operator);
        QueryTuple tuple = compose(field);
        appendColumn(tuple);

        return this;
    }

    @Override
    public QueryStream<T> operateFunction(FunctionConsumer<T> function, String operator, Object value) {

        startFunctionExpression(function, operator);
        if (functionTuple == null) {
            appendBody(value);
        } else {
            oppWithParameter(functionTuple, value, body);

        }

        return this;
    }

    @Override
    public QueryStream<T> operateFunctions(FunctionConsumer<T> function1, FunctionConsumer<T> function2,
            String operator) {

        startFunctionExpression(function1, operator);
        function2.accept(this);

        return this;
    }
}