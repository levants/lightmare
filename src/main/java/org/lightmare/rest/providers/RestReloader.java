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
 * @author levan
 * @since 0.0.50-SNAPSHOT
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

	synchronized (RestReloader.class) {

	    return reloader;
	}
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
