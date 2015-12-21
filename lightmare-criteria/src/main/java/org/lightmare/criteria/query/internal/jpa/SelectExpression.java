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
package org.lightmare.criteria.query.internal.jpa;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.QueryStream;

/**
 * Interface to generate SELECT for instant fields
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public interface SelectExpression<T> {

    /**
     * Select generator
     * 
     * @author Levan Tsinadze
     *
     */
    public static final class Select {

        private List<Serializable> fields;

        private Select() {
            fields = new LinkedList<Serializable>();
        }

        public static Select select() {
            return new Select();
        }

        public List<Serializable> getFields() {
            return fields;
        }

        public <K, F> Select column(EntityField<K, F> field) {
            fields.add(field);
            return this;
        }
    }

    /**
     * Custom select expression for instant type
     * 
     * @param type
     * @param select
     * @return {@link QueryStream} for special type
     */
    <F> QueryStream<F> selectType(Class<F> type, Select select);

    /**
     * Custom select expression
     * 
     * @param select
     * @return {@link QueryStream} for {@link Object} array
     */
    default QueryStream<Object[]> select(Select select) {
        return selectType(Object[].class, select);
    }

    /**
     * Custom select expression for instant type
     * 
     * @param expression
     * @param types
     * @return {@link QueryStream} for special type
     */
    <F> QueryStream<F> select(String expression, Class<F> type);

    /**
     * Custom select expression
     * 
     * @param expression
     * @return {@link QueryStream} for {@link Object} array
     */
    default QueryStream<Object[]> select(String expression) {
        return select(expression, Object[].class);
    }

    /**
     * Gets instant field by type
     * 
     * @param field
     * @return {@link QueryStream} for field type
     */
    <F> QueryStream<F> selectOne(EntityField<T, F> field);

    <F> QueryStream<Object[]> select(EntityField<T, F> field);

    <F1, F2> QueryStream<Object[]> select(EntityField<T, F1> field1, EntityField<T, F1> field2);

    <F1, F2, F3> QueryStream<Object[]> select(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3);

    <F1, F2, F3, F4> QueryStream<Object[]> select(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4);

    <F1, F2, F3, F4, F5> QueryStream<Object[]> select(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5);

    <F1, F2, F3, F4, F5, F6> QueryStream<Object[]> select(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5, EntityField<T, F6> field6);

    <F1, F2, F3, F4, F5, F6, F7> QueryStream<Object[]> select(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5, EntityField<T, F6> field6,
            EntityField<T, F7> field7);

    <F1, F2, F3, F4, F5, F6, F7, F8> QueryStream<Object[]> select(EntityField<T, F1> field1, EntityField<T, F2> field2,
            EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5, EntityField<T, F6> field6,
            EntityField<T, F7> field7, EntityField<T, F8> field8);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9> QueryStream<Object[]> select(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10> QueryStream<Object[]> select(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9,
            EntityField<T, F10> field10);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11> QueryStream<Object[]> select(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9,
            EntityField<T, F10> field10, EntityField<T, F11> field11);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12> QueryStream<Object[]> select(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9,
            EntityField<T, F10> field10, EntityField<T, F11> field11, EntityField<T, F12> field12);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13> QueryStream<Object[]> select(EntityField<T, F1> field1,
            EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4, EntityField<T, F5> field5,
            EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8, EntityField<T, F9> field9,
            EntityField<T, F10> field10, EntityField<T, F11> field11, EntityField<T, F12> field12,
            EntityField<T, F13> field13);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14> QueryStream<Object[]> select(
            EntityField<T, F1> field1, EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4,
            EntityField<T, F5> field5, EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8,
            EntityField<T, F9> field9, EntityField<T, F10> field10, EntityField<T, F11> field11,
            EntityField<T, F12> field12, EntityField<T, F13> field13, EntityField<T, F14> field14);

    <F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15> QueryStream<Object[]> select(
            EntityField<T, F1> field1, EntityField<T, F2> field2, EntityField<T, F3> field3, EntityField<T, F4> field4,
            EntityField<T, F5> field5, EntityField<T, F6> field6, EntityField<T, F7> field7, EntityField<T, F8> field8,
            EntityField<T, F9> field9, EntityField<T, F10> field10, EntityField<T, F11> field11,
            EntityField<T, F12> field12, EntityField<T, F13> field13, EntityField<T, F14> field14,
            EntityField<T, F15> field15);
}
