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
import org.lightmare.criteria.annotations.DBTable;
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

    private static ColumnResolver columnResolver;

    private static ResultRetriever resultRetriever;

    public static ColumnResolver defaultJdbcColumnResolver = c -> c.getName();

    public static void configure(ColumnResolver columnNameResolver, ResultRetriever resultSetRetriever) {
        columnResolver = columnNameResolver;
        resultRetriever = resultSetRetriever;
    }

    public static ColumnResolver getColumnResolver() {
        return columnResolver;
    }

    public static ResultRetriever getResultRetriever() {
        return resultRetriever;
    }

    public static class TableResolver {

        public String resolve(Class<?> type) {

            String name;

            DBTable table = type.getAnnotation(DBTable.class);
            name = ObjectUtils.getOrInit(table::value, type::getName);

            return name;
        }
    }

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
    public static class DefaultRetriever implements ResultRetriever {

        private static final ConcurrentMap<Class<?>, List<FieldType>> COLUMNS = new ConcurrentHashMap<>();

        /**
         * Functional interface for {@link java.sql.ResultSet} value reader
         * 
         * @author Levan Tsinadze
         *
         */
        @FunctionalInterface
        private static interface ResultGetter {

            Object apply(ResultSet r, String s) throws SQLException;
        }

        private static class FieldType {

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

            public void set(Object instance, ResultSet rs) {

                try {
                    if (Objects.nonNull(rs.getObject(name))) {
                        Object value = function.apply(rs, name);
                        ClassUtils.set(field, instance, value);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
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
        public <T> T readRow(ResultSet result, Class<T> type) {

            T instance = ClassUtils.newInstance(type);

            List<FieldType> columns = getColumns(type);
            columns.forEach(c -> c.set(instance, result));

            return instance;
        }
    }
}
