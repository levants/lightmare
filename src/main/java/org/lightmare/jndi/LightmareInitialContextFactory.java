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
 * @author Levan Tsinadze
 * @since 0.0.60-SNAPSHOT
 */
public class LightmareInitialContextFactory implements InitialContextFactory {

    /**
     * Puts if absent shared parameter to JNDI properties before initialization
     * 
     * @param sharingEnv
     * @param parameter
     */
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

	Context lightmareContext;

	// clone the environment
	Hashtable<Object, Object> sharingEnv = ObjectUtils.cast(properties
		.clone());
	putToEnv(sharingEnv, JNDIParameters.SHARED_PARAMETER);
	lightmareContext = new LightmareContext(sharingEnv);

	return lightmareContext;
    }
}
