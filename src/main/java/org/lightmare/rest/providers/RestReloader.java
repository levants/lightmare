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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.lightmare.rest.RestConfig;
import org.lightmare.utils.ObjectUtils;

/**
 * Reloads {@link RestConfig} (implementation of {@link ResourceConfig}) at
 * runtime
 * 
 * @author Levan Tsinadze
 * @since 0.0.50-SNAPSHOT
 * @see ResourceConfig
 */
@Provider
public class RestReloader implements ContainerLifecycleListener {

    private static RestReloader reloader;

    private static final Lock LOCK = new ReentrantLock();

    public RestReloader() {

	ObjectUtils.lock(LOCK);
	try {
	    reloader = this;
	} finally {
	    ObjectUtils.unlock(LOCK);
	}
    }

    public static RestReloader get() {

	RestReloader restReloader;

	ObjectUtils.lock(LOCK);
	try {
	    restReloader = reloader;
	} finally {
	    ObjectUtils.unlock(LOCK);
	}

	return restReloader;
    }

    private Container container;

    public void reload() {
	container.reload();
    }

    public void reload(ResourceConfig config) {
	container.reload(config);
    }

    @Override
    public void onStartup(Container container) {
	this.container = container;
    }

    @Override
    public void onReload(Container container) {
    }

    @Override
    public void onShutdown(Container container) {
    }
}
