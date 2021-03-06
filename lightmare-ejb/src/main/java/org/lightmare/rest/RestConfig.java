/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
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
package org.lightmare.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.lightmare.cache.RestContainer;
import org.lightmare.rest.providers.JacksonFXmlFeature;
import org.lightmare.rest.providers.ObjectMapperProvider;
import org.lightmare.rest.providers.ResourceBuilder;
import org.lightmare.rest.providers.RestReloader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;

/**
 * Dynamically manage REST resources, implementation of {@link ResourceConfig}
 * class to add and remove {@link Resource}'s and reload at runtime
 * 
 * @author Levan Tsinadze
 * @since 0.0.50-SNAPSHOT
 */
public class RestConfig extends ResourceConfig {

    // Collection of resources before registration
    private Set<Resource> preResources;

    // Reloader instance (implementation of ContainerLifecycleListener class)
    private RestReloader reloader = RestReloader.get();

    // Lock of registration and initialization
    private static final Lock LOCK = new ReentrantLock();

    /**
     * Constructor with <code>boolean</code> flag change cache of configuration
     * or not
     * 
     * @param changeCache
     */
    public RestConfig(boolean changeCache) {
        super();
        RestConfig config = RestContainer.getRestConfig();
        register(ObjectMapperProvider.class);
        register(JacksonFXmlFeature.class);
        ObjectUtils.lock(LOCK);
        try {
            if (reloader == null) {
                reloader = new RestReloader();
            }
            this.registerInstances(reloader);

            if (ObjectUtils.notNull(config)) {
                // Adds resources to pre-resources from existing cached
                // configuration
                this.addPreResources(config);
                Map<String, Object> properties = config.getProperties();
                if (CollectionUtils.valid(properties)) {
                    addProperties(properties);
                }
            }

            if (changeCache) {
                RestContainer.setRestConfig(this);
            }
        } finally {
            ObjectUtils.unlock(LOCK);
        }
    }

    public RestConfig() {
        this(Boolean.TRUE);
    }

    /**
     * Caches this instance of {@link RestConfig} in {@link RestContainer} cache
     */
    public void cache() {

        RestConfig config = RestContainer.getRestConfig();
        if (ObjectUtils.notEquals(this, config)) {
            RestContainer.setRestConfig(this);
        }
    }

    /**
     * Clears deployed REST {@link Resource}s for deploy ervices
     */
    private void clearResources() {

        Set<Resource> resources = getResources();
        if (CollectionUtils.valid(resources)) {
            getResources().clear();
        }
    }

    /**
     * Registers {@link Resource}s from passed {@link RestConfig} to cache
     * 
     * @param oldConfig
     */
    public void registerAll(RestConfig oldConfig) {

        clearResources();
        Set<Resource> newResources = new HashSet<Resource>();
        if (ObjectUtils.notNull(oldConfig)) {
            Set<Resource> olds = oldConfig.getResources();
            if (CollectionUtils.valid(olds)) {
                newResources.addAll(olds);
            }
        }

        registerResources(newResources);
    }

    /**
     * Adds {@link Collection} of {@link Resource} to cache for further
     * registration
     * 
     * @param toAdd
     */
    public void addPreResources(Collection<Resource> toAdd) {

        if (CollectionUtils.valid(toAdd)) {
            if (this.preResources == null || this.preResources.isEmpty()) {
                this.preResources = new HashSet<Resource>();
            }
            this.preResources.addAll(toAdd);
        }
    }

    /**
     * Adds {@link Resource} to cache for further registration
     * 
     * @param resource
     */
    public void addPreResource(Resource resource) {

        Collection<Resource> resources = Collections.singleton(resource);
        addPreResources(resources);
    }

    /**
     * Adds {@link Collection} of {@link Resource} from old {@link RestConfig}
     * to cache for further registration
     * 
     * @param oldConfig
     */
    public void addPreResources(RestConfig oldConfig) {

        if (ObjectUtils.notNull(oldConfig)) {
            addPreResources(oldConfig.getResources());
            addPreResources(oldConfig.preResources);
        }

    }

    /**
     * Removes {@link Resource} from cache before it's been registered
     * 
     * @param resource
     */
    private void removePreResource(Resource resource) {

        if (CollectionUtils.valid(preResources)) {
            preResources.remove(resource);
        }
    }

    /**
     * Caches {@link Resource} created from passed {@link Class} for further
     * registration
     * 
     * @param resourceClass
     * @param oldConfig
     * @throws IOException
     */
    public void registerClass(Class<?> resourceClass, RestConfig oldConfig) throws IOException {

        Resource.Builder builder = Resource.builder(resourceClass);
        Resource preResource = builder.build();
        Resource resource = ResourceBuilder.rebuildResource(preResource);
        addPreResource(resource);
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
    }

    /**
     * Registers all cached {@link org.glassfish.jersey.server.model.Resource}
     * instances
     */
    public void registerPreResources() {

        if (CollectionUtils.valid(preResources)) {
            RestContainer.putResources(preResources);
            registerResources(preResources);
        }
    }
}
