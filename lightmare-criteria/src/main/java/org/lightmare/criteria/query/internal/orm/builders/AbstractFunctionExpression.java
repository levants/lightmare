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

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Abstract class to process functional expression
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 * @param <O>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
abstract class AbstractFunctionExpression<T, Q extends QueryStream<T, ? super Q>, O extends QueryStream<Object[], ? super O>>
        extends AbstractFunctionProcessor<T, Q, O> {

    protected AbstractFunctionExpression(final LayerProvider provider, final Class<T> entityType) {
        super(provider, entityType);
    }

    /**
     * Starts function expression
     * 
     * @param function
     * @param operator
     */
    protected void startFunctionExpression(FunctionConsumer<T> function, String operator) {

        newLine();
        appendOperator();
        function.accept(this);
        appendBody(operator);
    }

    @Override
    public <F> Q operateColumn(FunctionConsumer<T> function, String operator, EntityField<T, F> field) {

        Q stream = stream();

        startFunctionExpression(function, operator);
        QueryTuple tuple = resolve(field);
        appendColumn(tuple);

        return stream;
    }

    @Override
    public Q operateFunction(FunctionConsumer<T> function, String operator, Object value) {

        Q stream = stream();

        startFunctionExpression(function, operator);
        if (functionTuple == null) {
            appendBody(value);
        } else {
            oppWithParameter(functionTuple, value, body);
            newLine();
        }

        return stream;
    }

    @Override
    public Q operateFunctions(FunctionConsumer<T> function1, FunctionConsumer<T> function2, String operator) {

        Q stream = stream();

        startFunctionExpression(function1, operator);
        function2.accept(this);

        return stream;
    }
}
