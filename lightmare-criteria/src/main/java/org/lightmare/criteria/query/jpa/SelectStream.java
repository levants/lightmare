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

import java.io.Serializable;

import org.lightmare.criteria.query.FullQueryStream;

/**
 * Utility class to construct SELECT by fields
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public class SelectStream<T extends Serializable> extends FullQueryStream<Object[]> {

    // Real entity type before select statement
    private final Class<?> realEntityType;

    protected SelectStream(AbstractQueryStream<T> stream) {
	super(stream.getEntityManager(), Object[].class, stream.getAlias());
	this.realEntityType = stream.entityType;
	this.columns.append(stream.columns);
	this.body.append(stream.body);
	this.orderBy.append(stream.orderBy);
	this.parameters.addAll(stream.parameters);
    }

    @Override
    public String sql() {

	String value;

	sql.delete(START, sql.length());
	appendFromClause(realEntityType, alias, columns);
	generateBody(columns);
	sql.append(orderBy);
	sql.append(suffix);
	value = sql.toString();

	return value;
    }
}
