package org.lightmare.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.lightmare.jndi.JndiManager.JNDIParameters;
import org.lightmare.utils.ObjectUtils;

/**
 * Implementation of {@link InitialContextFactory} factory class to register and
 * instantiate JNDI {@link Context} instance
 * 
 * @author levan
 * 
 */
public class LightmareInitialContextFactory implements InitialContextFactory {

    private void putToEnv(Hashtable<Object, Object> sharingEnv,
	    JNDIParameters parameter) {

	String key = parameter.key;
	String value = parameter.value;

	// all instances will share stored data
	boolean notContainsKey = ObjectUtils.notTrue(sharingEnv
		.containsKey(key));

	if (notContainsKey) {
	    sharingEnv.put(key, value);
	}
    }

    @Override
    public Context getInitialContext(Hashtable<?, ?> properties)
	    throws NamingException {

	// clone the environment
	Hashtable<Object, Object> sharingEnv = ObjectUtils.cast(properties
		.clone());

	putToEnv(sharingEnv, JNDIParameters.SHARED_PARAMETER);

	Context lightmareContext = new LightmareContext(sharingEnv);

	return lightmareContext;
    }
}
