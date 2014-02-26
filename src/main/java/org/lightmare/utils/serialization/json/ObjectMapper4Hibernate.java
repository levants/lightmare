package org.lightmare.utils.serialization.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * Extension of {@link ObjectMapper} with JPA / Hibernate relationship lazy
 * initialization utilities
 * 
 * @author Levan Tsinadze
 * @since 0.1.1
 * 
 */
public class ObjectMapper4Hibernate extends ObjectMapper {

    private static final long serialVersionUID = 1L;

    public ObjectMapper4Hibernate() {
	super();
	registerModule(new Hibernate4Module());
    }
}
