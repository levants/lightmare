package org.lightmare.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    public static void putResource(Class<?> resourceClass, Resource resource) {
	REST_RESOURCES.putIfAbsent(resourceClass, resource);
    }

    public static Resource getResource(Class<?> resourceClass) {

	Resource resource = REST_RESOURCES.get(resourceClass);

	return resource;
    }

    public static void removeResource(Class<?> resourceClass) {

	REST_RESOURCES.remove(resourceClass);
    }

    /**
     * Checks if application has REST resources
     * 
     * @return
     */
    public static boolean hasRest() {

	return ObjectUtils.available(REST_RESOURCES);
    }
}
