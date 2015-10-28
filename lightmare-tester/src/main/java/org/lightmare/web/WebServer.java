package org.lightmare.web;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.lightmare.deploy.management.DeployManager;
import org.lightmare.listeners.LoaderListener;
import org.lightmare.rest.RestConfig;
import org.lightmare.servlets.PersonManager;
import org.lightmare.utils.collections.CollectionUtils;

public class WebServer implements Runnable {

    private static final int HTTP_SERVER_PORT = 8080;

    private static final String APPLICATION_PARAM_NAME = "javax.ws.rs.Application";

    private static final Logger LOG = Logger.getLogger(WebServer.class);

    private static final ExecutorService POOL = Executors
	    .newSingleThreadExecutor();

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
	    Map<String, String> inits = new HashMap<String, String>();
	    inits.put(APPLICATION_PARAM_NAME, RestConfig.class.getName());
	    restHolder.setInitParameters(inits);
	    restHolder.setInitOrder(1);
	    restHolder.setServlet(container);
	    ctxRest.addServlet(restHolder, "/*");

	    WebServlet webServlet = DeployManager.class
		    .getAnnotation(WebServlet.class);
	    String deployManagerName = CollectionUtils.getFirst(webServlet
		    .value());
	    ServletContextHandler ctxManager = new ServletContextHandler(
		    contexts, deployManagerName, ServletContextHandler.SESSIONS);

	    DeployManager deploy = new DeployManager();
	    ServletHolder managerHolder = new ServletHolder();

	    managerHolder.setServlet(deploy);
	    ctxManager.addServlet(managerHolder, "/*");

	    ServletContextHandler ctxPerson = new ServletContextHandler(
		    contexts, "/persons", ServletContextHandler.SESSIONS);
	    ctxPerson.addEventListener(new LoaderListener());

	    PersonManager manager = new PersonManager();
	    ServletHolder personHolder = new ServletHolder();

	    personHolder.setServlet(manager);
	    ctxPerson.addServlet(personHolder, "/*");

	    contexts.setHandlers(new Handler[] { ctxRest, ctxManager, ctxPerson });
	    jettyServer.start();
	    jettyServer.join();

	} catch (Exception ex) {
	    LOG.error("Error while starting jetty server", ex);
	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

	WebServer web = new WebServer();
	POOL.submit(web);
    }

}
