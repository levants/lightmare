package org.lightmare.rest.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.MetaData;
import org.lightmare.rest.providers.RestInflector;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.beans.BeanUtils;

/**
 * Class to build and {@link Resource} for REST services
 * 
 * @author Levan
 * 
 */
public class ResourceBuilder {

    /**
     * Defines method for {@link Resource} to build
     * 
     * @param builder
     * @param method
     * @param metaData
     */
    private static void addMethod(Resource.Builder builder,
	    ResourceMethod method, MetaData metaData) {

	List<MediaType> consumedTypes = method.getConsumedTypes();
	List<MediaType> producedTypes = method.getProducedTypes();
	Invocable invocable = method.getInvocable();
	Method realMethod = invocable.getHandlingMethod();
	List<Parameter> parameters = invocable.getParameters();
	MediaType type;
	if (ObjectUtils.available(consumedTypes)) {
	    type = ObjectUtils.getFirst(consumedTypes);
	} else {
	    type = null;
	}
	Inflector<ContainerRequestContext, Response> inflector = new RestInflector(
		realMethod, metaData, type, parameters);
	ResourceMethod.Builder methodBuilder = builder.addMethod(method
		.getHttpMethod());
	methodBuilder.consumes(consumedTypes);
	methodBuilder.produces(producedTypes);
	methodBuilder.nameBindings(method.getNameBindings());
	methodBuilder.handledBy(inflector);
	methodBuilder.build();
    }

    /**
     * Builds new {@link Resource} from passed one with new
     * {@link org.glassfish.jersey.process.Inflector} implementation
     * {@link RestInflector} and with all child resources
     * 
     * @param resource
     * @return {@link Resource}
     * @throws IOException
     */
    public static Resource rebuildResource(Resource resource)
	    throws IOException {

	Resource.Builder builder = Resource.builder(resource.getPath());
	builder.name(resource.getName());
	List<ResourceMethod> methods = resource.getAllMethods();
	ResourceMethod.Builder methodBuilder;
	Collection<Class<?>> handlers = resource.getHandlerClasses();
	Class<?> beanClass;
	String beanEjbName;
	beanClass = ObjectUtils.getFirst(handlers);
	beanEjbName = BeanUtils.beanName(beanClass);
	List<MediaType> consumedTypes;
	List<MediaType> producedTypes;
	Invocable invocable;
	MetaData metaData = MetaContainer.getSyncMetaData(beanEjbName);
	Method realMethod;
	MediaType type;
	List<Parameter> parameters;
	// Inflector to define bean methods
	Inflector<ContainerRequestContext, Response> inflector;
	for (ResourceMethod method : methods) {
	    addMethod(builder, method, metaData);
	}
	// Registers children resources recursively
	List<Resource> children = resource.getChildResources();
	if (ObjectUtils.available(children)) {
	    Resource child;
	    for (Resource preChild : children) {
		child = rebuildResource(preChild);
		builder.addChildResource(child);
	    }
	}
	Resource intercepted = builder.build();

	return intercepted;
    }
}
