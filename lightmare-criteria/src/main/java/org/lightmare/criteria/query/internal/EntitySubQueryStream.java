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

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.ParentField;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.builders.AbstractQueryStream;
import org.lightmare.criteria.query.internal.jpa.subqueries.AbstractSubQueryStream;
import org.lightmare.criteria.query.internal.jpa.subqueries.SubQueryStream;

/**
 * Implementation of {@link AbstractSubQueryStream} for sub query generation
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

    // ========================= Entity and parent method composers =========//

    @Override
    public <F> SubQueryStream<S, T> operatePr(EntityField<S, F> sfield, ParentField<T, F> field, String operator) {
        appendOperator();
        oppField(sfield, field, operator);

        return this;
    }

    @Override
    public <F> SubQueryStream<S, T> operateCollectionPr(EntityField<S, F> sfield, ParentField<T, Collection<F>> field,
            String operator) {
        appendOperator();
        oppCollectionField(sfield, field, operator);

        return this;
    }

    // ========================= select method composers ====================//

    @Override
    public <F> QueryStream<Object[]> select(EntityField<S, F> field) {
        return subSelectAll(field);
    }

    @Override
    public <F> QueryStream<F> selectOne(EntityField<S, F> field) {
        return subSelectOne(field);
    }
}
