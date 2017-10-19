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
import org.lightmare.criteria.query.orm.builders.AbstractQueryStream;
import org.lightmare.criteria.query.orm.builders.SelectStream;

/**
 * Query builder for JPA SELECT expressions
 * 
 * @author Levan Tsinadze
 *
 * @param <E>
 *            select type parameter
 * @param <T>
 *            entity type parameter
 */
public class JpaSelectStream<E, T> extends SelectStream<T, E, JpaQueryStream<E>, JpaQueryStream<Object[]>>
        implements JpaQueryStream<E> {

    private static final long serialVersionUID = 1L;

    protected JpaSelectStream(AbstractQueryStream<T, ?, ?> stream, Class<E> type) {
        super(stream, type);
    }

    @Override
    public <F> JpaQueryStream<E> embedded(EntityField<E, F> field, QueryConsumer<F, JpaQueryStream<F>> consumer) {
        return this;
    }
}
