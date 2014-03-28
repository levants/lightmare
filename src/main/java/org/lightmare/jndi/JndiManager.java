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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.apache.log4j.Logger;
import org.hibernate.cfg.AvailableSettings;
import org.lightmare.jndi.JndiManager.JNDIConfigs;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.StringUtils;

/**
 * Utility class to initialize and set (
 * {@link System#setProperty(String, String)}) the {@link InitialContextFactory}
 * for simple JNDI extensions
 * 
 * @author Levan Tsinadze
 * @since 0.0.60-SNAPSHOT
 */
public class JndiManager {

    /**
     * Caches JNDI system parameters for initializing {@link Context} instance
     * 
     * @author Levan Tsinadze
     * @since 0.81-SNAPSHOT
     */
    protected static enum JNDIParameters {

	// Name of InitialContextFactory implementation class
	FACTORY_CLASS_NAME(Context.INITIAL_CONTEXT_FACTORY,
		LightmareContextFactory.class.getName()),
	// Name of InitialContextFactory implementation class package
	PACKAGE_PREFIXES(Context.URL_PKG_PREFIXES,
		LightmareContextFactory.class.getPackage().getName()),
	// Additional parameter to share JNDI cache
	SHARED_PARAMETER("org.osjava.sj.jndi.shared", Boolean.TRUE.toString());

	// Cache of JNDI configuration key value pairs
	private static final Properties CONFIG = new Properties();

	// Cache of JNDI configuration key value pairs
	private static final Map<String, String> HIBERNATE_CONFIG = new HashMap<String, String>();

	public final String key;

	public final String value;

	/**
	 * Constructor with key and value
	 * 
	 * @param key
	 * @param value
	 */
	private JNDIParameters(String key, String value) {
	    this.key = key;
	    this.value = value;
	}

	/**
	 * Gets {@link Properties} of all key value pairs of this enumeration
	 */
	protected static Properties getConfig() {

	    if (CONFIG.isEmpty()) {
		// Gathers configuration instances
		JNDIParameters[] parameters = JNDIParameters.values();
		for (JNDIParameters parameter : parameters) {
		    CONFIG.put(parameter.key, parameter.value);
		}
	    }

	    return CONFIG;
	}

	/**
	 * Gets {@link Map} of all key value pairs of this enumeration prefixed
	 * with as JPA configuration prefix
	 */
	protected static Map<String, String> getHibreanteConfig() {

	    if (HIBERNATE_CONFIG.isEmpty()) {
		// Gathers JPA configuration
		JNDIParameters[] parameters = JNDIParameters.values();
		String hibernatekey;
		for (JNDIParameters parameter : parameters) {
		    hibernatekey = StringUtils.concat(
			    AvailableSettings.JNDI_PREFIX, StringUtils.DOT,
			    parameter.key);
		    HIBERNATE_CONFIG.put(hibernatekey, parameter.value);
		}
	    }

	    return HIBERNATE_CONFIG;
	}
    }

    /**
     * Gets data for JPA JNDI configuration
     * 
     * @author Levan Tsinadze
     * @see JNDIParameters
     * @since 0.1.2
     */
    public static enum JNDIConfigs {

	INIT; // Initialization to read JNDI parameters

	public final Properties config;

	public final Map<String, String> hinbernateConfig;

	private JNDIConfigs() {
	    config = JNDIParameters.getConfig();
	    hinbernateConfig = JNDIParameters.getHibreanteConfig();
	}
    }

    /**
     * Getter for {@link Context} instance
     * 
     * @return {@link Context}
     * @throws IOException
     */
    public static Context getContext() throws IOException {
	return NamingContext.getContext();
    }

    /**
     * Configures JNDI system properties and build {@link Context} if it is not
     * built yet
     */
    public static void loadContext() {
	NamingContext.configure();
    }

