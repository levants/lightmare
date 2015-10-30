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

import javax.persistence.EntityManager;

import org.lightmare.criteria.lambda.FieldGetter;
import org.lightmare.criteria.links.Filters;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.links.Orders;

/**
 * Query builder from getter method references
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
abstract class GetterQueryStream<T extends Serializable> extends AbstractQueryStream<T> {

    protected GetterQueryStream(final EntityManager em, final Class<T> entityType, final String alias) {
	super(em, entityType, alias);
    }

    @Override
    public <F> QueryStream<T> eq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.EQ);
	return this;
    }

    @Override
    public <F> QueryStream<T> notEq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.NOT_EQ);
	return this;
    }

    @Override
    public <F> QueryStream<T> more(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE);
	return this;
    }

    @Override
    public <F> QueryStream<T> less(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.LESS);
	return this;
    }

    @Override
    public <F> QueryStream<T> moreOrEq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.MORE_OR_EQ);
	return this;
    }

    @Override
    public <F> QueryStream<T> lessOrEq(FieldGetter<F> field, F value) throws IOException {
	oppLine(field, value, Operators.LESS_OR_EQ);
	return this;
    }

    @Override
    public QueryStream<T> startsWith(FieldGetter<String> field, String value) throws IOException {
	String enrich = value.concat(Filters.LIKE_SIGN);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    @Override
    public QueryStream<T> like(FieldGetter<String> field, String value) throws IOException {
	return startsWith(field, value);
    }

    @Override
    public QueryStream<T> endsWith(FieldGetter<String> field, String value) throws IOException {
	String enrich = Filters.LIKE_SIGN.concat(value);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    @Override
    public QueryStream<T> contains(FieldGetter<String> field, String value) throws IOException {
	String enrich = Filters.LIKE_SIGN.concat(value).concat(Filters.LIKE_SIGN);
	oppLine(field, enrich, Operators.LIKE);
	return this;
    }

    @Override
    public <F> QueryStream<T> isNull(FieldGetter<F> field) throws IOException {
	oppLine(field, Operators.IS_NULL);
	return this;
    }

    @Override
    public <F> QueryStream<T> notNull(FieldGetter<F> field) throws IOException {
	oppLine(field, Operators.NOT_NULL);
	return this;
    }

    @Override
    public <F> QueryStream<T> set(FieldGetter<F> field, F value) throws IOException {
	setOpp(field, value);
	return this;
    }

    @Override
    public QueryStream<T> orderBy(FieldGetter<?>... getters) throws IOException {
	setOrder(getters);
	return this;
    }

    @Override
    public QueryStream<T> orderByDesc(FieldGetter<?>... getters) throws IOException {
	setOrder(Orders.DESC, getters);
	return this;
    }
}
