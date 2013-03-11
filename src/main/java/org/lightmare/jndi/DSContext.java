package org.lightmare.jndi;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.lightmare.ejb.EjbConnector;
import org.lightmare.jpa.JPAManager;
import org.osjava.sj.memory.MemoryContext;

/**
 * Extensions of simple jndi {@link MemoryContext} for {@link EntityManager}
 * retrieving
 * 
 * @author levan
 * 
 */
public class DSContext extends MemoryContext {

    public DSContext(Hashtable<?, ?> env) {
	super(env);
    }

    @Override
    public Object lookup(String jndiName) throws NamingException {

	Object value;
	String name;
	// Checks if it is request for entity manager
	if (jndiName.startsWith("java:comp/env/")) {

	    name = NamingUtils.formatJpaJndiName(jndiName);

	    // Checks if connection is in progress and waits for finish
	    JPAManager.isInProgress(name);

	    // Gets EntityManagerFactory from parent
	    Object candidate = super.lookup(jndiName);
	    if (candidate == null) {
		value = candidate;
	    } else if (candidate instanceof EntityManagerFactory) {
		EntityManagerFactory emf = (EntityManagerFactory) (candidate);
		value = emf.createEntityManager();
	    } else {
		value = candidate;
	    }
	} else if (jndiName.startsWith("ejb:")) {

	    NamingUtils.BeanDescriptor descriptor = NamingUtils
		    .parseEjbJndiName(jndiName);
	    EjbConnector ejbConnection = new EjbConnector();
	    try {
		String beanName = descriptor.getBeanName();
		String interfaceName = descriptor.getInterfaceName();
		value = ejbConnection.connectToBean(beanName, interfaceName);
	    } catch (IOException ex) {
		throw new NamingException(ex.getMessage());
	    }

	} else {
	    value = super.lookup(jndiName);
	}

	return value;
    }

    @Override
    public void close() throws NamingException {
	// super.close();
    }
}