    /**
     * Lookups data with passed name in {@link Context} and casts it in
     * appropriated generic type
     * 
     * @param name
     * @return <code>T</code>
     * @throws IOException
     */
    public static <T> T lookup(String name) throws IOException {

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
     * Rebinds (deletes and binds again) passed {@link Object} to
     * {@link Context} by appropriate name
     * 
     * @param name
     * @param data
     * @throws IOException
     */
    public static void rebind(String name, Object data) throws IOException {

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
    public static void bind(String name, Object data) throws IOException {

	try {
	    getContext().bind(name, data);
	} catch (NamingException ex) {
	    throw new IOException(ex);
	} catch (IOException ex) {
	    throw new IOException(ex);
	}
    }

    /**
     * Deletes (unbinds) passed name and associated {@link Object} from
     * {@link Context}
     * 
     * @param name
     * @throws IOException
     */
    public static void unbind(String name) throws IOException {

	try {
	    getContext().unbind(name);
	} catch (NamingException ex) {
	    throw new IOException(ex);
	} catch (IOException ex) {
	    throw new IOException(ex);
	}
    }
}

/**
 * Enumeration to set JNDI system properties and initialize {@link Context}
 * instance
 * 
 * @author Levan Tsinadze
 * @since 0.1.1
 * @see JNDIConfigs
 */
class NamingContext {

    // Error descriptor if Context is not initialized
    private static final String NOT_INITIALIZED_ERROR = "Context not initialized";

    // Check if JNDI properties set as system properties
    private static final AtomicBoolean JNDI_IS_SET = new AtomicBoolean();

    private static final Logger LOG = Logger.getLogger(NamingContext.class);

    /**
     * Checks if {@link System} not contains passed key or passed key is
     * associated with different value
     * 
     * @param key
     * @param value
     * @return <code>boolean</code>
     */
    private boolean check(Object key, Object value) {

	// Checks if system do not contain key
	boolean valid = CollectionUtils
		.notContains(System.getProperties(), key);

	// Checks if system value on key is different then passed value
	if (ObjectUtils.notTrue(valid)) {
	    Object systemvalue = System.getProperties().get(key);
	    valid = ((systemvalue == null) || ObjectUtils.notEquals(
		    systemvalue, value));
	}

	return valid;
    }

    /**
     * Checks if {@link System} properties do not contains passed key and sets
     * it
     * 
     * @param key
     * @param value
     */
    private void checkAndSet(Object key, Object value) {

	if (check(key, value)) {
	    System.getProperties().put(key, value);
	}
    }

    /**
     * Puts passed {@link Properties} as {@link System} properties
     * 
     * @param properties
     */
    private void configure(Properties properties) {

	Set<Map.Entry<Object, Object>> entries = properties.entrySet();
	Object key;
	Object value;
	for (Map.Entry<Object, Object> entry : entries) {
	    key = entry.getKey();
	    value = entry.getValue();
	    checkAndSet(key, value);
	}
    }

    /**
     * Initialized {@link NamingContexts} and contained {@link Context} with
     * system properties
     */
    protected static void configure() {

	if (ObjectUtils.notTrue(JNDI_IS_SET.getAndSet(Boolean.TRUE))) {
	    ClassLoader loader = LibraryLoader.getContextClassLoader();
	    Thread thread = Thread.currentThread();
	    try {
		thread.setContextClassLoader(ClassLoader.getSystemClassLoader());
		// Gets system properties
		Properties properties = JNDIConfigs.INIT.config;
		// Registers properties as system properties
		NamingContext namingContext = new NamingContext();
		namingContext.configure(properties);
	    } finally {
		thread.setContextClassLoader(loader);
	    }
	}
    }

    /**
     * If context is not initialized throws {@link IOException} registered else
     * returns single {@link Context} instance instance
     * 
     */
    protected static Context getContext() throws IOException {

	InitialContext context;

	try {
	    configure();
	    Properties propertis = JNDIConfigs.INIT.config;
	    context = new InitialContext(propertis);
	} catch (NamingException ex) {
	    LOG.error(ex.getMessage(), ex);
	    throw new IOException(NOT_INITIALIZED_ERROR, ex);
	}

	return context;
    }
}
