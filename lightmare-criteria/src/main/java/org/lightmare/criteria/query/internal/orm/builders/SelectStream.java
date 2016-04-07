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
package org.lightmare.criteria.query.internal.orm.builders;

import org.lightmare.criteria.query.LambdaStream;
import org.lightmare.criteria.query.QueryStream;
import org.lightmare.criteria.query.internal.orm.links.Parts;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Utility class to construct SELECT clause by fields
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type for generated query
 * @param <E>
 *            select type parameter
 */
public abstract class SelectStream<T, E, Q extends QueryStream<E, ? super Q>, O extends QueryStream<Object[], ? super O>>
        extends EntityQueryStream<E, Q, O> {

    // Real entity type before select statement
    private final Class<?> realEntityType;

    protected SelectStream(AbstractQueryStream<T, ?, ?> stream, Class<E> type) {
        super(stream.getLayerProvider(), type);
        this.alias = stream.getAlias();
        this.realEntityType = stream.entityType;
        this.columns.append(stream.columns);
        this.from.append(stream.from);
        this.body.append(stream.body);
        this.orderBy.append(stream.orderBy);
        this.groupBy.append(stream.groupBy);
        this.aggregateFields = stream.getAggregateFields();
        this.aggregateQueue = stream.getAggregateQueue();
        this.parameters.addAll(stream.parameters);
    }

    @Override
    public <U, S extends LambdaStream<U, ? super S>> S initJoinQuery(String alias, Class<U> joinType) {
        return null;
    }

    @Override
    public <U, S extends QueryStream<U, ? super S>> S initSubQuery(Class<U> subType) {
        return null;
    }

    @Override
    public <U> SelectStream<E, U, ?, ?> initSelectQuery(Class<U> selectType) {
        return null;
    }

    /**
     * Appends comma to GROUP BY clause if it is not empty
     */
    private void validateAndCommaCount() {

        if (StringUtils.validAll(count, columns)) {
            count.append(Parts.COMMA).append(StringUtils.SPACE);
        }
    }

    /**
     * Generates prefix for SELECT clause
     */
    private void appendCountPrefix() {

        validateAndCommaCount();
        StringBuilder prefixBuilder = new StringBuilder();
        prefixBuilder.append(columns);
        prefixBuilder.append(from);
        count.append(prefixBuilder);
    }

    /**
     * Generates SELECT clause for JPA query
     */
    private void appendSelectPrefix() {

        StringUtils.clear(count);
        appendAggregate(count);
        appendFromClause(realEntityType, alias, from);
        appendCountPrefix();
    }

    /**
     * Adds ORDER BY, GROUP BY, HAVING and other suffixes
     */
    private void appendFooter() {

        sql.append(orderBy);
        sql.append(groupBy);
        sql.append(having);
        sql.append(suffix);
    }

    @Override
    public String sql() {

        String value;

        clearSql();
        appendSelectPrefix();
        generateBody(count);
        appendFooter();
        value = sql.toString();

        return value;
    }
}
