package org.lightmare.jndi;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.TransactionHolder;
import org.lightmare.ejb.EjbConnector;
import org.lightmare.jpa.JpaManager;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.NamingUtils;
import org.lightmare.utils.ObjectUtils;
import org.osjava.sj.memory.MemoryContext;

/**
 * Implementation of simple JNDI {@link MemoryContext} for EJB bean interface,
 * {@link UserTransaction} and {@link EntityManager} caching and retrieving
 * 
 * @author levan
 * @since 0.0.60-SNAPSHOT
 */
public class LightmareContext extends MemoryContext {

    // Caches EntityManager instances got from lookup method to clear after
    private Collection<WeakReference<EntityManager>> ems = new ArrayList<WeakReference<EntityManager>>();

    public LightmareContext(Hashtable<?, ?> env) {
	super(env);
    }

    /**
     * Caches resources to close them after {@link LightmareContext#close()}
     * method is called
     * 
     * @param resource
     */
    private void cacheResource(Object resource) {

	if (ObjectUtils.notNull(resource) && resource instanceof EntityManager) {
	    EntityManager em = ObjectUtils.cast(resource, EntityManager.class);
	    WeakReference<EntityManager> ref = new WeakReference<EntityManager>(
		    em);
	    ems.add(ref);
	}
    }

    @Override
    public Object lookup(String jndiName) throws NamingException {

	Object value;

	String name;
	if (jndiName.equals(NamingUtils.USER_TRANSACTION_NAME)) {
	    UserTransaction transaction = TransactionHolder.getTransaction();
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

	if (CollectionUtils.valid(ems)) {
	    try {
		EntityManager em;
		for (WeakReference<EntityManager> ref : ems) {
		    em = ref.get();
		    JpaManager.closeEntityManager(em);
		}
	    } finally {
		ems.clear();
	    }
	}
    }

    @Override
    public void close() throws NamingException {

	clearResources();
	// TODO: Must check is needed super.close() method call
	// super.close();
    }
}
