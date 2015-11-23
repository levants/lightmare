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
     * Validates if body needs boolean operator before clause
     * 
     * @param operators
     * @return <code>boolean</code> validation result
     */
    private boolean validForOperator(String... operators) {
        return (StringUtils.valid(having) && StringUtils.notEndsWithAll(having, operators));
    }

    /**
     * Validates query body to be append by logical operators
     * 
     * @return <code>boolean</code> validation result
     */
    public boolean validateOperator() {
        return validForOperator(Clauses.AND, Clauses.OR, Clauses.HAVING, Operators.OPEN_BRACKET);
    }

    /**
     * Appends default boolean operator to passed buffer
     */
    protected void appendOperator() {

        if (validateOperator()) {
            and();
        }
    }

    private void newLine() {

        if (StringUtils.notEndsWith(having, StringUtils.LINE)) {
            appendHaving(StringUtils.LINE);
        }
    }

    private void startHaving() {

        if (StringUtils.isEmpty(having)) {
            appendHaving(StringUtils.LINE);
            appendHaving(Clauses.HAVING);
        } else {
            appendOperator();
        }
    }

    @Override
    public <N extends Number> HavingExpression<T> operate(String operator, N value) {

        startHaving();
        appendHaving(aggregateTuple.expression());
        appendHaving(operator).appendHaving(value);
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
