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
package org.lightmare.criteria.functions;

import java.io.Serializable;
import java.util.function.Consumer;

import org.lightmare.criteria.query.LambdaStream;

/**
 * Query consumer to process JPA query brackets, sub queries, aggregates etc.
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 * @param <S>
 *            {@link org.lightmare.criteria.query.LambdaStream} implementation
 */
@FunctionalInterface
public interface QueryConsumer<T, S extends LambdaStream<T, ? super S>> extends Consumer<S>, Serializable {

    @Override
    void accept(S stream);
}
