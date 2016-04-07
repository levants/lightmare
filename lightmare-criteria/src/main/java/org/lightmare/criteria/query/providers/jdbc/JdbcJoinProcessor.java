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
import org.lightmare.criteria.query.internal.orm.subqueries.JoinProcessor;

/**
 * Implementation of
 * {@link org.lightmare.criteria.query.internal.orm.subqueries.SubQueryStream}
 * to process JOIN statements
 * 
 * @author Levan Tsiadze
 *
 * @param <S>
 *            join entity type parameter for generated query
 * @param <T>
 *            entity type parameter for generated query
 */
public class JdbcJoinProcessor<S, T> extends JoinProcessor<S, T, JdbcQueryStream<S>, JdbcQueryStream<Object[]>>
        implements JdbcQueryStream<S> {

    public JdbcJoinProcessor(AbstractQueryStream<T, ?, ?> parent, String alias, Class<S> entityType) {
        super(parent, alias, entityType);
    }

    public JdbcJoinProcessor(AbstractQueryStream<T, ?, ?> parent, Class<S> entityType) {
        super(parent, entityType);
    }

    @Override
    public <E, L extends LambdaStream<E, ? super L>> L initJoinQuery(String alias, Class<E> joinType) {
        L joinQuery = JdbcUtils.initJoinQuery(this, alias, joinType);
        return joinQuery;
    }

    @Override
    public <E, L extends QueryStream<E, ? super L>> L initSubQuery(Class<E> subType) {
        L subQuery = JdbcUtils.initSubQuery(this, subType);
        return subQuery;
    }

    @Override
    public <E> SelectStream<S, E, ?, ?> initSelectQuery(Class<E> selectType) {
        return new JdbcSubSelectStream<S, E>(this, selectType);
    }
}
