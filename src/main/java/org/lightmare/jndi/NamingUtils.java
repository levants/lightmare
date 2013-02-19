package org.lightmare.jndi;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * Utility class to initialize and set (
 * {@link System#setProperty(String, String)}) the {@link InitialContextFactory}
 * for simple jndi extensions
 * 
 * @author levan
 * 
 */
public class NamingUtils {

	private static boolean isContextFactory;

	private static Context context;

	/**
	 * Creates jndi name pefixes for ejb objects
	 * 
	 * @param jndiName
	 * @return
	 */
	public static String createJndiName(String jndiName) {

		return String.format("java:comp/env/%s", jndiName);
	}

	public void unbind(String name) throws IOException {

		try {
			getContext().unbind(name);
		} catch (NamingException ex) {
			throw new IOException(ex);
		} catch (IOException ex) {
			throw new IOException(ex);
		}
	}

	/**
	 * Creates and sets {@link InitialContext}
	 * 
	 * @throws IOException
	 */
	public void setInitialCotext() throws IOException {
		if (!isContextFactory) {
			System.getProperties().put(Context.INITIAL_CONTEXT_FACTORY,
					"org.lightmare.jndi.DSInitialContextFactory");
			System.getProperties().put(Context.URL_PKG_PREFIXES,
					"org.lightmare.jndi");
			isContextFactory = true;
		}
		if (context == null) {
			try {
				Properties properties = new Properties();
				properties.put(Context.INITIAL_CONTEXT_FACTORY,
						"org.lightmare.jndi.DSInitialContextFactory");
				properties.put(Context.URL_PKG_PREFIXES, "org.lightmare.jndi");
				context = new InitialContext(properties);
			} catch (NamingException ex) {
				throw new IOException(ex);
			}
		}
	}

	/**
	 * Getter for {@link Context} with check if it is initialized if not calls
	 * {@link NamingUtils#setInitialCotext()} method
	 * 
	 * @return {@link Context}
	 * @throws IOException
	 */
	public Context getContext() throws IOException {
		if (context == null) {
			synchronized (NamingUtils.class) {
				setInitialCotext();
			}
		}

		return context;
	}
}
