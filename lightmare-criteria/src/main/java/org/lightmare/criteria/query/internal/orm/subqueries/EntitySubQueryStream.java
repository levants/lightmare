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
package org.lightmare.criteria.query.internal.orm.subqueries;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.builders.AbstractQueryStream;
import org.lightmare.criteria.query.internal.orm.links.Aggregates;

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
public abstract class EntitySubQueryStream<S, T, Q extends QueryStream<S, ? super Q>, O extends QueryStream<Object[], ? super O>>
        extends AbstractSubQueryStream<S, T, Q, O> {

    public EntitySubQueryStream(AbstractQueryStream<T, ?, ?> parent, Class<S> type) {
        super(parent, type);
    }

    public EntitySubQueryStream(AbstractQueryStream<T, ?, ?> parent, String alias, Class<S> type) {
        super(parent, alias, type);
    }

    // ========================= select method composers ====================//

    @Override
    public <F> O select(EntityField<S, F> field) {
        return subSelectAll(field);
    }

    @Override
    public <F, L extends QueryStream<F, ? super L>> L selectType(EntityField<S, F> field) {
        return subSelectOne(field);
    }

    @Override
    public <F, R extends Number, L extends QueryStream<R, ? super L>> L aggregate(EntityField<S, F> field,
            Aggregates function, Class<R> type) {
        return subAggregate(field, function, type);
    }
}
