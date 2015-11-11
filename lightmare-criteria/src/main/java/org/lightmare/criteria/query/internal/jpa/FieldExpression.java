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
import org.lightmare.criteria.links.Operators;
import org.lightmare.criteria.query.QueryStream;

/**
 * Query stream for entity fields and appropriated values
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface FieldExpression<T> {

    // ========================= Entity self method composers ===============//

    /**
     * Generates query part for instant fields with and operator
     * 
     * @param field1
     * @param field2
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> operateFl(EntityField<T, F> field1, EntityField<T, F> field2, String operator);

    default <F> QueryStream<T> equalFl(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operateFl(field1, field2, Operators.EQ);
    }

    default <F> QueryStream<T> notEqualFl(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operateFl(field1, field2, Operators.NOT_EQ);
    }

    default <F> QueryStream<T> gtFl(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operateFl(field1, field2, Operators.MORE);
    }

    default <F> QueryStream<T> ltFl(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operateFl(field1, field2, Operators.LESS);
    }

    default <F> QueryStream<T> geFl(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operateFl(field1, field2, Operators.GREATER_OR_EQ);
    }

    default <F> QueryStream<T> leFl(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operateFl(field1, field2, Operators.LESS_OR_EQ);
    }

    <F> QueryStream<T> betweenFl(EntityField<T, F> field1, EntityField<T, F> field2, EntityField<T, F> field3);

    <F> QueryStream<T> notBetweenFl(EntityField<T, F> field1, EntityField<T, F> field2, EntityField<T, F> field3);

    default QueryStream<T> likeFl(EntityField<T, String> field1, EntityField<T, String> field2) {
        return operateFl(field1, field2, Operators.LIKE);
    }

    /**
     * Generates query part for instant fields with {@link Collection} types
     * 
     * @param field1
     * @param field2
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> operateCollectionFl(EntityField<T, F> field1, EntityField<T, Collection<F>> field2,
            String operator);

    default <F> QueryStream<T> inFl(EntityField<T, F> field1, EntityField<T, Collection<F>> field2) {
        return operateCollectionFl(field1, field2, Operators.IN);
    }

    default <F> QueryStream<T> notInFl(EntityField<T, F> field1, EntityField<T, Collection<F>> field2) {
        return operateCollectionFl(field1, field2, Operators.NOT_IN);
    }
}
