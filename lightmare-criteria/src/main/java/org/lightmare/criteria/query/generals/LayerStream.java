package org.lightmare.criteria.query.generals;

import org.lightmare.criteria.query.internal.connectors.LayerProvider;

/**
 * Abstraction of entity type, alias, and
 * {@link org.lightmare.criteria.query.internal.connectors.LayerProvider} for
 * various data base layers
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
interface LayerStream<T> {

    /**
     * Gets data base layer provider implementation
     * 
     * @return {@link org.lightmare.criteria.query.internal.connectors.LayerProvider}
     *         implementation
     */
    LayerProvider getLayerProvider();

    /**
     * Gets entity {@link Class} for this query stream
     * 
     * @return {@link Class} entity type
     */
    Class<T> getEntityType();

    /**
     * Gets entity alias for query
     * 
     * @return {@link String} entity alias
     */
    String getAlias();
}
