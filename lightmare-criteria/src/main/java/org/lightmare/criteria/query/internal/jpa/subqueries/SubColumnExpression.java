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
import org.lightmare.criteria.query.internal.jpa.ColumnExpression;

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
interface SubColumnExpression<S, T> extends ColumnExpression<S> {

    // ========================= Entity self method composers ===============//

    @Override
    <F> SubQueryStream<S, T> operateCl(EntityField<S, F> field1, EntityField<S, F> field2, String operator);

    @Override
    default <F> SubQueryStream<S, T> equalCl(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operateCl(field1, field2, Operators.EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> notEqualCl(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operateCl(field1, field2, Operators.NOT_EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> gtCl(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operateCl(field1, field2, Operators.GREATER);
    }

    @Override
    default <F> SubQueryStream<S, T> ltCl(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operateCl(field1, field2, Operators.LESS);
    }

    @Override
    default <F> SubQueryStream<S, T> geCl(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operateCl(field1, field2, Operators.GREATER_OR_EQ);
    }

    @Override
    default <F> SubQueryStream<S, T> leCl(EntityField<S, F> field1, EntityField<S, F> field2) {
        return operateCl(field1, field2, Operators.LESS_OR_EQ);
    }

    @Override
    <F> SubQueryStream<S, T> betweenCl(EntityField<S, F> field1, EntityField<S, F> field2, EntityField<S, F> field3);

    @Override
    <F> SubQueryStream<S, T> notBetweenCl(EntityField<S, F> field1, EntityField<S, F> field2, EntityField<S, F> field3);

    @Override
    default SubQueryStream<S, T> likeCl(EntityField<S, String> field1, EntityField<S, String> field2) {
        return operateCl(field1, field2, Operators.LIKE);
    }

    @Override
    <F> SubQueryStream<S, T> operateCollectionCl(EntityField<S, F> field1, EntityField<S, Collection<F>> field2,
            String operator);

    @Override
    default <F> SubQueryStream<S, T> inCl(EntityField<S, F> field1, EntityField<S, Collection<F>> field2) {
        return operateCollectionCl(field1, field2, Operators.IN);
    }

    @Override
    default <F> SubQueryStream<S, T> notInCl(EntityField<S, F> field1, EntityField<S, Collection<F>> field2) {
        return operateCollectionCl(field1, field2, Operators.NOT);
    }
}
