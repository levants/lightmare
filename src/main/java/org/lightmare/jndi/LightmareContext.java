package org.lightmare.jndi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.TransactionContainer;
import org.lightmare.ejb.EjbConnector;
import org.lightmare.jpa.JpaManager;
import org.lightmare.utils.NamingUtils;
import org.lightmare.utils.ObjectUtils;
import org.osjava.sj.memory.MemoryContext;

/**
 * Extensions of simple jndi {@link MemoryContext} for {@link EntityManager}
 * retrieving
 * 
 * @author levan
 * 
 */
public class LightmareContext extends MemoryContext {

    // Caches EntityManager instances got from lookup method to clear after
    private Collection<EntityManager> ems = new ArrayList<EntityManager>();

    public LightmareContext(Hashtable<?, ?> env) {
	super(env);
    }

    private void cacheResource(Object resource) {

	if (ObjectUtils.notNull(resource) && resource instanceof EntityManager) {

	    EntityManager em = ObjectUtils.cast(resource, EntityManager.class);
	    ems.add(em);
	}
    }

    @Override
    public Object lookup(String jndiName) throws NamingException {

	Object value;

	String name;
	if (jndiName.equals(NamingUtils.USER_TRANSACTION_NAME)) {

	    UserTransaction transaction = TransactionContainer.getTransaction();
	    value = transaction;

	} else if (jndiName.startsWith(NamingUtils.JPA_NAME_PREF)) {
	    // Checks if it is request for entity manager
	    name = NamingUtils.formatJpaJndiName(jndiName);

	    // Checks if connection is in progress and waits for finish
	    ConnectionContainer.isInProgress(name);

	    // Gets EntityManagerFactory from parent
	    Object candidate = super.lookup(jndiName);
	    if (candidate == null) {
		value = candidate;
	    } else if (candidate instanceof EntityManagerFactory) {
		EntityManagerFactory emf = ObjectUtils.cast(candidate,
			EntityManagerFactory.class);
		EntityManager em = emf.createEntityManager();
		value = em;
	    } else {
		value = candidate;
	    }
	} else if (jndiName.startsWith(NamingUtils.EJB_NAME_PREF)) {

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

	// Saves value to clear after close method is called
	cacheResource(value);

	return value;
    }

    /**
     * Clears and closes all cached resources
     */
    private void clearResources() {

	if (ObjectUtils.available(ems)) {
	    for (EntityManager em : ems) {
		JpaManager.closeEntityManager(em);
	    }
	}
    }

    @Override
    public void close() throws NamingException {
	clearResources();
	// super.close();
    }
}
