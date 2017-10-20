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
import org.lightmare.criteria.query.layers.LayerProvider;
import org.lightmare.criteria.query.orm.builders.AbstractQueryStream;
import org.lightmare.criteria.query.orm.builders.SelectStream;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryStream} for JPA
 * queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
class JpaEntityQueryStream<T> extends AbstractJpaQueryWrapper<T> implements JpaQueryStream<T> {

    protected JpaEntityQueryStream(final LayerProvider provider, final Class<T> entityType) {
        super(provider, entityType);
    }

    @Override
    public <E, S extends LambdaStream<E, ? super S>> S initJoinQuery(String alias, Class<E> joinType) {
        S joinQuery = JpaUtils.initJoinQuery(this, alias, joinType);
        return joinQuery;
    }

    @Override
    public <E, S extends QueryStream<E, ? super S>> S initSubQuery(Class<E> subType) {
        S subQuery = JpaUtils.initSubQuery(this, subType);
        return subQuery;
    }

    @Override
    public <E> SelectStream<T, E, ?, ?> initSelectQuery(Class<E> selectType) {

        SelectStream<T, E, ?, ?> stream;

        AbstractQueryStream<T, ?, ?> parent = this;
        stream = new JpaSelectStream<>(parent, selectType);

        return stream;
    }

    // =========================embedded=field=queries=======================//

    @Override
    public <F> JpaQueryStream<T> embedded(EntityField<T, F> field, QueryConsumer<F, JpaQueryStream<F>> consumer) {

        QueryTuple tuple = compose(field);
        Class<F> type = tuple.getFieldGenericType();
        String embeddedName = tuple.getFieldName();
        JpaQueryStream<F> embeddedQuery = new JpaEmbeddedStream<>(this, type, embeddedName);
        acceptAndCall(consumer, embeddedQuery);

        return this;
    }
}
