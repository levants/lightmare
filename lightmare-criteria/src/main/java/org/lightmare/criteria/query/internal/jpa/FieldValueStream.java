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
public interface FieldValueStream<T> {

    // ========================= Entity self method composers ===============//

    /**
     * Generates query part for instant fields with and operator
     * 
     * @param field1
     * @param field2
     * @param operator
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> operate(EntityField<T, F> field1, EntityField<T, F> field2, String operator);

    default <F> QueryStream<T> equals(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operate(field1, field2, Operators.EQ);
    }

    default <F> QueryStream<T> notEquals(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operate(field1, field2, Operators.NOT_EQ);
    }

    default <F> QueryStream<T> greater(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operate(field1, field2, Operators.MORE);
    }

    default <F> QueryStream<T> less(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operate(field1, field2, Operators.LESS);
    }

    default <F> QueryStream<T> greaterOrEquals(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operate(field1, field2, Operators.MORE_OR_EQ);
    }

    default <F> QueryStream<T> lessOrEquals(EntityField<T, F> field1, EntityField<T, F> field2) {
        return operate(field1, field2, Operators.LESS_OR_EQ);
    }

    default QueryStream<T> startsWith(EntityField<T, String> field1, EntityField<T, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    default QueryStream<T> like(EntityField<T, String> field1, EntityField<T, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    default QueryStream<T> endsWith(EntityField<T, String> field1, EntityField<T, String> field2) {
        return operate(field1, field2, Operators.LIKE);
    }

    default QueryStream<T> contains(EntityField<T, String> field1, EntityField<T, String> field2) {
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
    <F> QueryStream<T> operateCollection(EntityField<T, F> field1, EntityField<T, Collection<F>> field2,
            String operator);

    default <F> QueryStream<T> in(EntityField<T, F> field1, EntityField<T, Collection<F>> field2) {
        return operateCollection(field1, field2, Operators.IN);
    }

    default <F> QueryStream<T> notIn(EntityField<T, F> field1, EntityField<T, Collection<F>> field2) {
        return operateCollection(field1, field2, Operators.NOT_IN);
    }
}
