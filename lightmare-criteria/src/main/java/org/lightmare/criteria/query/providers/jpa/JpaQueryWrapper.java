package org.lightmare.criteria.query.providers.jpa;

import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;

import org.lightmare.criteria.query.providers.JpaQueryStream;

/**
 * Interface for JPA query additional configuration
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface JpaQueryWrapper<T> {

    /**
     * Set the position of the first result to retrieve.
     *
     * @param startPosition
     *            position of the first result, numbered from 0
     *
     * @return the same
     *         {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         instance
     *
     */
    JpaQueryStream<T> setFirstResult(int startPosition);

    /**
     * The position of the first result the query object was set to retrieve.
     * Returns 0 if <code>setFirstResult</code> was not applied to the query
     * object.
     *
     * @return position of the first result
     *
     * @since Java Persistence 2.0
     */
    int getFirstResult();

    /**
     * Set a query property or hint. The hints elements may be used to specify
     * query properties and hints. Properties defined by this specification must
     * be observed by the provider. Vendor-specific hints that are not
     * recognized by a provider must be silently ignored. Portable applications
     * should not rely on the standard timeout hint. Depending on the database
     * in use and the locking mechanisms used by the provider, this hint may or
     * may not be observed.
     *
     * @param hintName
     *            name of the property or hint
     * @param value
     *            value for the property or hint
     *
     * @return the same
     *         {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         instance
     *
     */
    JpaQueryStream<T> setHint(String hintName, Object value);

    /**
     * Get the properties and hints and associated values that are in effect for
     * the query instance.
     *
     * @return query properties and hints
     *
     * @since Java Persistence 2.0
     */
    Map<String, Object> getHints();

    /**
     * Set the flush mode type to be used for the query execution. The flush
     * mode type applies to the query regardless of the flush mode type in use
     * for the entity manager.
     *
     * @param flushMode
     *            flush mode
     *
     * @return the same
     *         {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         instance
     */
    JpaQueryStream<T> setFlushMode(FlushModeType flushMode);

    /**
     * Set the lock mode type to be used for the query execution.
     *
     * @param lockMode
     *            lock mode
     *
     * @return the same
     *         {@link org.lightmare.criteria.query.providers.JpaQueryStream}
     *         instance
     */
    JpaQueryStream<T> setLockMode(LockModeType lockMode);
}
