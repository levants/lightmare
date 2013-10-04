package org.lightmare.rest.providers;

import org.lightmare.rest.RestConfig;

/**
 * Default values for REST service application initialization
 * 
 * @author levan
 * @since 0.0.56-SNAPSHOT
 */
public enum ApplicationInit {

    APPLICATION_INIT("javax.ws.rs.Application", RestConfig.class.getName()), // Initializer

    REST_DEFAULT_URI("/rest"); // URL path

    public String key;

    public String value;

    private ApplicationInit(String key) {
	this.key = key;
    }

    private ApplicationInit(String key, String value) {
	this(key);
	this.value = value;
    }
}
