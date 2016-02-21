package org.lightmare.criteria.config;

import org.lightmare.criteria.config.Configuration.ColumnResolver;
import org.lightmare.criteria.config.Configuration.ResultRetriever;

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
}
