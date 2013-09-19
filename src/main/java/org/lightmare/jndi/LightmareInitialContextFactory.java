package org.lightmare.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.lightmare.utils.ObjectUtils;

/**
 * Implementation of {@link InitialContextFactory} factory class to register and
 * instantiate JNDI {@link Context} instance
 * 
 * @author levan
 * 
 */
public class LightmareInitialContextFactory implements InitialContextFactory {

    private static final String SHARE_DATA_PROPERTY = "org.osjava.sj.jndi.shared";

    private static final String SHARE_DATA_PROPERTY_VALUE = Boolean.TRUE
	    .toString();

    @Override
    public Context getInitialContext(Hashtable<?, ?> properties)
	    throws NamingException {

	// clone the environment
	Hashtable<Object, Object> sharingEnv = ObjectUtils.cast(properties
		.clone());

	// all instances will share stored data
	boolean notContainsKey = !sharingEnv.containsKey(SHARE_DATA_PROPERTY);
	if (notContainsKey) {
	    sharingEnv.put(SHARE_DATA_PROPERTY, SHARE_DATA_PROPERTY_VALUE);
	}

	Context lightmareContext = new LightmareContext(sharingEnv);

	return lightmareContext;
    }
}
