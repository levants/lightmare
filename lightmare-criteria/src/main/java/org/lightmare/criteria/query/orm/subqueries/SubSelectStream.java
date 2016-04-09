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
package org.lightmare.criteria.query.orm.subqueries;

import java.util.List;

import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.orm.builders.SelectStream;
import org.lightmare.criteria.utils.CollectionUtils;

/**
 * Processes SELECT statements for sub queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 * @param <E>
 *            select type parameter
 * @param <Q>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 * @param <O>
 *            {@link org.lightmare.criteria.query.QueryStream} implementation
 *            parameter
 */
public abstract class SubSelectStream<T, E, Q extends QueryStream<E, ? super Q>, O extends QueryStream<Object[], ? super O>>
        extends SelectStream<T, E, Q, O> {

    private final AbstractSubQueryStream<T, ?, ?, ?> stream;

    protected SubSelectStream(AbstractSubQueryStream<T, ?, ?, ?> stream, Class<E> type) {
        super(stream, type);
        this.stream = stream;
    }

    /**
     * Appends SQL part to original query and switches prepare state to called
     */
    private void appendOriginal() {

        String query = super.sql();
        stream.appendToParent(query);
        stream.switchState();
    }

    @Override
    public E get() {
        appendOriginal();
        return null;
    }

    @Override
    public List<E> toList() {
        appendOriginal();
        return null;
    }

    @Override
    public Long count() {
        return null;
    }

    @Override
    public int execute() {
        return CollectionUtils.EMPTY;
    }
}
