package org.lightmare.criteria.query.internal.layers;

import org.lightmare.criteria.query.internal.orm.links.Operators;

/**
 * Implementation for
 * {@link org.lightmare.criteria.query.internal.layers.LayerExpressions} for
 * JPA and JDBC layers
 * 
 * @author Levan Tsinadze
 *
 */
abstract class JpaLayerExpressions implements LayerExpressions {

    @Override
    public String and() {
        return Operators.AND;
    }

    @Override
    public String or() {
        return Operators.OR;
    }

    @Override
    public String equal() {
        return Operators.EQ;
    }

    @Override
    public String notEqual() {
        return Operators.NOT_EQ;
    }

    @Override
    public String lessThen() {
        return Operators.LESS;
    }

    @Override
    public String lessThenOrEqual() {
        return Operators.LESS_OR_EQ;
    }

    @Override
    public String greaterThen() {
        return Operators.GREATER;
    }

    @Override
    public String greaterThenOrEqual() {
        return Operators.GREATER_OR_EQ;
    }

    @Override
    public String like() {
        return Operators.LIKE;
    }

    @Override
    public String notLike() {
        return Operators.NOT_LIKE;
    }

    @Override
    public String in() {
        return Operators.IN;
    }

    @Override
    public String notIn() {
        return Operators.NOT_IN;
    }

    @Override
    public String isNull() {
        return Operators.IS_NULL;
    }

    @Override
    public String isNotNull() {
        return Operators.NOT_NULL;
    }

}
