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
 * @author levan
 * 
 */
public class LightmareInitialContextFactoryBuilder implements
	InitialContextFactoryBuilder {

    private static final String COULD_NOT_FIND_ERROR = "Could not find initial cotext";

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

    @Override
    public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> env)
	    throws NamingException {

	InitialContextFactory initialContextFactory;

	String requestedFactory = null;
	if (ObjectUtils.notNull(env)) {
	    requestedFactory = (String) env
		    .get(Context.INITIAL_CONTEXT_FACTORY);
	}
	if (ObjectUtils.notNull(requestedFactory)) {
	    initialContextFactory = simulateBuilderLessNamingManager(requestedFactory);
	} else {
	    initialContextFactory = new LightmareInitialContextFactory();
	}

	return initialContextFactory;
    }
}
