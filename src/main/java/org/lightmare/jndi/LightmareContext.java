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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import javax.enterprise.context.spi.Context;
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
import org.lightmare.utils.finalizers.Cleanable;
import org.lightmare.utils.finalizers.FinalizationUtils;
import org.osjava.sj.jndi.AbstractContext;
import org.osjava.sj.memory.MemoryContext;

/**
 * Implementation of JNDI {@link Context} and extension of simple JNDI's
 * {@link MemoryContext} and {@link AbstractContext} for EJB bean interface,
 * {@link UserTransaction} and {@link EntityManager} caching and retrieving
 * 
 * @author Levan Tsinadze
 * @since 0.0.60-SNAPSHOT
 */
public class LightmareContext extends MemoryContext implements Cleanable {

    // Caches EntityManager instances got from lookup method to clear after
    private Collection<WeakReference<EntityManager>> ems = new ArrayList<WeakReference<EntityManager>>();

    /**
     * Constructor with no parameters just calls superclass appropriated
     * constructor
     */
    public LightmareContext() {
	super();
    }

    /**
     * Constructor with {@link Hashtable} to cache lookups in memory
     * 
     * @param env
     */
    public LightmareContext(Hashtable<?, ?> env) {
	super(env);
	FinalizationUtils.add(this);
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

    /**
     * Searches JPA objects by JNDI name
     * 
     * @param jndiName
     * @return {@link Object}
     * @throws NamingException
     */
    private Object lookupJpa(String jndiName) throws NamingException {

	Object value;

	// Checks if it is request for entity manager
	String name = NamingUtils.formatJpaJndiName(jndiName);
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

	return value;
    }

    /**
     * Initializes EJB objects by JNDI name
     * 
     * @param jndiName
     * @return {@link Object}
     * @throws NamingException
     */
    private Object lookupEjb(String jndiName) throws NamingException {

	Object value;

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

	return value;
    }

    @Override
    public Object lookup(String jndiName) throws NamingException {

	Object value;

	// Retrieves JTA UserTransaction object from thread cache
	if (jndiName.equals(NamingUtils.USER_TRANSACTION_NAME)) {
	    UserTransaction transaction = TransactionHolder.getTransaction();
	    value = transaction;
	} else if (jndiName.startsWith(NamingUtils.JPA_NAME_PREF)) {
	    value = lookupJpa(jndiName);
	    // Retrieves EJB bean object from lookup
	} else if (jndiName.startsWith(NamingUtils.EJB_NAME_PREF)) {
	    value = lookupEjb(jndiName);
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

	// Closes existing EntityManagers from cache
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

    /**
     * Clears cached resources and calls {@link MemoryContext#close()} method
     * 
     * @see AbstractContext#close()
     */
    @Override
    public void close() throws NamingException {
	clearResources();
	// TODO: Must check if needed super.close() method call
	// super.close();
    }

    /**
     * Clears cached resources
     */
    @Override
    public void clean() throws IOException {
	clearResources();
    }
}
