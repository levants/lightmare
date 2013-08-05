package org.lightmare.cache;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.glassfish.jersey.server.model.Resource;
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

    private static final AtomicBoolean AVAILABLE = new AtomicBoolean();

    public static void putResource(Class<?> handlerClass, Resource resource) {
	REST_RESOURCES.putIfAbsent(handlerClass, resource);
    }

    private static Class<?> getHandlerClass(Resource resource) {

	Class<?> handlerClass;
	Set<Class<?>> handlerClasses = resource.getHandlerClasses();
	if (ObjectUtils.available(handlerClasses)) {
	    handlerClass = ObjectUtils.getFirst(handlerClasses);
	} else {
	    handlerClass = null;
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

	if (ObjectUtils.available(resources)) {
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

	if (ObjectUtils.available(existingResources)) {

	    for (Resource existingResource : existingResources) {
		removeResource(existingResource);
	    }
	}
    }

    public static boolean setAvailability(boolean restValue) {

	return AVAILABLE.getAndSet(restValue);
    }

    /**
     * Checks if application has REST resources
     * 
     * @return
     */
    public static boolean hasRest() {

	return AVAILABLE.get();
    }
}
