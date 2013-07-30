package org.lightmare.jndi;

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
 * 
 * @author levan
 * 
 */
public class LightmareInitialContextFactoryBuilder implements
	InitialContextFactoryBuilder {

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

    private InitialContextFactory simulateBuilderLessNamingManager(
	    String requestedFactory) throws NoInitialContextException {
	try {
	    Class<?> requestedClass = MetaUtils
		    .initClassForName(requestedFactory);

	    return (InitialContextFactory) requestedClass.newInstance();
	} catch (Exception ex) {
	    NoInitialContextException ne = new NoInitialContextException(
		    "Could not find initial cotext");
	    ne.setRootCause(ex);
	    throw ne;
	}
    }
}
