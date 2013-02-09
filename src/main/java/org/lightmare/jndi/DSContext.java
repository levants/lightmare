package org.lightmare.jndi;

import java.util.Hashtable;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;

import org.lightmare.jpa.JPAManager;
import org.osjava.sj.memory.MemoryContext;

public class DSContext extends MemoryContext {

	public DSContext(Hashtable<?, ?> env) {
		super(env);
	}

	@Override
	public Object lookup(String name) throws NamingException {

		// Checks if connection is in progress and waits for finish
		JPAManager.isInProgress(name);

		// Gets EntityManagerFactory from parent
		EntityManagerFactory emf = (EntityManagerFactory) super.lookup(name);

		return emf.createEntityManager();
	}

	@Override
	public void close() throws NamingException {
		// super.close();
	}
}
