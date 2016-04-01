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
package org.lightmare.criteria.query.internal.orm.subqueries;

import org.lightmare.criteria.query.internal.orm.builders.AbstractQueryStream;
import org.lightmare.criteria.utils.CollectionUtils;

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
public class EntityJoinProcessor<S, T> extends EntitySubQueryStream<S, T> {

    private boolean parentOperator;

    private boolean onClause;

    private int onCount;

    public EntityJoinProcessor(AbstractQueryStream<T> parent, String alias, Class<S> entityType) {
        super(parent, alias, entityType);
        this.onClause = Boolean.TRUE;
    }

    public EntityJoinProcessor(AbstractQueryStream<T> parent, Class<S> entityType) {
        super(parent, entityType);
    }

    /**
     * Validates JOIN clause operators
     * 
     * @return <code>boolean</code> validation result
     */
    private boolean validateJoinOperator() {

        boolean valid;

        if (parentOperator) {
            valid = super.validateOperator();
        } else {
            valid = parent.validateOperator();
            parentOperator = Boolean.TRUE;
        }

        return valid;
    }

    @Override
    public boolean validateOperator() {

        boolean valid;

        if (onClause) {
            valid = (onCount > CollectionUtils.EMPTY && super.validateOperator());
            onCount++;
        } else {
            valid = validateJoinOperator();
        }

        return valid;
    }

    @Override
    public String sql() {

        String value;

        StringBuilder joinQuery = new StringBuilder(sql);
        joinQuery.append(body);
        value = joinQuery.toString();

        return value;
    }

    @Override
    protected void appendToParent() {

        if (onClause) {
            String query = sql();
            parent.appendJoin(query);
        } else {
            super.appendToParent();
        }
    }
}
