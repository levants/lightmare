package org.lightmare.criteria.query.internal.jpa.builders;

import java.util.Objects;

import org.lightmare.criteria.functions.HavingConsumer;
import org.lightmare.criteria.query.internal.jpa.HavingExpression;
import org.lightmare.criteria.query.internal.jpa.links.Clauses;
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

    private void newLine() {

        if (StringUtils.notEndsWith(having, StringUtils.NEWLINE)) {
            appendHaving(StringUtils.NEWLINE);
        }
    }

    private void startHaving() {

        if (StringUtils.isEmpty(having)) {
            appendHaving(StringUtils.NEWLINE);
            appendHaving(Clauses.HAVING);
        }
    }

    @Override
    public HavingExpression<T> appendHaving(Object operator) {
        having.append(operator);
        return this;
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
