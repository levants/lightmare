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
package org.lightmare.criteria.tuples;

import java.io.Serializable;

import javax.persistence.TemporalType;

/**
 * Query field and entity type container class
 * 
 * @author Levan Tsinadze
 *
 */
public class QueryTuple implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String entity;

    private final String method;

    private final String field;

    private TemporalType temporalType;

    private String alias;

    private static final String ALIAS_PREFIX = "c";

    public QueryTuple(final String entity, final String method, final String field) {
	this.method = method;
	this.entity = entity;
	this.field = field;
    }

    public String getEntity() {
	return entity;
    }

    public String getMethod() {
	return method;
    }

    public String getField() {
	return field;
    }

    public TemporalType getTemporalType() {
	return temporalType;
    }

    public void setTemporalType(TemporalType temporalType) {
	this.temporalType = temporalType;
    }

    public String getAlias() {
	return alias;
    }

    public void setAlias(String alias) {
	this.alias = alias;
    }

    public boolean hasNoAlias() {
	return (this.alias == null || this.alias.isEmpty());
    }

    public void setAlias(int index) {

	if (this.alias == null || this.alias.isEmpty()) {
	    this.alias = ALIAS_PREFIX.concat(String.valueOf(index));
	}
    }

    public String getEntityType() {
	return entity.concat(" ").concat(alias);
    }
}
