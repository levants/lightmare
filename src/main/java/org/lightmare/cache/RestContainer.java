package org.lightmare.cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.glassfish.jersey.server.model.Resource;
import org.lightmare.rest.RestConfig;
import org.lightmare.rest.providers.RestInflector;
import org.lightmare.utils.CollectionUtils;
import org.lightmare.utils.ObjectUtils;

/**
 * Container class to cache REST resource classes
 * 
 * @author levan
 * 
 */
public class RestContainer {

    // Cached REST resource classes
    private static final ConcurrentMap<Class<?>, Resource> REST_RESOURCES = new ConcurrentHashMap<Class<?>, Resource>();

    // Cached running instance of RestConfig class
    private static RestConfig restConfig;

    public static void putResource(Class<?> handlerClass, Resource resource) {

	REST_RESOURCES.putIfAbsent(handlerClass, resource);
    }

    /**
     * Finds if {@link Resource} has handler instances and if they are instance
     * of {@link RestInflector} and gets appropriate bean class
     * 
     * @param resource
     * @return {@link Class}
     */
    private static Class<?> getFromHandlerInstance(Resource resource) {

	Class<?> handlerClass = null;

	Set<Object> handlers = resource.getHandlerInstances();
	if (CollectionUtils.available(handlers)) {
	    Iterator<Object> iterator = handlers.iterator();
	    Object handler;
	    RestInflector inflector;
	    while (iterator.hasNext() && handlerClass == null) {
		handler = iterator.next();
		if (handler instanceof RestInflector) {
		    inflector = ObjectUtils.cast(handler, RestInflector.class);
		    handlerClass = inflector.getBeanClass();
		}
	    }
	}

	return handlerClass;
    }

    /**
     * Gets handler bean class directly from {@link Resource} or from handler
     * instances
     * 
     * @param resource
     * @return {@link Class}
     */
    private static Class<?> getHandlerClass(Resource resource) {

	Class<?> handlerClass;
	Set<Class<?>> handlerClasses = resource.getHandlerClasses();
	if (CollectionUtils.available(handlerClasses)) {
	    handlerClass = CollectionUtils.getFirst(handlerClasses);
	} else {
	    handlerClass = getFromHandlerInstance(resource);
	}

	return handlerClass;
    }

    public static void putResource(Resource resource) {

	Class<?> handlerClass = getHandlerClass(resource);
	if (ObjectUtils.notNull(handlerClass)) {
	    putResource(handlerClass, resource);
	}
    }

    public static void putResources(Collection<Resource> resources) {

	if (CollectionUtils.available(resources)) {
	    for (Resource resource : resources) {
		putResource(resource);
	    }
	}
    }

    public static Resource getResource(Class<?> resourceClass) {

	Resource resource = REST_RESOURCES.get(resourceClass);

	return resource;
    }

    public static void removeResource(Class<?> resourceClass) {

	REST_RESOURCES.remove(resourceClass);
    }

    public static void removeResource(Resource resource) {

	Class<?> handlerClass = getHandlerClass(resource);
	if (ObjectUtils.notNull(handlerClass)) {
	    REST_RESOURCES.remove(handlerClass);
	}
    }

    public static int size() {

	return REST_RESOURCES.size();
    }

    public static void removeResources(Set<Resource> existingResources) {

	if (CollectionUtils.available(existingResources)) {

	    for (Resource existingResource : existingResources) {
		removeResource(existingResource);
	    }
	}
    }

    /**
     * Checks if application has REST resources
     * 
     * @return <code>boolean</code>
     */
    public static boolean hasRest() {

	synchronized (RestContainer.class) {
	    return ObjectUtils.notNull(restConfig);
	}
    }

    public static void setRestConfig(RestConfig newConfig) {

	synchronized (RestContainer.class) {
	    restConfig = newConfig;
	}
    }

    public static RestConfig getRestConfig() {

	return restConfig;
    }

    /**
     * Clears cached rest resources
     */
    public static void clear() {

	REST_RESOURCES.clear();
    }
}
