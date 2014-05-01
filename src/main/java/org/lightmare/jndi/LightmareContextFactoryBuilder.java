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

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.reflect.MetaUtils;

/**
 * Implementation of {@link InitialContextFactoryBuilder} factory builder class
 * for instantiate {@link InitialContextFactory} implementation
 * 
 * @author Levan Tsinadze
 * @since 0.0.60-SNAPSHOT
 */
public class LightmareContextFactoryBuilder implements
	InitialContextFactoryBuilder {

    // Error message
    private static final String COULD_NOT_FIND_ERROR = "Could not find initial cotext";

    /**
     * Builds {@link InitialContextFactory} from passed requested (
     * {@link String}) class name
     * 
     * @param requestedFactory
     * @return {@link InitialContextFactory}
     * @throws NoInitialContextException
     */
    private InitialContextFactory simulateBuilderLessNamingManager(
	    String requestedFactory) throws NoInitialContextException {

	InitialContextFactory factory;

	Class<?> requestedClass;
	try {
	    requestedClass = MetaUtils.initClassForName(requestedFactory);
	    Object instance = MetaUtils.instantiate(requestedClass);
	    factory = ObjectUtils.cast(instance, InitialContextFactory.class);
	} catch (IOException ex) {
	    NoInitialContextException nex = new NoInitialContextException(
		    COULD_NOT_FIND_ERROR);
	    nex.setRootCause(ex);
	    throw nex;
	}

	return factory;
    }

    /**
     * Initializes {@link InitialContextFactory} implementation for passed
     * parameters
     */
    @Override
    public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> env)
	    throws NamingException {

	InitialContextFactory initialContextFactory;

	String requestedFactory;
	if (ObjectUtils.notNull(env)) {
	    Object factory = env.get(Context.INITIAL_CONTEXT_FACTORY);
	    requestedFactory = ObjectUtils.cast(factory, String.class);
	} else {
	    requestedFactory = null;
	}

	if (ObjectUtils.notNull(requestedFactory)) {
	    initialContextFactory = simulateBuilderLessNamingManager(requestedFactory);
	} else {
	    initialContextFactory = new LightmareContextFactory();
	}

	return initialContextFactory;
    }
}
