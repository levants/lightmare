package org.lightmare.cache;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.lightmare.jndi.JndiManager;
import org.lightmare.jpa.JPAManager;
import org.lightmare.utils.NamingUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Container class to cache connections
 * 
 * @author levan
 * 
 */
public class ConnectionContainer {

    // Keeps unique EntityManagerFactories builded by unit names
    private static final ConcurrentMap<String, ConnectionSemaphore> CONNECTIONS = new ConcurrentHashMap<String, ConnectionSemaphore>();

    private static final Logger LOG = Logger
	    .getLogger(ConnectionContainer.class);

    public static boolean checkForEmf(String unitName) {

	boolean check = ObjectUtils.available(unitName);

	if (check) {
	    check = CONNECTIONS.containsKey(unitName);
	}

	return check;
    }

    public static ConnectionSemaphore getSemaphore(String unitName) {

	return CONNECTIONS.get(unitName);
    }

    private static ConnectionSemaphore createSemaphore(String unitName) {

	ConnectionSemaphore semaphore = CONNECTIONS.get(unitName);
	ConnectionSemaphore current = null;
	if (semaphore == null) {
	    semaphore = new ConnectionSemaphore();
	    semaphore.setUnitName(unitName);
	    semaphore.setInProgress(Boolean.TRUE);
	    semaphore.setCached(Boolean.TRUE);
	    current = CONNECTIONS.putIfAbsent(unitName, semaphore);
	}
	if (current == null) {
	    current = semaphore;
	}
	current.incrementUser();

	return current;
    }

    public static ConnectionSemaphore setSemaphore(String unitName,
	    String jndiName) {

	ConnectionSemaphore semaphore = null;

	if (ObjectUtils.available(unitName)) {

	    semaphore = createSemaphore(unitName);
	    if (ObjectUtils.available(jndiName)) {
		ConnectionSemaphore existent = CONNECTIONS.putIfAbsent(
			jndiName, semaphore);
		if (existent == null) {
		    semaphore.setJndiName(jndiName);
		}
	    }
	}

	return semaphore;
    }

    private static void awaitConnection(ConnectionSemaphore semaphore) {
	synchronized (semaphore) {
	    boolean inProgress = semaphore.isInProgress()
		    && !semaphore.isBound();
	    while (inProgress) {
		try {
		    semaphore.wait();
		    inProgress = semaphore.isInProgress()
			    && !semaphore.isBound();
		} catch (InterruptedException ex) {
		    inProgress = Boolean.FALSE;
		    LOG.error(ex.getMessage(), ex);
		}
	    }
	}

    }

    /**
     * Checks if {@link ConnectionSemaphore#isInProgress()} for appropriated
     * unit name
     * 
     * @param jndiName
     * @return <code>boolean</code>
     */
    public static boolean isInProgress(String jndiName) {

	ConnectionSemaphore semaphore = CONNECTIONS.get(jndiName);
	boolean inProgress = ObjectUtils.notNull(semaphore);
	if (inProgress) {
	    inProgress = semaphore.isInProgress() && !semaphore.isBound();
	    if (inProgress) {
		awaitConnection(semaphore);
	    }
	}
	return inProgress;
    }

    /**
     * Gets {@link ConnectionSemaphore} from cache, awaits if connection
     * instantiation is in progress
     * 
     * @param unitName
     * @return {@link ConnectionSemaphore}
     * @throws IOException
     */
    public static ConnectionSemaphore getConnection(String unitName)
	    throws IOException {

	ConnectionSemaphore semaphore = CONNECTIONS.get(unitName);
	if (ObjectUtils.notNull(semaphore)) {
	    awaitConnection(semaphore);
	}

	return semaphore;
    }

    /**
     * Gets {@link EntityManagerFactory} from {@link ConnectionSemaphore},
     * awaits if connection
     * 
     * @param unitName
     * @return {@link EntityManagerFactory}
     * @throws IOException
     */
    public static EntityManagerFactory getEntityManagerFactory(String unitName)
	    throws IOException {

	EntityManagerFactory emf = null;
	ConnectionSemaphore semaphore = CONNECTIONS.get(unitName);
	if (ObjectUtils.notNull(semaphore)) {
	    awaitConnection(semaphore);
	    emf = semaphore.getEmf();
	}

	return emf;
    }

    /**
     * Removes connection from {@link javax.naming.Context} cache
     * 
     * @param semaphore
     */
    private static void unbindConnection(ConnectionSemaphore semaphore) {

	String jndiName = semaphore.getJndiName();
	if (ObjectUtils.notNull(jndiName) && semaphore.isBound()) {
	    JndiManager namingUtils = new JndiManager();
	    try {
		Context context = namingUtils.getContext();
		String fullJndiName = NamingUtils.createJpaJndiName(jndiName);
		if (ObjectUtils.notNull(context.lookup(fullJndiName))) {
		    context.unbind(fullJndiName);
		}
	    } catch (NamingException ex) {
		LOG.error(String.format(
			"Could not unbind jndi name %s cause %s", jndiName,
			ex.getMessage()), ex);
	    } catch (IOException ex) {
		LOG.error(String.format(
			"Could not unbind jndi name %s cause %s", jndiName,
			ex.getMessage()), ex);
	    }
	}
    }

    /**
     * Closes all existing {@link EntityManagerFactory} instances kept in cache
     */
    public static void closeEntityManagerFactories() {
	Collection<ConnectionSemaphore> semaphores = CONNECTIONS.values();
	EntityManagerFactory emf;
	for (ConnectionSemaphore semaphore : semaphores) {
	    emf = semaphore.getEmf();
	    JPAManager.closeEntityManagerFactory(emf);
	}

	CONNECTIONS.clear();
    }

    /**
     * Closes connection ({@link EntityManagerFactory}) in passed
     * {@link ConnectionSemaphore}
     * 
     * @param semaphore
     */
    private static void closeConnection(ConnectionSemaphore semaphore) {
	int users = semaphore.decrementUser();
	if (users <= 0) {
	    EntityManagerFactory emf = semaphore.getEmf();
	    JPAManager.closeEntityManagerFactory(emf);
	    unbindConnection(semaphore);
	    CONNECTIONS.remove(semaphore.getUnitName());
	    String jndiName = semaphore.getJndiName();
	    if (ObjectUtils.available(jndiName)) {
		CONNECTIONS.remove(jndiName);
		semaphore.setBound(Boolean.FALSE);
		semaphore.setCached(Boolean.FALSE);
	    }
	}
    }

    /**
     * Removes {@link ConnectionSemaphore} from cache and unbinds name from
     * {@link javax.naming.Context}
     * 
     * @param unitName
     */
    public static void removeConnection(String unitName) {

	ConnectionSemaphore semaphore = CONNECTIONS.get(unitName);
	if (ObjectUtils.notNull(semaphore)) {
	    awaitConnection(semaphore);
	    unbindConnection(semaphore);
	    closeConnection(semaphore);
	}
    }

    public static ConnectionSemaphore get(String unitName) {

	return CONNECTIONS.get(unitName);
    }
}
