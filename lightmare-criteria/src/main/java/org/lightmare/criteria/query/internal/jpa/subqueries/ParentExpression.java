package org.lightmare.criteria.query.internal.jpa.subqueries;

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
import org.lightmare.criteria.functions.ParentField;
import org.lightmare.criteria.query.internal.jpa.links.Operators;

/**
 * Sub query stream for entity fields and parent entity fields
 * 
 * @author Levan Tsinadze
 *
 * @param <S>
 *            parent query entity type parameter
 * @param <T>
 *            entity type for generated query
 */
interface ParentExpression<S, T> {

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
    <F> SubQueryStream<S, T> operatePr(EntityField<S, F> sfield, ParentField<T, F> field, String operator);

    default <F> SubQueryStream<S, T> equalPr(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operatePr(sfield, field, Operators.EQ);
    }

    default <F> SubQueryStream<S, T> notEqualPr(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operatePr(sfield, field, Operators.NOT_EQ);
    }

    default <F> SubQueryStream<S, T> gtPr(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operatePr(sfield, field, Operators.GREATER);
    }

    default <F> SubQueryStream<S, T> greaterThenPr(EntityField<S, F> sfield, ParentField<T, F> field) {
        return gtPr(sfield, field);
    }

    default <F> SubQueryStream<S, T> ltPr(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operatePr(sfield, field, Operators.LESS);
    }

    default <F> SubQueryStream<S, T> lowerThenPr(EntityField<S, F> sfield, ParentField<T, F> field) {
        return ltPr(sfield, field);
    }

    default <F> SubQueryStream<S, T> gePr(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operatePr(sfield, field, Operators.GREATER_OR_EQ);
    }

    default <F> SubQueryStream<S, T> greaterThenOrEqualPr(EntityField<S, F> sfield, ParentField<T, F> field) {
        return gePr(sfield, field);
    }

    default <F> SubQueryStream<S, T> lePr(EntityField<S, F> sfield, ParentField<T, F> field) {
        return operatePr(sfield, field, Operators.LESS_OR_EQ);
    }

    default <F> SubQueryStream<S, T> lowerThenOrEqualPr(EntityField<S, F> sfield, ParentField<T, F> field) {
        return lePr(sfield, field);
    }

    default SubQueryStream<S, T> likePr(EntityField<S, String> sfield, ParentField<T, String> field) {
        return operatePr(sfield, field, Operators.LIKE);
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
    <F> SubQueryStream<S, T> operateCollectionPr(EntityField<S, F> sfield, ParentField<T, Collection<F>> field,
            String operator);

    default <F> SubQueryStream<S, T> inPr(EntityField<S, F> sfield, ParentField<T, Collection<F>> field) {
        return operateCollectionPr(sfield, field, Operators.IN);
    }

    default <F> SubQueryStream<S, T> notInPr(EntityField<S, F> sfield, ParentField<T, Collection<F>> field) {
        return operateCollectionPr(sfield, field, Operators.NOT_IN);
    }

    // =======================================================================//
}
