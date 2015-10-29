package org.lightmare.linq.links;

/**
 * Query operators
 * 
 * @author Levan Tsinadze
 *
 */
public interface Operators {

    String EQ = " = ";

    String NOT_EQ = " != ";

    String MORE = " > ";

    String LESS = " < ";

    String MORE_OR_EQ = " >= ";

    String LESS_OR_EQ = " <= ";

    String LIKE = " like ";

    String IS_NULL = " is null ";

    String NOT_NULL = " is not null ";

    String IN = " in ";

    String EXISTS = " exists ";

    String OPEN_BRACKET = " (";

    String CLOSE_BRACKET = ") ";
}
