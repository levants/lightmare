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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.ParentField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.query.jpa.subqueries.AbstractSubQueryStream;

/**
 * Implementation of {@link QueryStream} for sub queries and joins
 * 
 * @author Levan Tsinadze
 * @param <S>
 *            entity type for generated (sub) query
 * @param <T>
 *            entity type for generated query
 */
public interface SubQueryStream<S extends Serializable, T extends Serializable> extends QueryStream<S> {

    /**
     * Gets entity alias for custom sub queries
     * 
     * @return {@link String} entity alias
     */
    String getAlias();

    // ========================= Entity method composers ====================//

    @Override
    <F> SubQueryStream<S, T> equals(EntityField<S, F> field, F value);

    @Override
    <F> SubQueryStream<S, T> notEquals(EntityField<S, F> field, F value);

    @Override
    <F> SubQueryStream<S, T> more(EntityField<S, F> field, F value);

    @Override
    <F> SubQueryStream<S, T> less(EntityField<S, F> field, F value);

    @Override
    <F> SubQueryStream<S, T> moreOrEquals(EntityField<S, F> field, F value);

    @Override
    <F> SubQueryStream<S, T> lessOrEquals(EntityField<S, F> field, F value);

    @Override
    SubQueryStream<S, T> startsWith(EntityField<S, String> field, String value);

    @Override
    SubQueryStream<S, T> like(EntityField<S, String> field, String value);

    @Override
    SubQueryStream<S, T> endsWith(EntityField<S, String> field, String value);

    @Override
    SubQueryStream<S, T> contains(EntityField<S, String> field, String value);

    @Override
    <F> SubQueryStream<S, T> in(EntityField<S, F> field, Collection<F> values);

    @Override
    <F> SubQueryStream<S, T> notIn(EntityField<S, F> field, Collection<F> values);

    @Override
    default <F> SubQueryStream<S, T> in(EntityField<S, F> field, F[] values) {
	return this.in(field, Arrays.asList(values));
    }

    @Override
    default <F> SubQueryStream<S, T> notIn(EntityField<S, F> field, F[] values) {
	return this.notIn(field, Arrays.asList(values));
    }

    @Override
    SubQueryStream<S, T> isNull(EntityField<S, ?> field);

    @Override
    SubQueryStream<S, T> notNull(EntityField<S, ?> field);

    // ========================= Entity self method composers ===============//

    @Override
    <F> SubQueryStream<S, T> operate(EntityField<S, F> field1, EntityField<S, F> field2, String operator);

    @Override
    <F> SubQueryStream<S, T> equals(EntityField<S, F> field1, EntityField<S, F> field2);

    @Override
    <F> SubQueryStream<S, T> notEquals(EntityField<S, F> field1, EntityField<S, F> field2);

    @Override
    <F> SubQueryStream<S, T> more(EntityField<S, F> field1, EntityField<S, F> field2);

    @Override
    <F> SubQueryStream<S, T> less(EntityField<S, F> field1, EntityField<S, F> field2);

    @Override
    <F> SubQueryStream<S, T> moreOrEquals(EntityField<S, F> field1, EntityField<S, F> field2);

    @Override
    <F> SubQueryStream<S, T> lessOrEquals(EntityField<S, F> field1, EntityField<S, F> field2);

    @Override
    SubQueryStream<S, T> startsWith(EntityField<S, String> field1, EntityField<S, String> field2);

    @Override
    SubQueryStream<S, T> like(EntityField<S, String> field1, EntityField<S, String> field2);

    @Override
    SubQueryStream<S, T> endsWith(EntityField<S, String> field1, EntityField<S, String> field2);

    @Override
    SubQueryStream<S, T> contains(EntityField<S, String> field1, EntityField<S, String> field2);

    @Override
    <F> SubQueryStream<S, T> in(EntityField<S, F> field1, EntityField<S, Collection<F>> field2);

    @Override
    <F> SubQueryStream<S, T> notIn(EntityField<S, F> field1, EntityField<S, Collection<F>> field2);

    // ========================= Entity and parent method composers =========//

    <F> SubQueryStream<S, T> operate(EntityField<S, F> sfield, ParentField<T, F> field, String operator);

    <F> SubQueryStream<S, T> equals(EntityField<S, F> sfield, ParentField<T, F> field);

    <F> SubQueryStream<S, T> notEquals(EntityField<S, F> sfield, ParentField<T, F> field);

    <F> SubQueryStream<S, T> more(EntityField<S, F> sfield, ParentField<T, F> field);

    <F> SubQueryStream<S, T> less(EntityField<S, F> sfield, ParentField<T, F> field);

    <F> SubQueryStream<S, T> moreOrEquals(EntityField<S, F> sfield, ParentField<T, F> field);

    <F> SubQueryStream<S, T> lessOrEquals(EntityField<S, F> sfield, ParentField<T, F> field);

    SubQueryStream<S, T> startsWith(EntityField<S, String> sfield, ParentField<T, String> field);

    SubQueryStream<S, T> like(EntityField<S, String> sfield, ParentField<T, String> field);

    SubQueryStream<S, T> endsWith(EntityField<S, String> sfield, ParentField<T, String> field);

    SubQueryStream<S, T> contains(EntityField<S, String> sfield, ParentField<T, String> field);

