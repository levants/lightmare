package org.lightmare.jndi;

import java.util.Hashtable;

import javax.naming.NamingException;

import org.lightmare.jpa.JPAManager;
import org.osjava.sj.memory.MemoryContext;

public class DSContext extends MemoryContext {

	public DSContext(Hashtable<?, ?> env) {
		super(env);
	}

	@Override
	public Object lookup(String name) throws NamingException {

		JPAManager.isInProgress(name);

		return super.lookup(name);
	}

	@Override
	public void close() throws NamingException {
		// super.close();
	}
}
