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
 * Extension of factory builder class {@link InitialContextFactoryBuilder}
 * 
 * @author levan
 * 
 */
public class DSInitialContextFactoryBuilder implements
	InitialContextFactoryBuilder {

    @Override
    public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> env)
	    throws NamingException {
	String requestedFactory = null;
	if (ObjectUtils.notNull(env)) {
	    requestedFactory = (String) env
		    .get(Context.INITIAL_CONTEXT_FACTORY);
	}
	if (ObjectUtils.notNull(requestedFactory)) {
	    return simulateBuilderlessNamingManager(requestedFactory);
	}
	return new DSInitialContextFactory();

    }

    private InitialContextFactory simulateBuilderlessNamingManager(
	    String requestedFactory) throws NoInitialContextException {
	try {
	    Class<?> requestedClass = MetaUtils
		    .initClassForName(requestedFactory);

	    return (InitialContextFactory) requestedClass.newInstance();
	} catch (Exception ex) {
	    NoInitialContextException ne = new NoInitialContextException(
		    "there is not initial cotext");
	    ne.setRootCause(ex);
	    throw ne;
	}
    }

}
