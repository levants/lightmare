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
package org.lightmare.criteria.query.internal;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.internal.orm.builders.AbstractQueryStream;
import org.lightmare.criteria.query.internal.orm.links.Aggregates;
import org.lightmare.criteria.query.internal.orm.subqueries.AbstractSubQueryStream;
import org.lightmare.criteria.query.providers.JpaQueryStream;

/**
 * Implementation of
 * {@link org.lightmare.criteria.query.internal.orm.subqueries.AbstractSubQueryStream}
 * for sub query generation
 * 
 * @author Levan Tsinadze
 *
 * @param <S>
 *            entity type for generated query
 * @param <T>
 *            parent entity type for generated query
 */
class EntitySubQueryStream<S, T> extends AbstractSubQueryStream<S, T> {

    protected EntitySubQueryStream(AbstractQueryStream<T> parent, Class<S> type) {
        super(parent, type);
    }

    protected EntitySubQueryStream(AbstractQueryStream<T> parent, String alias, Class<S> type) {
        super(parent, alias, type);
    }

    // ========================= select method composers ====================//

    @Override
    public <F> JpaQueryStream<Object[]> select(EntityField<S, F> field) {
        return subSelectAll(field);
    }

    @Override
    public <F> JpaQueryStream<F> selectType(EntityField<S, F> field) {
        return subSelectOne(field);
    }

    @Override
    public <F, R extends Number> JpaQueryStream<R> aggregate(EntityField<S, F> field, Aggregates function,
            Class<R> type) {
        return subAggregate(field, function, type);
    }
}
