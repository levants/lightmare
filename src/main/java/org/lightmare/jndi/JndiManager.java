package org.lightmare.jndi;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.lightmare.utils.ObjectUtils;

/**
 * Utility class to initialize and set (
 * {@link System#setProperty(String, String)}) the {@link InitialContextFactory}
 * for simple jndi extensions
 * 
 * @author levan
 * 
 */
public class JndiManager {

    // Value of InitialContextFactory implementation class
    private static final Class<LightmareInitialContextFactory> FACTORY_CLASS = LightmareInitialContextFactory.class;

    // Name of InitialContextFactory implementation class package
    private static final String PACKAGE_PREFIXES = FACTORY_CLASS.getPackage()
	    .getName();

    // Name of InitialContextFactory implementation class
    private static final String FACTORY_CLASS_NAME = FACTORY_CLASS.getName();

    private static boolean isContextFactory;

    private static Context context;

    private static final Lock LOCK = new ReentrantLock();

    /**
     * Creates and sets {@link InitialContext}
     * 
     * @throws IOException
     */
    private void setInitialCotext() throws IOException {

	if (ObjectUtils.notTrue(isContextFactory)) {
	    System.getProperties().put(Context.INITIAL_CONTEXT_FACTORY,
		    FACTORY_CLASS_NAME);
	    System.getProperties().put(Context.URL_PKG_PREFIXES,
		    PACKAGE_PREFIXES);
	    isContextFactory = Boolean.TRUE;
	}
	if (context == null) {
	    try {
		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY,
			FACTORY_CLASS_NAME);
		properties.put(Context.URL_PKG_PREFIXES, PACKAGE_PREFIXES);
		context = new InitialContext(properties);
	    } catch (NamingException ex) {
		throw new IOException(ex);
	    }
	}
    }

    /**
     * Getter for {@link Context} with check if it is initialized if not calls
     * {@link JndiManager#setInitialCotext()} method
     * 
     * @return {@link Context}
     * @throws IOException
     */
    public Context getContext() throws IOException {
	if (context == null) {
	    LOCK.lock();
	    try {
		setInitialCotext();
	    } finally {
		LOCK.unlock();
	    }
	}

	return context;
    }

    /**
     * Unbinds passed name from {@link Context}
     * 
     * @param name
     * @throws IOException
     */
    public void unbind(String name) throws IOException {

	try {
	    getContext().unbind(name);
	} catch (NamingException ex) {
	    throw new IOException(ex);
	} catch (IOException ex) {
	    throw new IOException(ex);
	}
    }
}
