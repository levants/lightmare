package org.lightmare.rest.providers;

import org.lightmare.rest.RestConfig;

/**
 * Default values for REST service application initialization
 * 
 * @author levan
 * 
 */
public class ApplicationInit {

    public static final String REST_DEFAULT_URI = "/rest";

    public static final String APPLICATION_INIT_PARAM_NAME = "javax.ws.rs.Application";

    public static final String APPLICATION_INIT_PARAM = RestConfig.class
	    .getName();
}
