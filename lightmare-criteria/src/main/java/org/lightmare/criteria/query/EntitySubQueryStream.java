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
package org.lightmare.criteria.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.query.jpa.AbstractQueryStream;
import org.lightmare.criteria.query.jpa.subqueries.AbstractSubQueryStream;

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
class EntitySubQueryStream<S extends Serializable, T extends Serializable> extends AbstractSubQueryStream<S, T> {

    protected EntitySubQueryStream(AbstractQueryStream<T> parent, Class<S> type) {
	super(parent, type);
    }

    // ========================= Entity and parent method composers =========//
    @Override
    public <F> SubQueryStream<S, T> equals(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException {
	oppSubQuery(sfield, field, Operators.EQ);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> notEquals(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException {
	oppSubQuery(sfield, field, Operators.NOT_EQ);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> more(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException {
	oppSubQuery(sfield, field, Operators.MORE);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> less(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException {
	oppSubQuery(sfield, field, Operators.LESS);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> moreOrEquals(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException {
	oppSubQuery(sfield, field, Operators.MORE_OR_EQ);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> lessOrEquals(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException {
	oppSubQuery(sfield, field, Operators.LESS_OR_EQ);
	return this;
    }

    @Override
    public SubQueryStream<S, T> startsWith(EntityField<S, String> sfield, EntityField<T, String> field)
	    throws IOException {
	oppSubQuery(sfield, field, Operators.LIKE);
	return this;
    }

    @Override
    public SubQueryStream<S, T> like(EntityField<S, String> sfield, EntityField<T, String> field) throws IOException {
	oppSubQuery(sfield, field, Operators.LIKE);
	return this;
    }

    @Override
    public SubQueryStream<S, T> endsWith(EntityField<S, String> sfield, EntityField<T, String> field)
	    throws IOException {
	oppSubQuery(sfield, field, Operators.LIKE);
	return this;
    }

    @Override
    public SubQueryStream<S, T> contains(EntityField<S, String> sfield, EntityField<T, String> field)
	    throws IOException {
	oppSubQuery(sfield, field, Operators.LIKE);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> in(EntityField<S, F> sfield, EntityField<T, Collection<F>> field)
	    throws IOException {
	oppSubQueryCollection(sfield, field);
	return this;
    }

    @Override
    public QueryStream<Object[]> select(EntityField<S, ?> field) throws IOException {
	return subSelectAll(field);
    }
}
