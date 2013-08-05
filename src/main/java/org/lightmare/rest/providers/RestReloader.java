package org.lightmare.rest.providers;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.lightmare.rest.RestConfig;

/**
 * Reloads {@link RestConfig} (implementation of {@link ResourceConfig}) at
 * runtime
 * 
 * @author levan
 * 
 */
@Provider
public class RestReloader implements ContainerLifecycleListener {

    private static RestReloader reloader;

    private static final Lock lock = new ReentrantLock();

    public RestReloader() {

	lock.lock();
	try {
	    reloader = this;
	} finally {
	    lock.unlock();
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
