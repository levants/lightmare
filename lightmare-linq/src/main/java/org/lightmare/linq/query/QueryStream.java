package org.lightmare.linq.query;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.persistence.TemporalType;

import org.lightmare.linq.lambda.FieldGetter;
import org.lightmare.linq.lambda.FieldSetter;
import org.lightmare.linq.tuples.ParameterTuple;

/**
 * Main interface with query construction methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 */
interface QueryStream<T> {

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
    <F> void addParameter(String key, F value);

    /**
     * Adds custom parameter to composed query
     * 
     * @param key
     * @param value
     * @param temporalType
     */
    <F> void addParameter(String key, F value, TemporalType temporalType);

    // ===================== Getter method composers ========================//

    <F> QueryStream<T> eq(FieldGetter<F> field, F value) throws IOException;

    <F> QueryStream<T> notEq(FieldGetter<F> field, F value) throws IOException;

    <F> QueryStream<T> more(FieldGetter<F> field, F value) throws IOException;

    <F> QueryStream<T> less(FieldGetter<F> field, F value) throws IOException;

    <F> QueryStream<T> moreOrEq(FieldGetter<F> field, F value) throws IOException;

    <F> QueryStream<T> lessOrEq(FieldGetter<F> field, F value) throws IOException;

    QueryStream<T> startsWith(FieldGetter<String> field, String value) throws IOException;

    QueryStream<T> like(FieldGetter<String> field, String value) throws IOException;

    QueryStream<T> endsWith(FieldGetter<String> field, String value) throws IOException;

    QueryStream<T> contains(FieldGetter<String> field, String value) throws IOException;

    <F> QueryStream<T> isNull(FieldGetter<F> field) throws IOException;

    <F> QueryStream<T> notNull(FieldGetter<F> field) throws IOException;

    // ===================== Setter method composers ========================//

    <F> QueryStream<T> eq(FieldSetter<T, F> field, F value) throws IOException;

    <F> QueryStream<T> notEq(FieldSetter<T, F> field, F value) throws IOException;

    <F> QueryStream<T> more(FieldSetter<T, F> field, F value) throws IOException;

    <F> QueryStream<T> less(FieldSetter<T, F> field, F value) throws IOException;

    <F> QueryStream<T> moreOrEq(FieldSetter<T, F> field, F value) throws IOException;

    <F> QueryStream<T> lessOrEq(FieldSetter<T, F> field, F value) throws IOException;

    QueryStream<T> startsWith(FieldSetter<T, String> field, String value) throws IOException;

    QueryStream<T> like(FieldSetter<T, String> field, String value) throws IOException;

    QueryStream<T> endsWith(FieldSetter<T, String> field, String value) throws IOException;

    QueryStream<T> contains(FieldSetter<T, String> field, String value) throws IOException;

    QueryStream<T> isNull(FieldSetter<T, ?> field) throws IOException;

    QueryStream<T> notNull(FieldSetter<T, ?> field) throws IOException;

    // ===========================================================================//

    List<T> toList();

    T get();

    QueryStream<T> where();

    QueryStream<T> and();

    QueryStream<T> or();

    QueryStream<T> openBracket();

    QueryStream<T> closeBracket();

    QueryStream<T> appendPrefix(Object clause);

    QueryStream<T> appendBody(Object clause);

    /**
     * Gets generated JPA query
     * 
     * @return {@link String} JPA query
     */
    String sql();

    Set<ParameterTuple<?>> getParameters();

    void setWerbose(boolean verbose);
}
