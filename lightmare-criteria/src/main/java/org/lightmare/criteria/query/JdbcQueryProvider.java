package org.lightmare.criteria.query;

import javax.persistence.EntityManager;

/**
 * Query provider for JDBC layer
 * 
 * @author Levan Tsinadze
 *
 */
public abstract class JdbcQueryProvider {

    /**
     * Generates DELETE statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link org.lightmare.criteria.query.QueryStream} with select
     *         statement
     */
    public static <T> QueryStream<T> delete(final EntityManager em, final Class<T> entityType,
            final String entityAlias) {
        return JpaQueryStream.delete(em, entityType, entityAlias);
    }

    /**
     * Generates DELETE statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link org.lightmare.criteria.query.QueryStream} with select
     *         statement
     */
    public static <T> QueryStream<T> delete(final EntityManager em, Class<T> entityType) {
        return JpaQueryStream.delete(em, entityType, QueryStream.DEFAULT_ALIAS);
    }

    /**
     * Generates UPDATE statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link org.lightmare.criteria.query.QueryStream} with select
     *         statement
     */
    public static <T> QueryStream<T> update(final EntityManager em, final Class<T> entityType,
            final String entityAlias) {
        return JpaQueryStream.update(em, entityType, entityAlias);
    }

    /**
     * Generates UPDATE statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link org.lightmare.criteria.query.QueryStream} with select
     *         statement
     */
    public static <T> QueryStream<T> update(final EntityManager em, Class<T> entityType) {
        return JpaQueryStream.update(em, entityType, QueryStream.DEFAULT_ALIAS);
    }

    /**
     * Generates SELECT statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link org.lightmare.criteria.query.QueryStream} with select
     *         statement
     */
    public static <T> QueryStream<T> select(final EntityManager em, final Class<T> entityType,
            final String entityAlias) {
        return JpaQueryStream.query(em, entityType, entityAlias);
    }

    /**
     * Generates SELECT statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link org.lightmare.criteria.query.QueryStream} with select
     *         statement
     */
    public static <T> QueryStream<T> select(final EntityManager em, Class<T> entityType) {
        return JpaQueryStream.query(em, entityType, QueryStream.DEFAULT_ALIAS);
    }
}
