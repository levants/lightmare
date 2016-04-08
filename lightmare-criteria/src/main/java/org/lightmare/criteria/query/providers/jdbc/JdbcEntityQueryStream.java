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
package org.lightmare.criteria.query.providers.jdbc;

import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.builders.AbstractQueryStream;
import org.lightmare.criteria.query.internal.orm.builders.SelectStream;
import org.lightmare.criteria.query.layers.LayerProvider;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryStream} for JDBC
 * queries
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
class JdbcEntityQueryStream<T> extends AbstractJdbcQueryWrapper<T> implements JdbcQueryStream<T> {

    protected JdbcEntityQueryStream(final LayerProvider provider, final Class<T> entityType) {
        super(provider, entityType);
    }

    @Override
    public <E, S extends LambdaStream<E, ? super S>> S initJoinQuery(String alias, Class<E> joinType) {
        S joinQuery = JdbcUtils.initJoinQuery(this, alias, joinType);
        return joinQuery;
    }

    @Override
    public <E, S extends QueryStream<E, ? super S>> S initSubQuery(Class<E> subType) {
        S subQuery = JdbcUtils.initSubQuery(this, subType);
        return subQuery;
    }

    @Override
    public <E> SelectStream<T, E, ?, ?> initSelectQuery(Class<E> selectType) {

        SelectStream<T, E, ?, ?> stream;

        AbstractQueryStream<T, ?, ?> parent = this;
        stream = new JdbcSelectStream<>(parent, selectType);

        return stream;
    }
}
