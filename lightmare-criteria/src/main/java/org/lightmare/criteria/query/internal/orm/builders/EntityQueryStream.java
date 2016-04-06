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

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.FunctionConsumer;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.layers.LayerProvider;

/**
 * Query builder from setter method references
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 * @param <O>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
public abstract class EntityQueryStream<T, Q extends QueryStream<T, ? super Q>, O extends QueryStream<Object[], ? super O>>
        extends AbstractSelectStatements<T, Q, O> {

    protected EntityQueryStream(final LayerProvider provider, Class<T> entityType) {
        super(provider, entityType);
    }

    @Override
    public <F> Q operate(EntityField<T, F> field, String operator) {

        Q stream = stream();

        appendOperator();
        oppLine(field, operator);

        return stream;
    }

    @Override
    public <F> Q operate(EntityField<T, ? extends F> field, Object value, String operator) {

        Q stream = stream();

        appendOperator();
        oppLine(field, value, operator);

        return stream;
    }

    @Override
    public <F> Q operate(EntityField<T, ? extends F> field, Object value1, Object value2, String operator) {

        Q stream = stream();

        appendOperator();
        oppLine(field, value1, value2, operator);

        return stream;
    }

    @Override
    public <F> Q operate(EntityField<T, ? extends F> field, String operator1, Object value1, String operator2,
            Object value2) {

        Q stream = stream();

        appendOperator();
        oppLine(field, operator1, value1, operator2, value2);

        return stream;
    }

    @Override
    public <F> Q operateCollection(EntityField<T, F> field, Collection<F> values, String operator) {

        Q stream = stream();

        appendOperator();
        oppCollection(field, values, operator);

        return stream;
    }

    @Override
    public <S, F> Q operateCollection(Object value, EntityField<S, Collection<F>> field, String operator) {

        Q stream = stream();

        appendOperator();
        oppCollection(value, field, operator);

        return stream;
    }

    // ==========================Entity=self=method=composers================//

    @Override
    public <F, S> Q operate(EntityField<T, ? extends F> field1, EntityField<S, ? extends F> field2, String operator) {

        Q stream = stream();

        appendOperator();
        oppField(field1, field2, operator);

        return stream;
    }

    @Override
    public <F, E, S, L> Q operate(EntityField<T, ? extends F> field1, String operator1,
            EntityField<S, ? extends F> field2, String operator2, EntityField<L, E> field3) {

        Q stream = stream();

        appendOperator();
        oppField(field1, operator1, field2, operator2, field3);

        return stream;
    }

    @Override
    public <F, S> Q operate(EntityField<T, ? extends F> field1, EntityField<S, ? extends F> field2,
            EntityField<S, ? extends F> field3, String operator) {

        Q stream = stream();

        appendOperator();
        oppLine(field1, field2, field3, operator);

        return stream;
    }

    @Override
    public <F, S> Q operateCollection(EntityField<T, F> field1, EntityField<S, Collection<F>> field2, String operator) {

        Q stream = stream();

        appendOperator();
        oppCollectionField(field1, field2, operator);

        return stream;
    }

    // =========================operate=sub=queries==========================//

    /**
     * Creates
     * {@link org.lightmare.criteria.query.internal.orm.subqueries.SubQueryStream}
     * for instant {@link Class} entity type
     * 
     * @param type
     * @param consumer
     */
    protected <S, L extends QueryStream<S, ? super L>> void initSubQuery(Class<S> type, QueryConsumer<S, L> consumer) {

        L query = initSubQuery(type);
        acceptAndCall(consumer, query);
        closeBracket();
        newLine();
    }

    @Override
    public <S, L extends QueryStream<S, ? super L>> Q operateSubQuery(Class<S> type, QueryConsumer<S, L> consumer) {

        Q stream = stream();

        openBracket();
        initSubQuery(type, consumer);

        return stream;
    }

    @Override
    public <F, S, L extends QueryStream<S, ? super L>> Q operateSubQuery(EntityField<T, F> field, String operator,
            Class<S> type, QueryConsumer<S, L> consumer) {

        Q stream = stream();

        appendOperator();
        appSubQuery(field, operator);
        initSubQuery(type, consumer);

        return stream;
    }

    @Override
    public <F, S, L extends QueryStream<S, ? super L>> Q operateSubQuery(Object value, String operator, Class<S> type,
            QueryConsumer<S, L> consumer) {

        Q stream = stream();

        appendOperator();
        appendOperator(value, operator);
        openBracket();
        initSubQuery(type, consumer);

        return stream;
    }

    @Override
    public <F, S, L extends QueryStream<S, ? super L>> Q operateFunctionWithSubQuery(FunctionConsumer<T> function,
            String operator, Class<S> type, QueryConsumer<S, L> consumer) {

        Q stream = stream();

        startFunctionExpression(function, operator);
        openBracket();
        initSubQuery(type, consumer);

        return stream;
    }

    @Override
    public <F, S, L extends QueryStream<S, ? super L>> Q operateSubQuery(String operator, Class<S> type,
            QueryConsumer<S, L> consumer) {

        Q stream = stream();

        appendOperator();
        appendBody(operator);
        openBracket();
        initSubQuery(type, consumer);

        return stream;
    }

    // =================================Set=Clause===========================//

    @Override
    public <F> Q set(EntityField<T, F> field, F value) {

        Q stream = stream();
        setOpp(field, value);

        return stream;
    }

    // =================================Order=By=============================//

    @Override
    public <F> Q order(String dir, EntityField<T, F> field) {

        Q stream = stream();
        setOrder(dir, new EntityField[] { field });

        return stream;
    }
}
