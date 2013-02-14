package org.lightmare.jndi;

import java.util.Hashtable;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.lightmare.jpa.JPAManager;
import org.osjava.sj.memory.MemoryContext;

/**
 * Extentions of simple jndi {@link MemoryContext} for {@link EntityManager}
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
	public Object lookup(String name) throws NamingException {

		Object value;
		// Checks if it is request for entity manager
		if (name.startsWith("java:comp/env/")) {

			// Checks if connection is in progress and waits for finish
			JPAManager.isInProgress(name);

			// Gets EntityManagerFactory from parent
			Object candidate = super.lookup(name);
			if (candidate == null) {
				value = candidate;
			} else if (candidate instanceof EntityManagerFactory) {
				EntityManagerFactory emf = (EntityManagerFactory) (candidate);
				value = emf.createEntityManager();
			} else {
				value = candidate;
			}
		} else {
			value = super.lookup(name);
		}

		return value;
	}

	@Override
	public void close() throws NamingException {
		// super.close();
	}
}
