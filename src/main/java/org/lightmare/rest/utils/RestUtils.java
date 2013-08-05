package org.lightmare.rest.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.ws.rs.Path;

import org.glassfish.jersey.server.model.Resource;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.MetaData;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.rest.RestConfig;
import org.lightmare.rest.providers.RestReloader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.serialization.JsonSerializer;

/**
 * Utility class for REST resources
 * 
 * @author levan
 * 
 */
public class RestUtils {

    private static RestConfig newConfig;

    private static RestConfig existingConfig;

    private static void getConfig() {

	existingConfig = RestConfig.get();
	newConfig = new RestConfig(Boolean.FALSE);
    }

    private static RestConfig get() {

	synchronized (RestUtils.class) {
	    getConfig();
	}

	return newConfig;
    }

    public static <T> T convert(String json, Class<T> valueClass)
	    throws IOException {

	T value = JsonSerializer.read(json, valueClass);

	return value;
    }

    public static String json(Object data) throws IOException {

	return JsonSerializer.write(data);
    }

    /**
     * Checks if class is acceptable to build {@link Resource} instance
     * 
     * @param resourceClass
     * @return <code>boolean</code>
     */
    private static boolean isAcceptable(Class<?> resourceClass) {

	boolean valid = Resource.isAcceptable(resourceClass)
		&& resourceClass.isAnnotationPresent(Path.class);

	return valid;
    }

    /**
     * Adds bean {@link Class} as
     * {@link org.glassfish.jersey.server.model.Resource} to {@link RestConfig}
     * instance
     * 
     * @param beanClass
     * @throws IOException
     */
    public static void add(Class<?> beanClass) throws IOException {

	boolean valid = isAcceptable(beanClass);
	if (valid) {

	    RestReloader reloader = RestReloader.get();
	    if (ObjectUtils.notNull(reloader)) {
		RestConfig conf = get();
		conf.registerClass(beanClass, existingConfig);
	    }
	}
    }

    /**
     * Removes bean {@link Class} as
     * {@link org.glassfish.jersey.server.model.Resource} to {@link RestConfig}
     * instance
     * 
     * @param beanClass
     */
    public static void remove(Class<?> beanClass) {

	RestReloader reloader = RestReloader.get();
	if (ObjectUtils.notNull(reloader)) {
	    RestConfig conf = get();
	    conf.unregister(beanClass);
	    if (ObjectUtils.notNull(existingConfig)) {
		Collection<Resource> existings = existingConfig.getResources();
		conf.changeOnRemoveState(existings);
	    }
	}
    }

    /**
     * Gets common class loader (enriched for each {@link ClassLoader} from
     * {@link MetaData}) to add to REST server
     * 
     * @return {@link ClassLoader}
     */
    public static ClassLoader getCommonLoader() {

	Iterator<MetaData> iterator = MetaContainer.getBeanClasses();
	MetaData metaData;
	ClassLoader newLoader;
	ClassLoader oldLoader = null;
	ClassLoader commonLoader = null;
	while (iterator.hasNext()) {
	    metaData = iterator.next();
	    newLoader = metaData.getLoader();
	    if (ObjectUtils.notNull(oldLoader)
		    && ObjectUtils.notNull(newLoader)) {
		commonLoader = LibraryLoader.createCommon(newLoader, oldLoader);
	    }
	    oldLoader = newLoader;
	}

	return commonLoader;
    }

    /**
     * Reloads {@link RestConfig} instance with new registered
     * {@link org.glassfish.jersey.server.model.Resource}s to activate
     */
    public static void reload() {

	RestReloader reloader = RestReloader.get();
	RestConfig conf = newConfig;
	if (ObjectUtils.notNull(conf) && ObjectUtils.notNull(reloader)) {
	    ClassLoader commonLoader = getCommonLoader();
	    if (ObjectUtils.notNull(commonLoader)) {
		conf.setClassLoader(commonLoader);
	    }
	    conf.registerPreResources();
	    conf.cache();
	    reloader.reload(conf);
	}
    }
}
