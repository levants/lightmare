package org.lightmare.criteria.tuples;

import org.lightmare.criteria.utils.StringUtils;

/**
 * Extension of {@link QueryTuple} for embedded entity fields resolving
 * 
 * @author Levan Tsinadze
 *
 */
public class EmbeddedTuple extends QueryTuple {

    private static final long serialVersionUID = 1L;

    private final String embeddedName;

    public EmbeddedTuple(final QueryTuple tuple, final String embeddedName) {
	super(tuple.getEntityName(), tuple.getMethodName(), tuple.getArguments(), tuple.getFieldName());
	setAlias(tuple.getAlias());
	this.embeddedName = embeddedName;
    }

    @Override
    public String getFieldName() {
	return StringUtils.concat(embeddedName, StringUtils.DOT, super.getFieldName());
    }
}
