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
package org.lightmare.criteria.query.jpa.subqueries;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.query.EntityQueryStream;
import org.lightmare.criteria.query.SubQueryStream;

/**
 * Main class to operate on sub queries and generate query clauses
 * 
 * @author Levan Tsinadze
 *
 * @param <S>
 *            entity type for generated (sub) query
 * @param <T>
 *            entity type for generated query
 */
abstract class DirectctSubQueryStream<S extends Serializable, T extends Serializable> extends EntityQueryStream<S>
	implements SubQueryStream<S, T> {

    protected DirectctSubQueryStream(EntityManager em, Class<S> entityType, String alias) {
	super(em, entityType, alias);
    }

    // ================= entity QL methods ===================================//
    @Override
    public <F> SubQueryStream<S, T> operate(EntityField<S, F> field, String operator) {
	super.operate(field, operator);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> operate(EntityField<S, F> field, F value, String operator) {
	super.operate(field, value, operator);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> equals(EntityField<S, F> field, F value) {
	super.equals(field, value);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> notEquals(EntityField<S, F> field, F value) {
	super.notEquals(field, value);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> more(EntityField<S, F> field, F value) {
	super.more(field, value);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> less(EntityField<S, F> field, F value) {
	super.less(field, value);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> moreOrEquals(EntityField<S, F> field, F value) {
	super.moreOrEquals(field, value);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> lessOrEquals(EntityField<S, F> field, F value) {
	super.lessOrEquals(field, value);
	return this;
    }

    @Override
    public SubQueryStream<S, T> startsWith(EntityField<S, String> field, String value) {
	super.startsWith(field, value);
	return this;
    }

    @Override
    public SubQueryStream<S, T> like(EntityField<S, String> field, String value) {
	super.like(field, value);
	return this;
    }

    @Override
    public SubQueryStream<S, T> endsWith(EntityField<S, String> field, String value) {
	super.endsWith(field, value);
	return this;
    }

    @Override
    public SubQueryStream<S, T> contains(EntityField<S, String> field, String value) {
	super.startsWith(field, value);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> in(EntityField<S, F> field, Collection<F> values) {
	super.in(field, values);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> notIn(EntityField<S, F> field, Collection<F> values) {
	super.notIn(field, values);
	return this;
    }

    @Override
    public SubQueryStream<S, T> isNull(EntityField<S, ?> field) {
	super.isNull(field);
	return this;
    }

    @Override
    public SubQueryStream<S, T> notNull(EntityField<S, ?> field) {
	super.notNull(field);
	return this;
    }

    // ========================= Entity self method composers ===============//

    @Override
    public <F> SubQueryStream<S, T> operate(EntityField<S, F> field1, EntityField<S, F> field2, String operator) {
	super.oppField(field1, field2, operator);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> equals(EntityField<S, F> field1, EntityField<S, F> field2) {
	super.oppField(field1, field2, Operators.EQ);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> notEquals(EntityField<S, F> field1, EntityField<S, F> field2) {
	super.oppField(field1, field2, Operators.NOT_EQ);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> more(EntityField<S, F> field1, EntityField<S, F> field2) {
	super.oppField(field1, field2, Operators.MORE);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> less(EntityField<S, F> field1, EntityField<S, F> field2) {
	super.oppField(field1, field2, Operators.LESS);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> moreOrEquals(EntityField<S, F> field1, EntityField<S, F> field2) {
	super.oppField(field1, field2, Operators.MORE_OR_EQ);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> lessOrEquals(EntityField<S, F> field1, EntityField<S, F> field2) {
	super.oppField(field1, field2, Operators.LESS_OR_EQ);
	return this;
    }

    @Override
    public SubQueryStream<S, T> startsWith(EntityField<S, String> field1, EntityField<S, String> field2) {
	super.oppField(field1, field2, Operators.LIKE);
	return this;
    }

    @Override
    public SubQueryStream<S, T> like(EntityField<S, String> field1, EntityField<S, String> field2) {
	super.oppField(field1, field2, Operators.LIKE);
	return this;
    }

    @Override
    public SubQueryStream<S, T> endsWith(EntityField<S, String> field1, EntityField<S, String> field2) {
	super.oppField(field1, field2, Operators.LIKE);
	return this;
    }

    @Override
    public SubQueryStream<S, T> contains(EntityField<S, String> field1, EntityField<S, String> field2) {
	super.oppField(field1, field2, Operators.LIKE);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> in(EntityField<S, F> field1, EntityField<S, Collection<F>> field2) {
	super.oppCollectionField(field1, field2, Operators.IN);
	return this;
    }

    @Override
    public <F> SubQueryStream<S, T> notIn(EntityField<S, F> field1, EntityField<S, Collection<F>> field2) {
	super.oppCollectionField(field1, field2, Operators.NOT_IN);
	return this;
    }

    // =========================sub=queries==================================//

    @Override
    public <K extends Serializable> SubQueryStream<S, T> subQuery(Class<K> subType, SubQueryConsumer<K, S> consumer) {
	super.subQuery(subType, consumer);
	return this;
    }

    @Override
    public <F, K extends Serializable> SubQueryStream<S, T> in(EntityField<S, F> field, Class<K> subType,
	    SubQueryConsumer<K, S> consumer) {
	super.in(field, subType, consumer);
	return this;
    }

    @Override
    public <F, K extends Serializable> SubQueryStream<S, T> notIn(EntityField<S, F> field, Class<K> subType,
	    SubQueryConsumer<K, S> consumer) {
	super.notIn(field, subType, consumer);
	return this;
    }

    @Override
    public <F, K extends Serializable> SubQueryStream<S, T> exists(Class<K> subType, SubQueryConsumer<K, S> consumer) {
	super.exists(subType, consumer);
	return this;
    }

    @Override
    public <F, K extends Serializable> SubQueryStream<S, T> notExists(Class<K> subType,
	    SubQueryConsumer<K, S> consumer) {
	super.notExists(subType, consumer);
	return this;
    }

    // =========================sub=queries==================================//

    // =========================order=by=====================================//
    @Override
    public SubQueryStream<S, T> orderBy(EntityField<S, ?> field) {
	super.orderBy(field);
	return this;
    }

    @Override
    public SubQueryStream<S, T> orderByDesc(EntityField<S, ?> field) {
	super.orderByDesc(field);
	return this;
    }
    // ======================================================================//

    // ================= entity QL Filters ===================================//
    @Override
    public SubQueryStream<S, T> where() {
	super.where();
	return this;
    }

    @Override
    public SubQueryStream<S, T> and() {
	super.and();
	return this;
    }

    @Override
    public SubQueryStream<S, T> or() {
	super.or();
	return this;
    }

    @Override
    public SubQueryStream<S, T> openBracket() {
	super.openBracket();
	return this;
    }

    @Override
    public SubQueryStream<S, T> closeBracket() {
	super.closeBracket();
	return this;
    }

    @Override
    public SubQueryStream<S, T> brackets(QueryConsumer<S> consumer) {
	super.brackets(consumer);
	return this;
    }

    // ================= entity QL Filters ==================================//

    // ================= methods for custom QL ==============================//

    @Override
    public SubQueryStream<S, T> appendPrefix(Object clause) {
	super.appendPrefix(clause);
	return this;
    }

    @Override
    public SubQueryStream<S, T> appendBody(Object clause) {
	super.appendBody(clause);
	return this;
    }
}
