package org.lightmare.rest;

import java.util.HashSet;
import java.util.Set;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.lightmare.cache.MetaContainer;
import org.lightmare.rest.providers.ObjectMapperProvider;
import org.lightmare.rest.providers.RestReloader;
import org.lightmare.utils.ObjectUtils;

/**
 * Dynamically manage REST resources
 * 
 * @author levan
 * 
 */
public class RestConfig extends ResourceConfig {

    private static RestConfig config;

    private RestReloader reloader = RestReloader.get();

    public RestConfig() {
	super();
	register(MultiPartFeature.class);
	register(ObjectMapperProvider.class);
	register(JacksonFeature.class);
	synchronized (RestConfig.class) {
	    if (reloader == null) {
		reloader = new RestReloader();
	    }
	    this.registerInstances(reloader);
	    config = this;
	}
    }

    public static RestConfig get() {

	synchronized (RestConfig.class) {

	    return config;
	}
    }

    public void registerClass(Class<?> resourceClass, RestConfig oldConfig) {

	Resource.Builder builder = Resource.builder(resourceClass);
	Resource resource = builder.build();
	Set<Resource> resources = getResources();
	Set<Resource> newResources;
	if (ObjectUtils.available(resources)) {
	    newResources = new HashSet<Resource>();
	} else {
	    newResources = new HashSet<Resource>(resources);
	}

	if (ObjectUtils.notNull(oldConfig)) {
	    Set<Resource> olds = oldConfig.getResources();
	    if (ObjectUtils.available(olds)) {
		newResources.addAll(olds);
	    }
	}

	newResources.add(resource);

	registerResources(newResources);

	MetaContainer.putResource(resourceClass, resource);
    }

    public void unregister(Class<?> resourceClass) {

	Resource resource = MetaContainer.getResource(resourceClass);

	Set<Resource> resources = getResources();
	Set<Resource> newResources;
	if (ObjectUtils.available(resources)) {
	    newResources = new HashSet<Resource>();
	} else {
	    newResources = new HashSet<Resource>(resources);
	}
	newResources.remove(resource);

	registerResources(newResources);
    }
}
