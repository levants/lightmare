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
import org.lightmare.criteria.functions.SelectConsumer;
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

        /**
         * Utility class to accept select statement
         * 
         * @param select
         */
        private static Select accept(SelectConsumer select) {

            Select columns = Select.select();
            select.accept(columns);

            return columns;
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
     * @param type
     * @param select
     * @return {@link QueryStream} for special type
     */
    default <F> QueryStream<F> selectType(Class<F> type, SelectConsumer select) {

        QueryStream<F> stream;

        Select columns = Select.accept(select);
        stream = selectType(type, columns);

        return stream;
    }

    /**
     * Custom select expression
     * 
     * @param select
     * @return {@link QueryStream} for {@link Object} array
     */
    default QueryStream<Object[]> selectAll(SelectConsumer select) {

        QueryStream<Object[]> stream;

        Select columns = Select.accept(select);
        stream = select(columns);

        return stream;
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

    <F> QueryStream<Object[]> select(EntityField<T, F> field);

    /**
     * Gets instant field by type
     * 
     * @param field
     * @return {@link QueryStream} for field type
     */
    <F> QueryStream<F> selectType(EntityField<T, F> field);
}
