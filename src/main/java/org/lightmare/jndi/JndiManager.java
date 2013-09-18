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

    protected static enum JNDIParameters {

	// Name of InitialContextFactory implementation class
	FACTORY_CLASS_NAME(Context.INITIAL_CONTEXT_FACTORY, FACTORY_CLASS
		.getName()),
	// Name of InitialContextFactory implementation class package
	PACKAGE_PREFIXES(Context.URL_PKG_PREFIXES, FACTORY_CLASS.getPackage()
		.getName()),
	// Additional parameter to share JNDI cache
	SHARED_PARAMETER("org.osjava.sj.jndi.shared", Boolean.TRUE.toString());

	public String key;

	public String value;

	private JNDIParameters(String key, String value) {
	    this.key = key;
	    this.value = value;
	}
    }

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
	    System.getProperties().put(JNDIParameters.FACTORY_CLASS_NAME.key,
		    JNDIParameters.FACTORY_CLASS_NAME.value);
	    System.getProperties().put(JNDIParameters.PACKAGE_PREFIXES.key,
		    JNDIParameters.PACKAGE_PREFIXES.value);
	    System.getProperties().put(JNDIParameters.SHARED_PARAMETER.key,
		    JNDIParameters.SHARED_PARAMETER.value);
	    isContextFactory = Boolean.TRUE;
	}
	if (context == null) {
	    try {
		Properties properties = new Properties();
		properties.put(JNDIParameters.FACTORY_CLASS_NAME.key,
			JNDIParameters.FACTORY_CLASS_NAME.value);
		properties.put(JNDIParameters.PACKAGE_PREFIXES.key,
			JNDIParameters.PACKAGE_PREFIXES.value);
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
     * Lookups data with passed name in {@link Context} and cast it in generic
     * type
     * 
     * @param name
     * @return <code>T</code>
     * @throws IOException
     */
    public <T> T lookup(String name) throws IOException {

	T value;

	try {

	    Object data = getContext().lookup(name);
	    value = ObjectUtils.cast(data);

	} catch (NamingException ex) {
	    throw new IOException(ex);
	}

	return value;
    }

    /**
     * Rebinds passed {@link Object} to {@link Context} by appropriate name
     * 
     * @param name
     * @param data
     * @throws IOException
     */
    public void rebind(String name, Object data) throws IOException {

	try {
	    getContext().rebind(name, data);
	} catch (NamingException ex) {
	    throw new IOException(ex);
	} catch (IOException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Binds passed {@link Object} to {@link Context} by appropriate name
     * 
     * @param name
     * @param data
     * @throws IOException
     */
    public void bind(String name, Object data) throws IOException {

	try {
	    getContext().bind(name, data);
	} catch (NamingException ex) {
	    throw new IOException(ex);
	} catch (IOException ex) {
	    throw new IOException(ex);
	}
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
