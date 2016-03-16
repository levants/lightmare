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
package org.lightmare.criteria.query.mongo;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.query.LambdaStream;

/**
 * Implementation of {@link org.lightmare.criteria.query.LambdaStream} for
 * MongoDB filters
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
public interface MongoStream<T> extends LambdaStream<T, MongoStream<T>> {

    @Override
    default MongoStream<T> stream() {
        return this;
    }

    @Override
    default <F extends Comparable<? super F>> MongoStream<T> greaterThan(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return gt(field, value);
    }

    @Override
    default <F extends Comparable<? super F>> MongoStream<T> lessThan(EntityField<T, Comparable<? super F>> field,
            Comparable<? super F> value) {
        return lt(field, value);
    }

    @Override
    default <F extends Comparable<? super F>> MongoStream<T> greaterThanOrEqualTo(
            EntityField<T, Comparable<? super F>> field, Comparable<? super F> value) {
        return ge(field, value);
    }

    @Override
    default <F extends Comparable<? super F>> MongoStream<T> lessThanOrEqualTo(
            EntityField<T, Comparable<? super F>> field, Comparable<? super F> value) {
        return le(field, value);
    }
}
