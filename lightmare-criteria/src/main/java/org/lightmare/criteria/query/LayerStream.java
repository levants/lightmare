package org.lightmare.criteria.query;

import org.lightmare.criteria.query.internal.layers.LayerProvider;

/**
 * Abstraction of entity type, alias, and
 * {@link org.lightmare.criteria.query.internal.layers.LayerProvider} for
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
     * @return {@link org.lightmare.criteria.query.internal.layers.LayerProvider}
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

    /**
     * Gets generated query
     * 
     * @return {@link String} query
     */
    String sql();
}
