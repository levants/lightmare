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
package org.lightmare.criteria.query.internal.jpa.subqueries;

import java.util.Collection;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.ParentField;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.query.QueryStream;

/**
 * Implementation of {@link QueryStream} for sub queries and joins
 * 
 * @author Levan Tsinadze
 * @param <S>
 *            entity type for generated (sub) query
 * @param <T>
 *            entity type for generated query
 */
public interface SubQueryStream<S, T>
        extends GeneralSubQueryStream<T, S>, SubFieldStream<T, S>, SubFieldValueStream<T, S> {

    // =========================embedded=field=queries=======================//

    @Override
    <F> SubQueryStream<S, T> embedded(EntityField<S, F> field, SubQueryConsumer<F, S> consumer);

    // ========================= Entity and parent method composers =========//

    /**
     * Generates query part for instant field and parent entity query field with
     * and operator
     * 
     * @param sfield
     * @param field
     * @param operator
     * @return {@link SubQueryStream} current instance
     */
    <F> SubQueryStream<S, T> operate(EntityField<S, F> sfield, ParentField<T, F> field, String operator);

    default <F> SubQueryStream<S, T> equals(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operate(sfield, field, Operators.EQ);
    }

    default <F> SubQueryStream<S, T> notEquals(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operate(sfield, field, Operators.NOT_EQ);
    }

    default <F> SubQueryStream<S, T> more(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operate(sfield, field, Operators.MORE);
    }

    default <F> SubQueryStream<S, T> less(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operate(sfield, field, Operators.LESS);
    }

    default <F> SubQueryStream<S, T> moreOrEquals(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operate(sfield, field, Operators.MORE_OR_EQ);
    }

    default <F> SubQueryStream<S, T> lessOrEquals(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operate(sfield, field, Operators.LESS_OR_EQ);
    }

    default SubQueryStream<S, T> startsWith(EntityField<S, String> sfield, ParentField<T, String> field) {
        return operate(sfield, field, Operators.LIKE);
    }

    default SubQueryStream<S, T> like(EntityField<S, String> sfield, ParentField<T, String> field) {
        return operate(sfield, field, Operators.LIKE);
    }

    default SubQueryStream<S, T> endsWith(EntityField<S, String> sfield, ParentField<T, String> field) {
        return operate(sfield, field, Operators.LIKE);
    }

    default SubQueryStream<S, T> contains(EntityField<S, String> sfield, ParentField<T, String> field) {
        return operate(sfield, field, Operators.LIKE);
    }

    /**
     * Generates query part for instant field and parent entity query field with
     * {@link Collection} type
     * 
     * @param sfield
     * @param field
     * @param operator
     * @return {@link SubQueryStream} current instance
     */
    <F> SubQueryStream<S, T> operateCollection(EntityField<S, F> sfield, ParentField<T, Collection<F>> field,
            String operator);

    default <F> SubQueryStream<S, T> in(EntityField<S, F> sfield, ParentField<T, Collection<F>> field) {
        return operateCollection(sfield, field, Operators.IN);
    }

    default <F> SubQueryStream<S, T> notIn(EntityField<S, F> sfield, ParentField<T, Collection<F>> field) {
        return operateCollection(sfield, field, Operators.NOT_IN);
    }
}
