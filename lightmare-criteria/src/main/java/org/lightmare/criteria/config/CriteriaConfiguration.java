package org.lightmare.criteria.config;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import org.lightmare.criteria.annotations.DBColumn;
import org.lightmare.criteria.annotations.DBTable;
import org.lightmare.criteria.config.Configuration.ColumnResolver;
import org.lightmare.criteria.config.Configuration.ResultRetriever;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Configuration for query parts and result retriever
 * 
 * @author Levan Tsinadze
 *
 */
public class CriteriaConfiguration {

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

    public static class DefaultResolver implements ColumnResolver {

        @Override
        public String resolve(Field field) {

            String name;

            DBColumn column = field.getAnnotation(DBColumn.class);
            name = ObjectUtils.getOrInit(column::value, field::getName);

            return name;
        }
    }

    public static class DefaultRetriever implements ResultRetriever {

        @Override
        public <T> T readRow(ResultSet result, Class<T> type) {

            T instance;

            try {
                instance = type.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }

            return instance;
        }
    }
}
