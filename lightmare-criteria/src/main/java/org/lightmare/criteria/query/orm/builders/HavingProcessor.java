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
package org.lightmare.criteria.query.orm.builders;

import org.lightmare.criteria.query.orm.HavingExpression;
import org.lightmare.criteria.query.orm.links.Clauses;
import org.lightmare.criteria.query.orm.links.Operators;
import org.lightmare.criteria.tuples.AggregateTuple;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Processes HAVING clause
 * 
 * @author Levan Tsinadze
 *
 */
class HavingProcessor implements HavingExpression {

    private final StringBuilder having;

    private final AggregateTuple aggregateTuple;

    protected HavingProcessor(final StringBuilder having, final AggregateTuple aggregateTuple) {
        this.having = having;
        this.aggregateTuple = aggregateTuple;
    }

    @Override
    public HavingExpression appendHaving(Object operator) {
        having.append(operator);
        return this;
    }

    @Override
    public HavingExpression replaceNewLine(char supposed) {
        StringUtils.replaceOrAppend(having, StringUtils.LINE, supposed);
        return this;
    }

    /**
     * Validates query body to be append by logical operators
     * 
     * @return <code>boolean</code> validation result
     */
    public boolean validateOperator() {
        return StringUtils.notEndsWithAll(having, Clauses.VALIDS);
    }

    /**
     * Appends default boolean operator AND to HAVING if there is no other valid
     * operators
     */
    protected void appendOperator() {

        if (validateOperator()) {
            and();
        }
    }

    /**
     * Appends HAVING clause query part with new line character
     */
    private void newLine() {

        if (StringUtils.notEndsWith(having, StringUtils.LINE)) {
            appendHaving(StringUtils.LINE);
        }
    }

    /**
     * Validates and starts HAVINC clause query part
     */
    private void startHaving() {

        if (StringUtils.isEmpty(having)) {
            appendHaving(StringUtils.LINE);
            appendHaving(Clauses.HAVING);
        } else {
            appendOperator();
        }
    }

    /**
     * Generates operator for HAVING clause
     * 
     * @param operator
     * @param value
     */
    private <N extends Number> void operateHaving(String operator, N value) {

        startHaving();
        appendHaving(aggregateTuple.expression());
        appendHaving(operator).appendHaving(value);
    }

    @Override
    public HavingExpression operate(String operator, Number value) {

        operateHaving(operator, value);
        newLine();

        return this;
    }

    @Override
    public HavingExpression operate(String operator, Number value1, Number value2) {

        operateHaving(operator, value1);
        appendHaving(StringUtils.SPACE).appendHaving(Operators.AND).appendHaving(value2);
        newLine();

        return this;
    }
}
