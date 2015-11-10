/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
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

import org.lightmare.criteria.utils.StringUtils;

/**
 * Extension of {@link QueryTuple} for embedded entity fields resolving
 * 
 * @author Levan Tsinadze
 *
 */
public class EmbeddedTuple extends QueryTuple {

    private static final long serialVersionUID = 1L;

    private final String embeddedName;

    public EmbeddedTuple(final QueryTuple tuple, final String embeddedName) {
        super(tuple.getEntityName(), tuple.getMethodName(), tuple.getArguments(), tuple.getFieldName());
        setEntityType(tuple.getEntityType());
        setMethod(tuple.getMethod());
        setField(tuple.getField());
        setAlias(tuple.getAlias());
        this.embeddedName = embeddedName;
    }

    @Override
    public String getFieldName() {
        return StringUtils.concat(embeddedName, StringUtils.DOT, super.getFieldName());
    }
}
