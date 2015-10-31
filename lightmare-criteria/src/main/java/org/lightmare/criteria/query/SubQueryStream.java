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

import org.lightmare.criteria.lambda.EntityField;

/**
 * Implementation of {@link QueryStream} for sub queries
 * 
 * @author Levan Tsinadze
 * @param <S>
 *            entity type for generated (sub) query
 * @param <T>
 *            entity type for generated query
 */
public interface SubQueryStream<S extends Serializable, T extends Serializable> extends QueryStream<S> {

    // ========================= Entity method composers ====================//

    <F> SubQueryStream<S, T> eq(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException;

    <F> SubQueryStream<S, T> equals(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException;

    <F> SubQueryStream<S, T> notEq(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException;

    <F> SubQueryStream<S, T> notEquals(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException;

    <F> SubQueryStream<S, T> more(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException;

    <F> SubQueryStream<S, T> less(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException;

    <F> SubQueryStream<S, T> moreOrEq(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException;

    <F> SubQueryStream<S, T> lessOrEq(EntityField<S, F> sfield, EntityField<T, F> field) throws IOException;

    SubQueryStream<S, T> startsWith(EntityField<S, String> sfield, EntityField<T, String> field) throws IOException;

    SubQueryStream<S, T> like(EntityField<S, String> sfield, EntityField<T, String> field) throws IOException;

    SubQueryStream<S, T> endsWith(EntityField<S, String> sfield, EntityField<T, String> field) throws IOException;

    SubQueryStream<S, T> contains(EntityField<S, String> sfield, EntityField<T, String> field) throws IOException;

    <F> SubQueryStream<S, T> in(EntityField<S, F> sfield, EntityField<T, Collection<F>> field) throws IOException;
}
