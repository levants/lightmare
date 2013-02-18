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
public class DSInitialContextFactory implements InitialContextFactory {

	private static final String SHARE_DATA_PROPERTY = "org.osjava.sj.jndi.shared";

	@SuppressWarnings("unchecked")
	@Override
	public Context getInitialContext(Hashtable<?, ?> properties)
			throws NamingException {

		// clone the environnement
		Hashtable<Object, Object> sharingEnv = (Hashtable<Object, Object>) properties
				.clone();

		// all instances will share stored data
		if (!sharingEnv.containsKey(SHARE_DATA_PROPERTY)) {
			sharingEnv.put(SHARE_DATA_PROPERTY, "true");
		}

		return new DSContext(sharingEnv);
	}

}
