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
package org.lightmare.criteria.query.internal;

import java.io.Serializable;

import org.lightmare.criteria.lambda.LambdaUtils;
import org.lightmare.criteria.query.internal.orm.builders.AbstractQueryStream;
import org.lightmare.criteria.tuples.EmbeddedTuple;
import org.lightmare.criteria.tuples.QueryTuple;

/**
 * Implementation of {@link org.lightmare.criteria.query.QueryStream} to process
 * embedded or "one to one" or "many to one" joined field statements
 * 
 * @author Levan Tsinadze
 *
 * @param <S>
 *            embedded entity type parameter
 * @param <T>
 *            query type parameter
 */
public class EntityEmbeddedStream<S, T> extends EntitySubQueryStream<S, T> {

    private final String embeddedName;

    protected EntityEmbeddedStream(final AbstractQueryStream<T> parent, final Class<S> type,
            final String embeddedName) {
        super(parent, parent.getAlias(), type);
        this.embeddedName = embeddedName;
    }

    @Override
    public boolean validateOperator() {
        return parent.validateOperator();
    }

    @Override
    protected QueryTuple resolve(Serializable field) {

        QueryTuple tuple;

        QueryTuple temp = super.resolve(field);
        if (temp.getEntityType().isAssignableFrom(parent.getEntityType())) {
            tuple = temp;
        } else {
            tuple = EmbeddedTuple.of(temp, embeddedName);
        }

        return tuple;
    }

    @Override
    protected QueryTuple compose(Serializable field) {

        QueryTuple tuple;

        QueryTuple temp = super.resolve(field);
        if (temp.getEntityType().isAssignableFrom(parent.getEntityType())) {
            LambdaUtils.setGenericIfValid(parent.getEntityType(), temp);
            tuple = temp;
        } else {
            LambdaUtils.setGenericIfValid(this.entityType, temp);
            tuple = EmbeddedTuple.of(temp, embeddedName);
        }

        return tuple;
    }

    @Override
    public String sql() {

        String value;

        sql.append(body);
        value = sql.toString();

        return value;
    }
}
