package org.lightmare.rest.providers;

import org.lightmare.rest.RestConfig;

/**
 * Default values for REST service application initialization
 * 
 * @author levan
 * 
 */
public enum ApplicationInit {
    
    APPLICATION_INIT("javax.ws.rs.Application", RestConfig.class
	    .getName()),

    public static final String REST_DEFAULT_URI = "/rest";

    public static final String APPLICATION_INIT_PARAM_NAME = "javax.ws.rs.Application";

    public static final String APPLICATION_INIT_PARAM = RestConfig.class
	    .getName();
    
    public String key;
    
    public String value;
    
    private ApplicationInit(String key){
	this.key = key;
    }
    
    private ApplicationInit(String key, String value){
	this(key);
	this.value = value;
    }
}
