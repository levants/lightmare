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
package org.lightmare.criteria.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TemporalType;

import org.lightmare.criteria.lambda.EntityField;
import org.lightmare.criteria.tuples.ParameterTuple;

/**
 * Main interface with query construction methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public interface QueryStream<T extends Serializable> {

    String DEFAULT_ALIAS = "c";

    char NEW_LINE = '\n';

    int START = 0;

    /**
     * Instantiates entity type for getter query composition
     * 
     * @return T instance of entity type
     * @throws IOException
     */
    T instance() throws IOException;

    /**
     * Adds custom parameter to composed query
     * 
     * @param tuple
     * @param value
     */
    void addParameter(String key, Object value);

    /**
     * Adds custom parameter to composed query
     * 
     * @param key
     * @param value
     * @param temporalType
     */
    void addParameter(String key, Object value, TemporalType temporalType);

    /**
     * Adds custom parameters to composed query
     * 
     * @param parameters
     */
    void addParameters(Map<String, Object> parameters);

    // ========================= Entity method composers ====================//

    <F> QueryStream<T> eq(EntityField<T, F> field, F value) throws IOException;

    <F> QueryStream<T> equals(EntityField<T, F> field, F value) throws IOException;

    <F> QueryStream<T> notEq(EntityField<T, F> field, F value) throws IOException;

    <F> QueryStream<T> notEquals(EntityField<T, F> field, F value) throws IOException;

    <F> QueryStream<T> more(EntityField<T, F> field, F value) throws IOException;

    <F> QueryStream<T> less(EntityField<T, F> field, F value) throws IOException;

    <F> QueryStream<T> moreOrEq(EntityField<T, F> field, F value) throws IOException;

    <F> QueryStream<T> lessOrEq(EntityField<T, F> field, F value) throws IOException;

    QueryStream<T> startsWith(EntityField<T, String> field, String value) throws IOException;

    QueryStream<T> like(EntityField<T, String> field, String value) throws IOException;

    QueryStream<T> endsWith(EntityField<T, String> field, String value) throws IOException;

    QueryStream<T> contains(EntityField<T, String> field, String value) throws IOException;

    <F> QueryStream<T> in(EntityField<T, F> field, Collection<F> values) throws IOException;

    QueryStream<T> isNull(EntityField<T, ?> field) throws IOException;

    QueryStream<T> notNull(EntityField<T, ?> field) throws IOException;

    // =========================order=by=====================================//
    QueryStream<T> orderBy(EntityField<T, ?> field) throws IOException;

    QueryStream<T> orderByDesc(EntityField<T, ?> field) throws IOException;
    // ======================================================================//

    /**
     * Set clause for bulk UPDATE query
     * 
     * @param field
     * @param value
     * @return {@link QueryStream} current instance
     * @throws IOException
     */
    <F> QueryStream<T> set(EntityField<T, F> field, F value) throws IOException;

    // ======================================================================//

    QueryStream<T> where();

    QueryStream<T> and();

    QueryStream<T> or();

    QueryStream<T> openBracket();

    QueryStream<T> closeBracket();

    // ======================================================================//

    /**
     * Appends to generated query prefix custom clause
     * 
     * @param clause
     * @return {@link QueryStream} current instance
     */
    QueryStream<T> appendPrefix(Object clause);

    /**
     * Appends to generated query body custom clause
     * 
     * @param clause
     * @return {@link QueryStream} current instance
     */
    QueryStream<T> appendBody(Object clause);

    /**
     * Gets generated JPA query
     * 
     * @return {@link String} JPA query
     */
    String sql();

    /**
     * Gets query parameters
     * 
     * @return {@link Set} of {@link ParameterTuple}s
     */
    Set<ParameterTuple> getParameters();

    // ================================= Result =============================//

    /**
     * Runs generated query {@link javax.persistence.Query#getResultList()} and
     * retrieves result list
     * 
     * @return {@link List} of query results
     * @see javax.persistence.Query#getResultList()
     */
    List<T> toList();

    /**
     * Runs generated query {@link javax.persistence.Query#getSingleResult()}
     * and retrieves single result
     * 
     * @return T single query result
     * @see javax.persistence.Query#getSingleResult()
     */
    T get();

    /**
     * Executes generates bulk update or delete query
     * {@link javax.persistence.Query#executeUpdate()} and returns number of
     * modified rows
     * 
     * @return <code>int<code/> number of modified rows
     * @see javax.persistence.Query#executeUpdate()
     */
    int execute();
}
