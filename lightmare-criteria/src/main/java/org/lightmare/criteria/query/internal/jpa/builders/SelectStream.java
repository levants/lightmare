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

import org.lightmare.criteria.query.JPAQueryStream;
import org.lightmare.criteria.query.internal.jpa.links.Parts;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Utility class to construct SELECT clause by fields
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 */
public class SelectStream<T, E> extends JPAQueryStream<E> {

    // Real entity type before select statement
    private final Class<?> realEntityType;

    protected SelectStream(AbstractQueryStream<T> stream, Class<E> type) {
        super(stream.getEntityManager(), type, stream.getAlias());
        this.realEntityType = stream.entityType;
        this.columns.append(stream.columns);
        this.body.append(stream.body);
        this.orderBy.append(stream.orderBy);
        this.groupBy.append(stream.groupBy);
        this.aggregateFields = stream.getAggregateFields();
        this.parameters.addAll(stream.parameters);
    }

    /**
     * Appends comma to GROUP BY clause if it is not empty
     */
    private void validateAndCommaCount() {

        if (StringUtils.validAll(count, columns)) {
            count.append(Parts.COMMA).append(StringUtils.SPACE);
        }
    }

    @Override
    public String sql() {

        String value;

        clearSql();
        appendAggregate(count);
        appendFromClause(realEntityType, alias, columns);
        validateAndCommaCount();
        count.append(columns);
        generateBody(count);
        sql.append(groupBy);
        sql.append(orderBy);
        sql.append(suffix);
        value = sql.toString();

        return value;
    }
}