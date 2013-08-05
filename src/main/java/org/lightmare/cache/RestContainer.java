package org.lightmare.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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

    public static void clearExistingResources(Set<Resource> existingResources) {

	if (ObjectUtils.available(existingResources)) {
	    Set<Map.Entry<Class<?>, Resource>> cacheds = REST_RESOURCES
		    .entrySet();
	    Iterator<Map.Entry<Class<?>, Resource>> iterator;
	    Map.Entry<Class<?>, Resource> cached;
	    boolean removed;
	    for (Resource existingResource : existingResources) {

		iterator = cacheds.iterator();
		removed = Boolean.FALSE;
		while (iterator.hasNext() && ObjectUtils.notTrue(removed)) {
		    cached = iterator.next();
		    removed = cached.getValue().equals(existingResource);
		    if (removed) {
			iterator.remove();
		    }
		}
	    }
	}
    }
}
