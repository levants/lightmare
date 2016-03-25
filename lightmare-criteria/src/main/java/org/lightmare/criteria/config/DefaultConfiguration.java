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
package org.lightmare.criteria.config;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.lightmare.criteria.annotations.DBColumn;
import org.lightmare.criteria.annotations.DBTransient;
import org.lightmare.criteria.config.Configuration.ColumnResolver;
import org.lightmare.criteria.config.Configuration.ResultRetriever;
import org.lightmare.criteria.utils.ClassUtils;
import org.lightmare.criteria.utils.CollectionUtils;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Configuration for query parts and result retriever
 * 
 * @author Levan Tsinadze
 *
 */
public class DefaultConfiguration {

    /**
     * Default column name resolver
     * 
     * @author Levan Tsinadze
     *
     */
    public static class DefaultResolver implements ColumnResolver {

        public static String getColumn(Field field) {
            return ObjectUtils.ifIsNull(field.getAnnotation(DBColumn.class), c -> field.getName(), DBColumn::value);
        }

        @Override
        public String resolve(Field field) {
            return getColumn(field);
        }
    }

    /**
     * Default result set reader
     * 
     * @author Levan Tsinadze
     *
     */
    public static class DefaultRetriever<T> implements ResultRetriever<T> {

        private final Class<T> type;

        private static final ConcurrentMap<Class<?>, List<FieldType>> COLUMNS = new ConcurrentHashMap<>();

        /**
         * Functional interface for {@link java.sql.ResultSet} value reader
         * 
         * @author Levan Tsinadze
         *
         */
        @FunctionalInterface
        public static interface ResultGetter {

            Object apply(ResultSet r, String s) throws SQLException;
        }

        /**
         * Wrapper class for field type and retriever function from
         * {@link java.sql.ResultSet} proper to this type
         * 
         * @author Levan Tsinadze
         *
         */
        public static class FieldType {

            final Field field;

            final String name;

            final Class<?> type;

            final ResultGetter function;

            public FieldType(Field field) {
                this.field = field;
                this.name = DefaultResolver.getColumn(field);
                this.type = field.getType();
                this.function = defineFunction();
            }

            /**
             * Defines function to retrieve data from {@link java.sql.ResultSet}
             * by type
             * 
             * @return {@link org.lightmare.criteria.config.DefaultConfiguration.DefaultRetriever.ResultGetter}
             *         for instant field type
             */
            public ResultGetter defineFunction() {

                ResultGetter getter;

                if (this.type.equals(Long.class) || this.type.equals(long.class)) {
                    getter = ResultSet::getLong;
                } else if (this.type.equals(Integer.class) || this.type.equals(int.class)) {
                    getter = ResultSet::getInt;
                } else if (this.type.equals(Short.class) || this.type.equals(short.class)) {
                    getter = ResultSet::getShort;
                } else if (this.type.equals(Byte.class) || this.type.equals(byte.class)) {
                    getter = ResultSet::getByte;
                } else if (this.type.equals(Boolean.class) || this.type.equals(boolean.class)) {
                    getter = ResultSet::getBoolean;
                } else if (this.type.equals(Double.class) || this.type.equals(double.class)) {
                    getter = ResultSet::getDouble;
                } else if (this.type.equals(Float.class) || this.type.equals(float.class)) {
                    getter = ResultSet::getFloat;
                } else if (this.type.equals(BigDecimal.class)) {
                    getter = ResultSet::getBigDecimal;
                } else if (this.type.equals(String.class)) {
                    getter = ResultSet::getString;
                } else {
                    getter = ResultSet::getObject;
                }

                return getter;
            }

            /**
             * Sets field value from {@link java.sql.ResultSet} instance
             * 
             * @param instance
             * @param rs
             * @throws SQLException
             */
            private void getAndSet(Object instance, ResultSet rs) throws SQLException {

                if (Objects.nonNull(rs.getObject(name))) {
                    Object value = function.apply(rs, name);
                    ClassUtils.set(field, instance, value);
                }
            }

            /**
             * Sets field value from {@link java.sql.ResultSet} instance
             * 
             * @param instance
             * @param rs
             */
            public void set(Object instance, ResultSet rs) {
                ObjectUtils.acceptQuietly(instance, rs, this::getAndSet);
            }
        }

        public DefaultRetriever(final Class<T> type) {
            this.type = type;
        }

        private static FieldType getColumnName(Field field) {
            return ObjectUtils.ifIsValid(field, c -> ClassUtils.notAnnotated(c, DBTransient.class), FieldType::new);
        }

        private static List<FieldType> getColumns(Field[] fields) {
            return CollectionUtils.toList(fields, DefaultRetriever::getColumnName);
        }

        private List<FieldType> put(Class<?> type) {

            List<FieldType> columns = ObjectUtils.ifNonNull(type::getDeclaredFields, DefaultRetriever::getColumns,
                    c -> Collections.emptyList());
            COLUMNS.putIfAbsent(type, columns);

            return columns;
        }

        private List<FieldType> getColumns(Class<?> type) {
            return ObjectUtils.getOrInit(() -> COLUMNS.get(type), () -> put(type));
        }

        @Override
        public T readRow(ResultSet result) {

            T instance = ClassUtils.newInstance(type);

            List<FieldType> columns = getColumns(type);
            columns.forEach(c -> c.set(instance, result));

            return instance;
        }
    }
}
