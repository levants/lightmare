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

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.functions.SubQueryConsumer;
import org.lightmare.criteria.query.internal.GeneralExpression;
import org.lightmare.criteria.query.internal.jpa.Expression;
import org.lightmare.criteria.query.internal.jpa.ColumnExpression;

/**
 * Main interface with query construction methods
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 */
public interface QueryStream<T> extends GeneralExpression<T>, Expression<T>, ColumnExpression<T> {

    /**
     * Generates query part for embedded entity fields
     * 
     * @param field
     * @param consumer
     * @return {@link QueryStream} current instance
     */
    <F> QueryStream<T> embedded(EntityField<T, F> field, SubQueryConsumer<F, T> consumer);

    @Override
    default QueryStream<T> where() {
        return this;
    }

    @Override
    default QueryStream<T> openBracket() {
        return GeneralExpression.super.openBracket();
    }
}
