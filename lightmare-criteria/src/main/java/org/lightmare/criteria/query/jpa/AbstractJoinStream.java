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
package org.lightmare.criteria.query.jpa;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.lightmare.criteria.functions.EntityField;
import org.lightmare.criteria.tuples.QueryTuple;
import org.lightmare.utils.StringUtils;

/**
 * Implementation for JOIN clause query generation
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter for generated query
 */
abstract class AbstractJoinStream<T extends Serializable> extends AbstractQueryStream<T> {

    protected AbstractJoinStream(EntityManager em, Class<T> entityType, String alias) {
	super(em, entityType, alias);
    }

    /**
     * Processes join statement for passed collection field
     * 
     * @param field
     * @param expression
     * @return {@link QueryTuple} for field and expression
     * @throws IOException
     */
    protected <C extends Collection<?>> QueryTuple oppJoin(EntityField<T, C> field, String expression)
	    throws IOException {

	QueryTuple tuple;

	appendJoin(expression);
	tuple = compose(field);
	appendJoin(tuple.getFieldName());
	appendJoin(StringUtils.SPACE);

	return tuple;
    }
}
