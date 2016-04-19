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
package org.lightmare.criteria.query.providers.jpa;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.orm.builders.AbstractQueryStream;
import org.lightmare.criteria.query.orm.builders.SelectStream;
import org.lightmare.criteria.query.orm.subqueries.EntitySubQueryStream;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Implementation of
 * {@link org.lightmare.criteria.query.orm.subqueries.EntitySubQueryStream}
 * for JPA sub query generation
 * 
 * @author Levan Tsinadze
 *
 * @param <S>
 *            sub query entity type parameter
 * @param <T>
 *            entity type parameter
 */
class JpaSubQueryStream<S, T> extends EntitySubQueryStream<S, T, JpaQueryStream<S>, JpaQueryStream<Object[]>>
        implements JpaQueryStream<S> {

    public JpaSubQueryStream(final AbstractQueryStream<T, ?, ?> parent, Class<S> type) {
        super(parent, type);
    }

    public JpaSubQueryStream(AbstractQueryStream<T, ?, ?> parent, String alias, Class<S> type) {
        super(parent, alias, type);
    }

    @Override
    public <E, L extends LambdaStream<E, ? super L>> L initJoinQuery(String alias, Class<E> joinType) {
        L joinQuery = JpaUtils.initJoinQuery(this, alias, joinType);
        return joinQuery;
    }

    @Override
    public <E, L extends QueryStream<E, ? super L>> L initSubQuery(Class<E> subType) {
        L subQuery = JpaUtils.initSubQuery(this, subType);
        return subQuery;
    }

    @Override
    public <E> SelectStream<S, E, ?, ?> initSelectQuery(Class<E> selectType) {
        return new JpaSubSelectStream<S, E>(this, selectType);
    }

    // =========================embedded=field=queries=======================//

    @Override
    public <F> JpaQueryStream<S> embedded(EntityField<S, F> field, QueryConsumer<F, JpaQueryStream<F>> consumer) {

        QueryTuple tuple = compose(field);
        Class<F> type = tuple.getFieldGenericType();
        String embeddedName = tuple.getFieldName();
        JpaQueryStream<F> embeddedQuery = new JpaEmbeddedStream<>(this, type, embeddedName);
        acceptAndCall(consumer, embeddedQuery);

        return this;
    }
}