    <F> SubQueryStream<S, T> in(EntityField<S, F> sfield, ParentField<T, Collection<F>> field);

    <F> SubQueryStream<S, T> notIn(EntityField<S, F> sfield, ParentField<T, Collection<F>> field);

    // =========================sub=queries==================================//

    /**
     * Generates {@link SubQueryStream} for S type
     * 
     * @param subType
     * @param consumer
     * @return {@link SubQueryStream} similar stream for sub query
     */
    @Override
    <K extends Serializable> SubQueryStream<S, T> subQuery(Class<K> subType, SubQueryConsumer<K, S> consumer);

    /**
     * Generates {@link SubQueryStream} for S type without conditions
     * 
     * @param consumer
     * @return {@link SubQueryStream} similar stream for sub query
     */
    @Override
    default SubQueryStream<S, T> subQuery(SubQueryConsumer<S, S> consumer) {
	return this.subQuery(getEntityType(), consumer);
    }

    @Override
    <F, K extends Serializable> SubQueryStream<S, T> in(EntityField<S, F> field, Class<K> subType,
	    SubQueryConsumer<K, S> consumer);

    @Override
    <F, K extends Serializable> SubQueryStream<S, T> notIn(EntityField<S, F> field, Class<K> subType,
	    SubQueryConsumer<K, S> consumer);

    @Override
    default <F, K extends Serializable> SubQueryStream<S, T> in(EntityField<S, F> field, Class<K> subType) {
	return this.in(field, subType, null);
    }

    @Override
    default <F, K extends Serializable> SubQueryStream<S, T> notIn(EntityField<S, F> field, Class<K> subType) {
	return this.notIn(field, subType, null);
    }

    @Override
    default <F> SubQueryStream<S, T> in(EntityField<S, F> field, SubQueryConsumer<S, S> consumer) {
	return this.in(field, getEntityType(), consumer);
    }

    @Override
    default <F> SubQueryStream<S, T> notIn(EntityField<S, F> field, SubQueryConsumer<S, S> consumer) {
	return this.notIn(field, getEntityType(), consumer);
    }

    @Override
    default <F> SubQueryStream<S, T> in(EntityField<S, F> field) {
	SubQueryConsumer<S, S> consumer = null;
	return this.in(field, consumer);
    }

    @Override
    default <F> SubQueryStream<S, T> notIn(EntityField<S, F> field) {
	SubQueryConsumer<S, S> consumer = null;
	return this.notIn(field, consumer);
    }

    @Override
    <F, K extends Serializable> SubQueryStream<S, T> exists(Class<K> subType, SubQueryConsumer<K, S> consumer);

    @Override
    <F, K extends Serializable> SubQueryStream<S, T> notExists(Class<K> subType, SubQueryConsumer<K, S> consumer);

    @Override
    default <F, K extends Serializable> SubQueryStream<S, T> exists(Class<K> subType) {
	return this.exists(subType, null);
    }

    @Override
    default <F, K extends Serializable> SubQueryStream<S, T> notExists(Class<K> subType) {
	return this.notExists(subType, null);
    }

    @Override
    default <F> SubQueryStream<S, T> exists(SubQueryConsumer<S, S> consumer) {
	return this.exists(getEntityType(), consumer);
    }

    @Override
    default <F> SubQueryStream<S, T> notExists(SubQueryConsumer<S, S> consumer) {
	return this.notExists(getEntityType(), consumer);
    }

    // =========================sub=queries==================================//

    // =========================order=by=====================================//

    @Override
    SubQueryStream<S, T> orderBy(EntityField<S, ?> field);

    @Override
    SubQueryStream<S, T> orderByDesc(EntityField<S, ?> field);

    // ======================================================================//

    /**
     * WHERE clause appender
     * 
     * @return {@link SubQueryStream} current instance
     */
    @Override
    SubQueryStream<S, T> where();

    /**
     * AND part appender
     * 
     * @return {@link SubQueryStream} current instance
     */
    @Override
    SubQueryStream<S, T> and();

    /**
     * OR part appender
     * 
     * @return {@link SubQueryStream} current instance
     */
    @Override
    SubQueryStream<S, T> or();

    /**
     * Opens bracket in query body
     * 
     * @return {@link SubQueryStream} current instance
     */
    @Override
    SubQueryStream<S, T> openBracket();

    /**
     * Closes bracket in query body
     * 
     * @return {@link SubQueryStream} current instance
     */
    @Override
    SubQueryStream<S, T> closeBracket();

    /**
     * Creates query part in brackets
     * 
     * @param field
     * @return {@link SubQueryStream} current instance
     */
    @Override
    SubQueryStream<S, T> brackets(QueryConsumer<S> field);

    // ======================================================================//

    /**
     * Appends to generated query prefix custom clause
     * 
     * @param clause
     * @return {@link SubQueryStream} current instance
     */
    @Override
    SubQueryStream<S, T> appendPrefix(Object clause);

    /**
     * Appends to generated query body custom clause
     * 
     * @param clause
     * @return {@link SubQueryStream} current instance
     */
    @Override
    SubQueryStream<S, T> appendBody(Object clause);

    /**
     * Executes {@link AbstractSubQueryStream#get()} method if it is in prepared
     * state
     */
    void call();
}
