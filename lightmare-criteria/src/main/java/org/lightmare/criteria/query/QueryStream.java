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

import java.util.Collection;
import java.util.function.Consumer;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.QueryConsumer;
import org.lightmare.criteria.query.orm.links.Operators;
import org.lightmare.criteria.utils.ObjectUtils;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Query stream of {@link String} based queries for abstract data base layers
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 */
public interface QueryStream<T, Q extends QueryStream<T, ? super Q>> extends TextQuery<T, Q> {

    @Override
    default <F> Q equal(EntityField<T, F> field, Object value) {
        return operate(field, value, Operators.EQ);
    }

    @Override
    default <F> Q notEqual(EntityField<T, F> field, Object value) {
        return operate(field, value, Operators.NOT_EQ);
    }

    @Override
    default <F extends Comparable<? super F>> Q gt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, Operators.GREATER);
    }

    @Override
    default <F extends Comparable<? super F>> Q lt(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, Operators.LESS);
    }

    @Override
    default <F extends Comparable<? super F>> Q ge(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, Operators.GREATER_OR_EQ);
    }

    @Override
    default <F extends Comparable<? super F>> Q le(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return operate(field, value, Operators.LESS_OR_EQ);
    }

    // =============================LIKE=clause==============================//

    @Override
    default Q like(EntityField<T, String> field, String value) {
        return operate(field, value, Operators.LIKE);
    }

    @Override
    default Q notLike(EntityField<T, String> field, String value) {
        return operate(field, value, Operators.NOT_LIKE);
    }

    // ======================================================================//

    /**
     * Generates query part for instant field with {@link java.util.Collection}
     * parameter and operator
     * 
     * @param field
     * @param values
     * @param operator
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    <F> Q operateCollection(EntityField<T, F> field, Collection<F> values, String operator);

    @Override
    default <F> Q in(EntityField<T, F> field, Collection<F> values) {
        return operateCollection(field, values, Operators.IN);
    }

    @Override
    default <F> Q notIn(EntityField<T, F> field, Collection<F> values) {
        return operateCollection(field, values, Operators.NOT_IN);
    }

    // =============================NULL=check===============================//

    @Override
    default <F> Q isNull(EntityField<T, F> field) {
        return operate(field, Operators.IS_NULL);
    }

    @Override
    default <F> Q isNotNull(EntityField<T, F> field) {
        return operate(field, Operators.NOT_NULL);
    }

    // ======================================================================//

    default Q appendOperator(Object operator) {
        return appendBody(operator);
    }

    @Override
    default Q and() {
        return appendOperator(Operators.AND);
    }

    @Override
    default Q or() {
        return appendOperator(Operators.OR);
    }

    /**
     * Appends query body with operator and value
     * 
     * @param value
     * @param operator
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default Q appendOperator(Object value, Object operator) {

        Q stream = appendBody(value);
        appendBody(StringUtils.SPACE).appendBody(operator);

        return stream;
    }

    // ======================WHERE=AND=OR=clauses=with=stream================//

    /**
     * Generates AND / OR connector and sets brackets
     * 
     * @param connector
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default Q brackets(Consumer<QueryStream<T, Q>> connector, QueryConsumer<T, Q> consumer) {

        Q stream;

        ObjectUtils.accept(connector, this);
        stream = brackets(consumer);

        return stream;
    }

    /**
     * AND clause in lambda expression manner
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    @Override
    default Q and(QueryConsumer<T, Q> consumer) {
        return brackets(QueryStream::and, consumer);
    }

    /**
     * OR clause in lambda expression manner
     * 
     * @param consumer
     * @return {@link org.lightmare.criteria.query.QueryStream} implementation
     */
    default Q or(QueryConsumer<T, Q> consumer) {
        return brackets(QueryStream::or, consumer);
    }
}
