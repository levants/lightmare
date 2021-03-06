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

import java.util.Objects;

import org.lightmare.criteria.query.orm.links.Aggregates;
import org.lightmare.criteria.utils.ObjectUtils;

/**
 * Tuple of aggregate functions for GROUP BY and HAVING clauses
 * 
 * @author Levan Tsinadze
 *
 */
public class AggregateTuple {

    private final String fieldName;

    private final Aggregates aggregate;

    private final String alias;

    private AggregateTuple(final QueryTuple tuple, final Aggregates aggregate, final String alias) {
        this.fieldName = tuple.getFieldName();
        this.aggregate = aggregate;
        this.alias = alias;
    }

    public static AggregateTuple of(final QueryTuple tuple, final Aggregates aggregate, final String alias) {
        return new AggregateTuple(tuple, aggregate, alias);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Aggregates getAggregate() {
        return aggregate;
    }

    public String getAlias() {
        return alias;
    }

    public String expression() {
        return aggregate.expression(fieldName, alias);
    }

    /**
     * Compares other {@link org.lightmare.criteria.tuples.AggregateTuple}
     * fields to current instance
     * 
     * @param other
     * @return <code>boolean</code> validation result
     */
    private boolean compareFields(AggregateTuple other) {
        return (this.fieldName.equals(other.fieldName) && this.aggregate.key.equals(other.aggregate.key));
    }

    @Override
    public boolean equals(Object raw) {

        boolean valid = (Objects.nonNull(raw) && (raw instanceof AggregateTuple));

        if (valid) {
            AggregateTuple other = ObjectUtils.cast(raw);
            valid = (super.equals(raw) || (compareFields(other)));
        }

        return valid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fieldName, this.aggregate.key);
    }
}
