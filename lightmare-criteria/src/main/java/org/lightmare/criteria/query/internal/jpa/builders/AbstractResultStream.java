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
package org.lightmare.criteria.query.internal.jpa.builders;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.lightmare.criteria.tuples.ParameterTuple;

/**
 * Abstract class for generated JPA query result
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
abstract class AbstractResultStream<T> extends AbstractJoinStream<T> {

    protected AbstractResultStream(EntityManager em, Class<T> entityType, String alias) {
        super(em, entityType, alias);
    }

    @Override
    public Long count() {

        Long result;

        TypedQuery<Long> query = initCountQuery();
        result = query.getSingleResult();

        return result;
    }

    @Override
    public List<T> toList() {

        List<T> results;

        TypedQuery<T> query = initTypedQuery();
        results = query.getResultList();

        return results;
    }

    @Override
    public T get() {

        T result;

        TypedQuery<T> query = initTypedQuery();
        result = query.getSingleResult();

        return result;
    }

    @Override
    public int execute() {

        int result;

        Query query = initBulkQuery();
        result = query.executeUpdate();

        return result;
    }

    @Override
    public Set<ParameterTuple> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return sql();
    }
}
