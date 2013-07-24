package org.lightmare.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.lightmare.cache.MetaContainer;
import org.lightmare.rest.providers.JacksonFXmlFeature;
import org.lightmare.rest.providers.ObjectMapperProvider;
import org.lightmare.rest.providers.RestReloader;
import org.lightmare.rest.utils.RestUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Dynamically manage REST resources
 * 
 * @author levan
 * 
 */
public class RestConfig extends ResourceConfig {

    private static RestConfig config;

    private Set<Resource> preResources;

    private RestReloader reloader = RestReloader.get();

    public RestConfig() {
	super();
	register(ObjectMapperProvider.class);
	register(JacksonFXmlFeature.class);
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

    private void clearResources() {

	Set<Resource> resources = getResources();

	if (ObjectUtils.available(resources)) {
	    getResources().clear();
	}
    }

    public void registerAll(RestConfig oldConfig) {

	clearResources();
	Set<Resource> newResources;
	newResources = new HashSet<Resource>();

	if (ObjectUtils.notNull(oldConfig)) {
	    Set<Resource> olds = oldConfig.getResources();
	    if (ObjectUtils.available(olds)) {
		newResources.addAll(olds);
	    }
	}

	registerResources(newResources);
    }

    public void registerClass(Class<?> resourceClass, RestConfig oldConfig)
	    throws IOException {

	Resource.Builder builder = Resource.builder(resourceClass);
	Resource preResource = builder.build();
	Resource resource = RestUtils.defineHandler(preResource);
	Set<Resource> resources = getResources();
	Set<Resource> newResources;
	if (ObjectUtils.available(resources)) {
	    newResources = new HashSet<Resource>(resources);
	} else {
	    newResources = new HashSet<Resource>();
	}

	if (ObjectUtils.notNull(oldConfig)) {
	    Set<Resource> olds = oldConfig.getResources();
	    if (ObjectUtils.available(olds)) {
		newResources.addAll(olds);
	    }
	}

	newResources.add(resource);

	clearResources();

	registerResources(newResources);

	MetaContainer.putResource(resourceClass, resource);
    }

    public void unregister(Class<?> resourceClass, RestConfig oldConfig) {

	Resource resource = MetaContainer.getResource(resourceClass);

	Set<Resource> resources = getResources();
	Set<Resource> newResources;
	if (ObjectUtils.available(resources)) {
	    newResources = new HashSet<Resource>(resources);
	} else {
	    newResources = new HashSet<Resource>();
	}

	clearResources();

	if (ObjectUtils.notNull(oldConfig)) {
	    Set<Resource> olds = oldConfig.getResources();
	    if (ObjectUtils.available(olds)) {
		newResources.addAll(olds);
	    }
	}

	newResources.remove(resource);

	registerResources(newResources);

	MetaContainer.removeResource(resourceClass);
    }

    public void addPreResource(Resource resource) {

	if (this.preResources == null || this.preResources.isEmpty()) {
	    this.preResources = new HashSet<Resource>();
	}

	this.preResources.add(resource);
    }

    public void addPreResources(Collection<Resource> preResources) {

	if (this.preResources == null || this.preResources.isEmpty()) {
	    this.preResources = new HashSet<Resource>();
	}
	this.preResources.addAll(preResources);
    }

    public void registerPreResources() {

	if (ObjectUtils.available(this.preResources)) {
	    registerResources(preResources);
	}
    }
}
