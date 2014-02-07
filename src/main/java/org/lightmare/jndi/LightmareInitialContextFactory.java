/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
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
