package org.lightmare.criteria.query;

/**
 * Container class with query alias and appropriated increment coefficient for
 * sub query alias generation
 * 
 * @author Levan Tsinadze
 *
 */
class AliasTuple {

    final String alias;

    final int counter;

    public AliasTuple(final String alias, final int counter) {
	this.alias = alias;
	this.counter = counter;
    }

    public String getAlias() {
	return alias;
    }

    public int getCounter() {
	return counter;
    }

    public String generate() {
	return alias.concat(String.valueOf(counter));
    }
}
