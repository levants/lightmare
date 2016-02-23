package org.lightmare.criteria.config;

import java.lang.reflect.Field;
import java.sql.ResultSet;

/**
 * Configuration interfaces for query initialization and result retriever
 * 
 * @author Levan Tsinadze
 *
 */
public interface Configuration {

    /**
     * Column name resolver
     * 
     * @author Levan Tsinadze
     *
     */
    @FunctionalInterface
    public static interface ColumnResolver {

        /**
         * Resolves column name from {@link Field} instance
         * 
         * @param field
         * @return {@link String} resolved column name
         */
        String resolve(Field field);
    }

    /**
     * Retrieves entity instance from {@link java.sql.ResultSet} instance
     * 
     * @author Levan Tsinadze
     *
     */
    @FunctionalInterface
    public static interface ResultRetriever<T> {

        /**
         * Retrieves result from {@link java.sql.ResultSet} and initializes
         * passed entity {@link Class} instance
         * 
         * @param result
         * @return T instance from {@link java.sql.ResultSet}
         */
        T readRow(ResultSet result);
    }
}
