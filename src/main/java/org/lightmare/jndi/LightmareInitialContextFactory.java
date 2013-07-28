package org.lightmare.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * Extension of factory class {@link InitialContextFactory}
 * 
 * @author levan
 * 
 */
public class LightmareInitialContextFactory implements InitialContextFactory {

    private static final String SHARE_DATA_PROPERTY = "org.osjava.sj.jndi.shared";

    private static final String SHARE_DATA_PROPERTY_VALUE = Boolean.TRUE
	    .toString();

    @SuppressWarnings("unchecked")
    @Override
    public Context getInitialContext(Hashtable<?, ?> properties)
	    throws NamingException {

	// clone the environnement
	Hashtable<Object, Object> sharingEnv = (Hashtable<Object, Object>) properties
		.clone();

	// all instances will share stored data
	boolean notContainsKey = !sharingEnv.containsKey(SHARE_DATA_PROPERTY);
	if (notContainsKey) {
	    sharingEnv.put(SHARE_DATA_PROPERTY, SHARE_DATA_PROPERTY_VALUE);
	}
	Context lightmareContext = new LightmareContext(sharingEnv);

	return lightmareContext;
    }

}
