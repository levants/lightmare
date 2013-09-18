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

    /**
     * Caches JNDI system parameters for initializing {@link Context} instance
     * 
     * @author levan
     * 
     */
    protected static enum JNDIParameters {

	// Name of InitialContextFactory implementation class
	FACTORY_CLASS_NAME(Context.INITIAL_CONTEXT_FACTORY, FACTORY_CLASS
		.getName()),
	// Name of InitialContextFactory implementation class package
	PACKAGE_PREFIXES(Context.URL_PKG_PREFIXES, FACTORY_CLASS.getPackage()
		.getName()),
	// Additional parameter to share JNDI cache
	SHARED_PARAMETER("org.osjava.sj.jndi.shared", Boolean.TRUE.toString());

	private static final Properties CONFIG = new Properties();

	public String key;

	public String value;

	private JNDIParameters(String key, String value) {
	    this.key = key;
	    this.value = value;
	}

	/**
	 * Gets {@link Properties} of all key value pairs of this enumeration
	 */
	protected static Properties getConfig() {

	    if (ObjectUtils.notAvailable(CONFIG)) {

		JNDIParameters[] parameters = JNDIParameters.values();

		for (JNDIParameters parameter : parameters) {
		    CONFIG.put(parameter.key, parameter.value);
		}
	    }

	    return CONFIG;
	}
    }

    // Check variable if system property are set
    private static boolean isContextFactory;

    // Cached context variable
    private static Context context;

    private static final Lock LOCK = new ReentrantLock();

    private void addSystemProperties(Properties configs) {

	System.getProperties().putAll(configs);
    }

    private void setFactoryProperties(Properties properties) {

	if (ObjectUtils.notTrue(isContextFactory)) {
	    addSystemProperties(properties);
	    isContextFactory = Boolean.TRUE;
	}
    }

    private void createContext(Properties properties) throws IOException {

	if (context == null) {

	    try {
		context = new InitialContext(properties);
	    } catch (NamingException ex) {
		throw new IOException(ex);
	    }
	}
    }

    /**
     * Creates and sets {@link InitialContext}
     * 
     * @throws IOException
     */
    private void setInitialCotext() throws IOException {

	if (ObjectUtils.notTrue(isContextFactory) || context == null) {
	    Properties properties = JNDIParameters.getConfig();
	    setFactoryProperties(properties);
	    createContext(properties);
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
