package org.lightmare.linq.tuples;

import javax.persistence.TemporalType;

/**
 * Query parameter name and value container class
 * 
 * @author Levan Tsinadze
 *
 * @param
 * 	   <P>
 */
public class ParameterTuple<P> {

    private final String name;

    private final P value;

    private final TemporalType temporalType;

    public ParameterTuple(final String name, final P value, final TemporalType temporalType) {
	this.name = name;
	this.value = value;
	this.temporalType = temporalType;
    }

    public ParameterTuple(final String name, final P value) {
	this(name, value, null);
    }

    public String getName() {
	return name;
    }

    public P getValue() {
	return value;
    }

    public TemporalType getTemporalType() {
	return temporalType;
    }

    @Override
    public String toString() {
	return String.format("%s : %s", name, value);
    }
}
