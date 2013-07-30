package org.lightmare.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.lightmare.cache.RestContainer;
import org.lightmare.rest.providers.JacksonFXmlFeature;
import org.lightmare.rest.providers.ObjectMapperProvider;
import org.lightmare.rest.providers.RestReloader;
import org.lightmare.rest.utils.ResourceBuilder;
import org.lightmare.utils.ObjectUtils;

/**
 * Dynamically manage REST resources, implementation of {@link ResourceConfig}
 * class to add and remove {@link Resource}'s and reload at runtime
 * 
 * @author levan
 * 
 */
public class RestConfig extends ResourceConfig {

    private static RestConfig config;

    // Collection of resources before registration
    private Set<Resource> preResources;

    // Reloader instance (implementation of ContainerLifecycleListener class)
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
	    if (ObjectUtils.notNull(config)) {
		// Adds resources to pre-resources from existing cached
		// configuration
		this.addPreResources(config);
		Map<String, Object> properties = config.getProperties();
		if (ObjectUtils.available(properties)) {
		    addProperties(properties);
		}
	    }
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

    /**
     * Registers {@link Resource}s from passed {@link RestConfig} as
     * {@link RestConfig#preResources} cache
     * 
     * @param oldConfig
     */
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

    /**
     * Caches {@link Resource} created from passed {@link Class} for further
     * registration
     * 
     * @param resourceClass
     * @param oldConfig
     * @throws IOException
     */
    public void registerClass(Class<?> resourceClass, RestConfig oldConfig)
	    throws IOException {

	Resource.Builder builder = Resource.builder(resourceClass);
	Resource preResource = builder.build();
	Resource resource = ResourceBuilder.rebuildResource(preResource);
	addPreResource(resource);

	RestContainer.putResource(resourceClass, resource);
    }

    /**
     * Removes {@link Resource} created from passed {@link Class} from
     * pre-resources cache
     * 
     * @param resourceClass
     */
    public void unregister(Class<?> resourceClass) {

	Resource resource = RestContainer.getResource(resourceClass);
	removePreResource(resource);
	RestContainer.removeResource(resourceClass);
    }

    public void addPreResource(Resource resource) {

	if (this.preResources == null || this.preResources.isEmpty()) {
	    this.preResources = new HashSet<Resource>();
	}

	this.preResources.add(resource);
    }

    public void removePreResource(Resource resource) {

	if (ObjectUtils.available(this.preResources)) {
	    this.preResources.remove(resource);
	}
    }

    public void addPreResources(Collection<Resource> preResources) {

	if (ObjectUtils.available(preResources)) {
	    if (this.preResources == null || this.preResources.isEmpty()) {
		this.preResources = new HashSet<Resource>();
	    }
	    this.preResources.addAll(preResources);
	}
    }

    public void addPreResources(RestConfig oldConfig) {

	if (ObjectUtils.notNull(oldConfig)) {
	    addPreResources(oldConfig.getResources());
	    addPreResources(oldConfig.preResources);
	}
    }

    public void registerPreResources() {

	if (ObjectUtils.available(this.preResources)) {
	    registerResources(preResources);
	}
    }
}
