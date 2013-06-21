package org.lightmare.jetty;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Jetty embeddable server to listen rest requests
 * 
 * @author Levan
 * 
 */
public class JettyServer implements Runnable {

    private static final int HTTP_SERVER_PORT = 8080;

    private static final Logger LOG = Logger.getLogger(JettyServer.class);

    @Override
    public void run() {

	try {
	    Server jettyServer = new Server(HTTP_SERVER_PORT);
	    ContextHandlerCollection contexts = new ContextHandlerCollection();
	    jettyServer.setHandler(contexts);

	    ServletContextHandler ctxRest = new ServletContextHandler(contexts,
		    "/rest", ServletContextHandler.SESSIONS);

	    ServletContainer container = new ServletContainer();
	    ServletHolder restHolder = new ServletHolder();
	    restHolder.setInitParameter(
		    "com.sun.jersey.config.property.packages",
		    "org.lightmare.rest");
	    restHolder.setInitOrder(1);
	    restHolder.setServlet(container);
	    ctxRest.addServlet(restHolder, "/*");

	    contexts.setHandlers(new Handler[] { ctxRest });
	    jettyServer.start();
	    jettyServer.join();

	} catch (Exception ex) {
	    LOG.error("Error while starting jetty server", ex);
	}
    }
}
