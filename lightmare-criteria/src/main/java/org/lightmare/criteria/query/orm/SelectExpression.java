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
package org.lightmare.criteria.query.orm;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.SelectConsumer;
import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Interface to generate SELECT clause for instant fields
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 * @param <Q>
 *            {@link org.lightmare.criteria.query.LambdaStream} implementation
 *            parameter
 * @param <O>
 *            {@link org.lightmare.criteria.query.LambdaStream} implementation
 *            parameter
 */
public interface SelectExpression<T, Q extends LambdaStream<T, ? super Q>, O extends LambdaStream<Object[], ? super O>> {

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
            ObjectUtils.accept(select, columns);

            return columns;
        }
    }

    /**
     * Custom select expression for instant type
     * 
     * @param type
     * @param select
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     *         for special type
     */
    <F, S extends LambdaStream<F, ? super S>> S selectType(Class<F> type, Select select);

    /**
     * Custom select expression
     * 
     * @param select
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     *         for {@link Object} array
     */
    default O select(Select select) {
        return selectType(Object[].class, select);
    }

    /**
     * Custom select expression for instant type
     * 
     * @param type
     * @param select
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     *         for special type
     */
    default <F, S extends LambdaStream<F, ? super S>> S selectType(Class<F> type, SelectConsumer select) {

        S stream;

        Select columns = Select.accept(select);
        stream = selectType(type, columns);

        return stream;
    }

    /**
     * Custom select expression
     * 
     * @param select
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     *         for {@link Object} array
     */
    default O selectAll(SelectConsumer select) {

        O stream;

        Select columns = Select.accept(select);
        stream = select(columns);

        return stream;
    }

    /**
     * Custom select expression for instant type
     * 
     * @param expression
     * @param type
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     *         for special type
     */
    <F, S extends LambdaStream<F, ? super S>> S select(String expression, Class<F> type);

    /**
     * Custom select expression
     * 
     * @param expression
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     *         for {@link Object} array
     */
    default O select(String expression) {
        return select(expression, Object[].class);
    }

    <F> O select(EntityField<T, F> field);

    /**
     * Gets instant field by type
     * 
     * @param field
     * @return {@link org.lightmare.criteria.query.LambdaStream} implementation
     *         for field type
     */
    <F, S extends LambdaStream<F, ? super S>> S selectType(EntityField<T, F> field);
}
