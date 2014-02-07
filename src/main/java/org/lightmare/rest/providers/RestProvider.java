/*
 * Lightmare, Embeddable ejb container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.rest.providers;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.ws.rs.Path;

import org.glassfish.jersey.server.model.Resource;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.MetaData;
import org.lightmare.cache.RestContainer;
import org.lightmare.libraries.LibraryLoader;
import org.lightmare.rest.RestConfig;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.serialization.JsonSerializer;

/**
 * Utility class for REST resources
 * 
 * @author Levan Tsinadze
 * @since 0.0.75-SNAPSHOT
 * @see RestConfig
 */
public class RestProvider {

    private static RestConfig newConfig;

    private static final Lock LOCK = new ReentrantLock();

    private static void getConfig() {

	if (newConfig == null) {
	    newConfig = new RestConfig(Boolean.FALSE);
	}
    }

    private static RestConfig get() {

	if (newConfig == null) {
	    ObjectUtils.lock(LOCK);
	    try {
		getConfig();
	    } finally {
		ObjectUtils.unlock(LOCK);
	    }
	}

	return newConfig;
    }

    /**
     * Converts passed data from JSON to passed generic {@link Class} instance
     * 
     * @param json
     * @param valueClass
     * @return <code>T</code>
     * @throws IOException
     */
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
		RestConfig existingConfig = RestContainer.getRestConfig();
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
	}
    }

    /**
     * Gets common class loader (enriched for each {@link ClassLoader} from
     * {@link MetaData}) to add to REST server
     * 
     * @return {@link ClassLoader}
     */
    public static ClassLoader getCommonLoader() {

	ClassLoader commonLoader = null;

	Iterator<MetaData> iterator = MetaContainer.getBeanClasses();
	MetaData metaData;
	ClassLoader newLoader;
	ClassLoader oldLoader = null;

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

	try {
	    RestReloader reloader = RestReloader.get();
	    RestConfig conf = get();
	    if (ObjectUtils.notNull(conf) && ObjectUtils.notNull(reloader)) {
		if (RestContainer.hasRest()) {
		    RestConfig existingConfig = RestContainer.getRestConfig();
		    Set<Resource> existingResources = existingConfig
			    .getResources();
		    RestContainer.removeResources(existingResources);
		}

		ClassLoader commonLoader = getCommonLoader();
		if (ObjectUtils.notNull(commonLoader)) {
		    conf.setClassLoader(commonLoader);
		}

		conf.registerPreResources();
		reloader.reload(conf);
		conf.cache();
	    }
	} finally {
	    newConfig = null;
	}
    }
}
