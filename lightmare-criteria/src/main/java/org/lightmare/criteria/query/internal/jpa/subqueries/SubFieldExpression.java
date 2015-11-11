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
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.query.internal.jpa.FieldExpression;

/**
 * Query stream for entity fields and appropriated values
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            parent query entity type parameter
 * @param <S>
 *            entity type parameter
 */
interface SubFieldExpression<S, T> extends FieldExpression<S> {

    // ========================= Entity self method composers ===============//

    @Override
    <F> SubQueryStream<S, T> operateFl(EntityField<S, F> field1, EntityField<S, F> field2, String operator);

    @Override
    default <F> SubQueryStream<S, T> equalFl(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operateFl(field1, field2, Operators.EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> notEqualFl(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operateFl(field1, field2, Operators.NOT_EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> gtFl(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operateFl(field1, field2, Operators.MORE);
    }

    @Override
    default <F> SubQueryStream<S, T> ltFl(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operateFl(field1, field2, Operators.LESS);
    }

    @Override
    default <F> SubQueryStream<S, T> geFl(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operateFl(field1, field2, Operators.MORE_OR_EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> leFl(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operateFl(field1, field2, Operators.LESS_OR_EQ);
    }

    @Override
    <F> SubQueryStream<S, T> betweenFl(EntityField<S, F> field1, EntityField<S, F> field2, EntityField<S, F> field3);

    @Override
    <F> SubQueryStream<S, T> notBetweenFl(EntityField<S, F> field1, EntityField<S, F> field2, EntityField<S, F> field3);

    @Override
    default SubQueryStream<S, T> likeFl(EntityField<S, String> field1, EntityField<S, String> field2) {
        return operateFl(field1, field2, Operators.LIKE);
    }

    @Override
    <F> SubQueryStream<S, T> operateCollectionFl(EntityField<S, F> field1, EntityField<S, Collection<F>> field2,
            String operator);

    @Override
    default <F> SubQueryStream<S, T> inFl(EntityField<S, F> field1, EntityField<S, Collection<F>> field2) {
        return operateCollectionFl(field1, field2, Operators.IN);
    }

    @Override
    default <F> SubQueryStream<S, T> notInFl(EntityField<S, F> field1, EntityField<S, Collection<F>> field2) {
        return operateCollectionFl(field1, field2, Operators.NOT);
    }
}
