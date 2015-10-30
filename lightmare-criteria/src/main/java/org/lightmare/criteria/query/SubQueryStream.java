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

import org.lightmare.criteria.lambda.EntityField;

/**
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated (sub) query
 */
public interface SubQueryStream<S extends Serializable, T extends Serializable> extends QueryStream<S> {

    // ========================= Entity method composers ====================//

    <F> QueryStream<T> eq(EntityField<S, F> field, EntityField<T, F> sfield) throws IOException;

    <F> QueryStream<T> equals(EntityField<T, F> sfield) throws IOException;

    <F> QueryStream<T> notEq(EntityField<T, F> sfielde) throws IOException;

    <F> QueryStream<T> notEquals(EntityField<T, F> sfield) throws IOException;

    <F> QueryStream<T> more(EntityField<T, F> sfield) throws IOException;

    <F> QueryStream<T> less(EntityField<T, F> sfield) throws IOException;

    <F> QueryStream<T> moreOrEq(EntityField<T, F> sfield) throws IOException;

    <F> QueryStream<T> lessOrEq(EntityField<T, F> sfield) throws IOException;

    QueryStream<T> startsWith(EntityField<T, String> sfield) throws IOException;

    QueryStream<T> like(EntityField<T, String> sfield) throws IOException;

    QueryStream<T> endsWith(EntityField<T, String> sfield) throws IOException;

    QueryStream<T> contains(EntityField<T, String> sfield) throws IOException;
}
