package org.lightmare.rest;

import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

/**
 * Reloads {@link ResourceConfig} at runtime
 * 
 * @author levan
 * 
 */
@Provider
public class Reloader implements ContainerLifecycleListener {

    private static Reloader reloader;

    public Reloader() {
	reloader = this;
    }

    public static Reloader get() {

	return reloader;
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
