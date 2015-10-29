package org.lightmare.criteria.query;

import java.io.Serializable;

import javax.persistence.EntityManager;

/**
 * Provider class to initialize {@link QueryStream} by clause
 * 
 * @author Levan Tsinadze
 *
 */
public class QueryProvider {

    /**
     * Generates select statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link FullQueryStream} with select statement
     */
    public static <T extends Serializable> QueryStream<T> select(final EntityManager em, final Class<T> entityType,
	    final String entityAlias) {
	return FullQueryStream.select(em, entityType, entityAlias);
    }

    /**
     * Generates select statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link FullQueryStream} with select statement
     */
    public static <T extends Serializable> QueryStream<T> select(final EntityManager em, Class<T> entityType) {
	return FullQueryStream.select(em, entityType, QueryStream.DEFAULT_ALIAS);
    }
}
