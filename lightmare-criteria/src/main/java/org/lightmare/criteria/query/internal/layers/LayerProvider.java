/*
 * Lightmare-criteria, JPA-QL query generator using lambda expressions
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.criteria.query.internal.layers;

import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Provider for query layer
 * 
 * @author Levan Tsinadze
 *
 */
public interface LayerProvider {

    /**
     * Generates {@link org.lightmare.criteria.query.internal.layers.QueryLayer}
     * for entity type
     * 
     * @param sql
     * @param type
     * @return {@link org.lightmare.criteria.query.internal.layers.QueryLayer}
     *         implementation
     */
    <T> QueryLayer<T> query(Object sql, Class<T> type);

    /**
     * Generates raw typed query
     * 
     * @param sql
     * @return {@link org.lightmare.criteria.query.internal.layers.QueryLayer}
     *         implementation
     */
    QueryLayer<?> query(Object sql);

    /**
     * Gets appropriated data base table name
     * 
     * @param type
     * @return {@link String} table name
     */
    String getTableName(Class<?> type);

    /**
     * Gets appropriated data base column name
     * 
     * @param tuple
     * @return {@link String} column name
     */
    String getColumnName(QueryTuple tuple);

    String getSelectType(String alias);

    String getCountType(String alias);

    String alias();

    void close();
}
