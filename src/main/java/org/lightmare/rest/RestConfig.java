package org.lightmare.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.lightmare.cache.MetaContainer;

/**
 * Dynamically manage REST resources
 * 
 * @author levan
 * 
 */
public class RestConfig extends ResourceConfig {

    private static RestConfig config;

    public RestConfig() {
	super();
	config = this;
    }

    public static RestConfig get() {
	return config;
    }

    public void registerClass(Class<?> resourceClass) {

	Resource.Builder builder = Resource.builder(resourceClass);
	Resource resource = builder.build();
	getResources().add(resource);
	MetaContainer.putResource(resourceClass, resource);
    }

    public void unregister(Class<?> resourceClass) {

	Resource resource = MetaContainer.getResource(resourceClass);
	getResources().remove(resource);
    }
}
