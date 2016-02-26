package org.lightmare.criteria.query.internal.connectors;

/**
 * Query expressions for data base layer
 * 
 * @author Levan Tsinadze
 *
 */
interface LayerExpressions {

    // =================== Logical =================//

    String and();

    String or();

    // =============================================//

    String equal();

    String notEqual();

    String lessThen();

    String lessThenOrEqual();

    String greaterThen();

    String greaterThenOrEqual();

    String like();

    String notLike();

    String in();

    String notIn();

    String isNull();

    String isNotNull();
}
