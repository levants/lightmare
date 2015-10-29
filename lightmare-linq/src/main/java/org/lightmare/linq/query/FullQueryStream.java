package org.lightmare.linq.query;

import javax.persistence.EntityManager;

import org.lightmare.linq.links.Filters;

/**
 * Main class for lambda expression analyze and JPA query generator
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public class FullQueryStream<T> extends SetterQueryStream<T> {

    private FullQueryStream(final EntityManager em, final Class<T> entityType) {
	super(em, entityType);
    }

    /**
     * Generates select statements with custom alias
     * 
     * @param em
     * @param entityType
     * @param entityAlias
     * @return {@link FullQueryStream} with select statement
     */
    public static <T> FullQueryStream<T> select(final EntityManager em, final Class<T> entityType,
	    final String entityAlias) {

	FullQueryStream<T> stream = new FullQueryStream<T>(em, entityType);

	stream.appendPrefix(Filters.SELECT).appendPrefix(entityAlias).appendPrefix(Filters.FROM);
	stream.appendPrefix(entityType.getName()).appendPrefix(Filters.AS).appendPrefix(entityAlias);
	stream.appendPrefix(NEW_LINE);

	return stream;
    }

    /**
     * Generates select statements with default alias
     * 
     * @param em
     * @param entityType
     * @return {@link FullQueryStream} with select statement
     */
    public static <T> FullQueryStream<T> select(final EntityManager em, Class<T> entityType) {
	return select(em, entityType, DEFAULT_ALIAS);
    }
}
