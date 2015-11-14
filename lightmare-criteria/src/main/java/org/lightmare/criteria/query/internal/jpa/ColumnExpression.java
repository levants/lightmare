package org.lightmare.criteria.query.internal.jpa;

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
import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Operators;

/**
 * Query stream for entity fields and appropriated values
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface ColumnExpression<T> {

    // ========================= Entity self method composers ===============//

    /**
     * Generates query part for instant fields with and operator
     * 
     * @param field1
     * @param field2
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> operate(EntityField<T, ? extends F> field1, EntityField<S, ? extends F> field2,
            String operator);

    default <F, S> QueryStream<T> equal(EntityField<T, F> field1, EntityField<S, F> field2) {
        return operate(field1, field2, Operators.EQ);
    }

    default <F, S> QueryStream<T> notEqual(EntityField<T, F> field1, EntityField<S, F> field2) {
        return operate(field1, field2, Operators.NOT_EQ);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> gtCl(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return operate(field1, field2, Operators.GREATER);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> greaterThenCl(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return gtCl(field1, field2);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> ltCl(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return operate(field1, field2, Operators.LESS);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lowerThenCl(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return ltCl(field1, field2);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> geCl(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return operate(field1, field2, Operators.GREATER_OR_EQ);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> greaterThenOrEqualCl(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return geCl(field1, field2);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> leCl(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return operate(field1, field2, Operators.LESS_OR_EQ);
    }

    default <F extends Comparable<? super F>, S> QueryStream<T> lowerThenOrEqualCl(EntityField<T, ? extends F> field1,
            EntityField<S, ? extends F> field2) {
        return leCl(field1, field2);
    }

    <F> QueryStream<T> betweenCl(EntityField<T, F> field1, EntityField<T, F> field2, EntityField<T, F> field3);

    <F> QueryStream<T> notBetweenCl(EntityField<T, F> field1, EntityField<T, F> field2, EntityField<T, F> field3);

    default QueryStream<T> like(EntityField<T, String> field1, EntityField<T, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    /**
     * Generates query part for instant fields with {@link Collection} types
     * 
     * @param field1
     * @param field2
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <F, S> QueryStream<T> operateCollection(EntityField<T, F> field1, EntityField<S, Collection<F>> field2,
            String operator);

    default <F, S> QueryStream<T> in(EntityField<T, F> field1, EntityField<S, Collection<F>> field2) {
        return operateCollection(field1, field2, Operators.IN);
    }

    default <F, S> QueryStream<T> notIn(EntityField<T, F> field1, EntityField<S, Collection<F>> field2) {
        return operateCollection(field1, field2, Operators.NOT_IN);
    }
}
