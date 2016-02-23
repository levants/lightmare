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
     * To retrieve entity from {@link java.sql.ResultSet} instance
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
