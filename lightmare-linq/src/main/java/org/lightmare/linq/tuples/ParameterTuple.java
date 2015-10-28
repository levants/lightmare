package org.lightmare.linq.tuples;

public class ParameterTuple<P> {

	private final String name;

	private final P value;

	public ParameterTuple(String name, P value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public P getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.format("%s : %s", name, value);
	}
}
