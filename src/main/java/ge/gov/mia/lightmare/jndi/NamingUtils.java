package ge.gov.mia.lightmare.jndi;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class NamingUtils {

	private static boolean isContextFactory;

	private static volatile Context context;

	public void setInitialCotext() throws IOException {
		if (!isContextFactory) {
			System.getProperties().put(Context.INITIAL_CONTEXT_FACTORY,
					"ge.gov.mia.lightmare.jndi.DSInitialContextFactory");
			System.getProperties().put(Context.URL_PKG_PREFIXES,
					"ge.gov.mia.lightmare.jndi");
			isContextFactory = true;
		}
		if (context == null) {
			try {
				Properties properties = new Properties();
				properties.put(Context.INITIAL_CONTEXT_FACTORY,
						"ge.gov.mia.lightmare.jndi.DSInitialContextFactory");
				properties.put(Context.URL_PKG_PREFIXES,
						"ge.gov.mia.lightmare.jndi");
				context = new InitialContext(properties);
			} catch (NamingException ex) {
				throw new IOException(ex);
			}
		}
	}

	public Context getContext() throws IOException {
		if (context == null) {
			synchronized (NamingUtils.class) {
				setInitialCotext();
			}
		}

		return context;
	}
}
