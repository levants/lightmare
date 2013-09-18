package org.lightmare.jndi;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.TransactionContainer;
import org.lightmare.ejb.EjbConnector;
import org.lightmare.utils.NamingUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;
import org.osjava.sj.memory.MemoryContext;

/**
 * Extensions of simple jndi {@link MemoryContext} for {@link EntityManager}
 * retrieving
 * 
 * @author levan
 * 
 */
public class LightmareContext extends MemoryContext {

    protected static class EMCloser implements Runnable {

	private EntityManager em;

	public EMCloser(EntityManager em) {
	    this.em = em;
	}

	private void close() {
	    if (ObjectUtils.notNull(em) && em.isOpen()) {
		em.close();
	    }
	}

	@Override
	public void run() {

	    try {
		Thread.currentThread().join();
	    } catch (InterruptedException ex) {
		ex.printStackTrace();
	    } finally {
		close();
	    }
	}

    }

    public LightmareContext(Hashtable<?, ?> env) {
	super(env);
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
		Thread thread = new Thread(new EMCloser(em));
		thread.setName(StringUtils.concat(thread.getId(),
			StringUtils.HYPHEN, "em-closer-thread"));
		thread.start();
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

	return value;
    }

    @Override
    public void close() throws NamingException {
	// super.close();
    }
}
