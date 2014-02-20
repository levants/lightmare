/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
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
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.apache.log4j.Logger;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Utility class to initialize and set (
 * {@link System#setProperty(String, String)}) the {@link InitialContextFactory}
 * for simple JNDI extensions
 * 
 * @author Levan Tsinadze
 * @since 0.0.60-SNAPSHOT
 */
public class JndiManager {

    // Value of InitialContextFactory implementation class
    private static final Class<LightmareInitialContextFactory> FACTORY_CLASS = LightmareInitialContextFactory.class;

    private static final Logger LOG = Logger.getLogger(JndiManager.class);

    /**
     * Caches JNDI system parameters for initializing {@link Context} instance
     * 
     * @author Levan Tsinadze
     * @since 0.81-SNAPSHOT
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

	// Cache of JNDI configuration key value pairs
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

	    if (CollectionUtils.invalid(CONFIG)) {
		JNDIParameters[] parameters = JNDIParameters.values();
		for (JNDIParameters parameter : parameters) {
		    CONFIG.put(parameter.key, parameter.value);
		}
	    }

	    return CONFIG;
	}
    }

    /**
     * Enumeration to set JNDI system properties and initialize {@link Context}
     * instance
     * 
     * @author Levan Tsinadze
     * @since 0.1.1
     */
    private static enum NamingContexts {

	CONTEXT;

	private final Context context;

	private static final String NOT_INITIALIZED_ERROR = "Context not initialized";

	private NamingContexts() {
	    // Gets system properties
	    Properties properties = JNDIParameters.getConfig();
	    // Registers properties as system properties
	    System.getProperties().putAll(properties);
	    try {
		context = new InitialContext(properties);
	    } catch (NamingException ex) {
		LOG.error(ex.getMessage(), ex);
		throw new ExceptionInInitializerError(ex);
	    }
	}

	/**
	 * If context is not initialized throws {@link IOException} registered
	 * else returns single {@link Context} instance instance
	 * 
	 */
	protected Context getContext() throws IOException {

	    if (context == null) {
		throw new IOException(NOT_INITIALIZED_ERROR);
	    }

	    return context;
	}
    }

    /**
     * Getter for {@link Context} instance
     * 
     * @return {@link Context}
     * @throws IOException
     */
    public static Context getContext() throws IOException {
	return NamingContexts.CONTEXT.getContext();
    }

    /**
     * Lookups data with passed name in {@link Context} and casts it in
     * appropriated generic type
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
