package org.lightmare.criteria.query.internal.jpa.builders;

import java.util.Objects;

import org.lightmare.criteria.functions.HavingConsumer;
import org.lightmare.criteria.query.internal.jpa.HavingExpression;
import org.lightmare.criteria.query.internal.jpa.links.Clauses;
import org.lightmare.criteria.query.internal.jpa.links.Operators;
import org.lightmare.criteria.tuples.AggregateTuple;
import org.lightmare.criteria.utils.StringUtils;

/**
 * Processes HAVING clause
 * 
 * @author Levan Tsinadze
 *
 * @param <T>
 *            entity type parameter
 */
class HavingProcessor<T> implements HavingExpression<T> {

    private final StringBuilder having;

    private final AggregateTuple aggregateTuple;

    protected HavingProcessor(final StringBuilder having, final AggregateTuple aggregateTuple) {
        this.having = having;
        this.aggregateTuple = aggregateTuple;
    }

    @Override
    public HavingExpression<T> appendHaving(Object operator) {
        having.append(operator);
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
     * Appends default boolean operator to passed buffer
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
    public HavingExpression<T> operate(String operator, Number value) {

        operateHaving(operator, value);
        newLine();

        return this;
    }

    @Override
    public HavingExpression<T> operate(String operator, Number value1, Number value2) {

        operateHaving(operator, value1);
        appendHaving(StringUtils.SPACE).appendHaving(Operators.AND).appendHaving(value2);
        newLine();

        return this;
    }

    @Override
    public HavingExpression<T> brackets(HavingConsumer<T> consumer) {

        if (Objects.nonNull(consumer)) {
            openBracket();
            consumer.accept(this);
            closeBracket();
        }

        return this;
    }
}
