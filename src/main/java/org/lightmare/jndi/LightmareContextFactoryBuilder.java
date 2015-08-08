/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
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
import org.lightmare.utils.reflect.ClassUtils;

/**
 * Implementation of {@link InitialContextFactoryBuilder} factory builder class
 * for instantiate {@link InitialContextFactory} implementation
 *
 * @author Levan Tsinadze
 * @since 0.0.60
 */
public class LightmareContextFactoryBuilder
	implements InitialContextFactoryBuilder {

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
	    requestedClass = ClassUtils.initClassForName(requestedFactory);
	    Object instance = ClassUtils.instantiate(requestedClass);
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
     * Gets requested factory class name
     *
     * @param env
     * @return {@link String} factory class name
     */
    private String getRequestedFactory(Hashtable<?, ?> env) {

	String requestedFactory;

	if (env == null) {
	    requestedFactory = null;
	} else {
	    Object factory = env.get(Context.INITIAL_CONTEXT_FACTORY);
	    requestedFactory = ObjectUtils.cast(factory, String.class);
	}

	return requestedFactory;
    }

    /**
     * Initializes {@link InitialContextFactory} from passed class name
     *
     * @param requestedFactory
     * @return {@link InitialContextFactory} instance from factory class name
     * @throws NoInitialContextException
     */
    private InitialContextFactory instantiateFromName(String requestedFactory)
	    throws NoInitialContextException {

	InitialContextFactory initialContextFactory;

	if (requestedFactory == null) {
	    initialContextFactory = new LightmareContextFactory();
	} else {
	    initialContextFactory = simulateBuilderLessNamingManager(
		    requestedFactory);
	}

	return initialContextFactory;
    }

    /**
     * Initializes {@link InitialContextFactory} implementation for passed
     * parameters
     */
    @Override
    public InitialContextFactory createInitialContextFactory(
	    Hashtable<?, ?> env) throws NamingException {

	InitialContextFactory initialContextFactory;

	String requestedFactory = getRequestedFactory(env);
	initialContextFactory = instantiateFromName(requestedFactory);

	return initialContextFactory;
    }
}
