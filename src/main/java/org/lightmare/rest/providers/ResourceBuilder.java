package org.lightmare.rest.providers;

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
import org.lightmare.utils.CollectionUtils;
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
     * Gets {@link MetaData} for resource handler {@link Class}es first value
     * 
     * @param resource
     * @return {@link MetaData}
     * @throws IOException
     */
    private static MetaData getMetaData(Resource resource) throws IOException {

	Collection<Class<?>> handlers = resource.getHandlerClasses();
	Class<?> beanClass = CollectionUtils.getFirst(handlers);
	String beanEjbName = BeanUtils.beanName(beanClass);

	MetaData metaData = MetaContainer.getSyncMetaData(beanEjbName);

	return metaData;
    }

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

	// Defines media type
	MediaType type;
	if (ObjectUtils.available(consumedTypes)) {
	    type = CollectionUtils.getFirst(consumedTypes);
	} else {
	    type = null;
	}
	// Inflector to define bean methods
	Inflector<ContainerRequestContext, Response> inflector = new RestInflector(
		realMethod, metaData, type, parameters);

	// Builds new method for resource
	ResourceMethod.Builder methodBuilder = builder.addMethod(method
		.getHttpMethod());
	methodBuilder.consumes(consumedTypes);
	methodBuilder.produces(producedTypes);
	methodBuilder.nameBindings(method.getNameBindings());
	methodBuilder.handledBy(inflector);
	methodBuilder.build();
    }

    /**
     * Registers child resources for passed {@link Resource.Builder} from
     * appropriate {@link Resource} instance
     * 
     * @param resource
     * @param builder
     * @throws IOException
     */
    private static void registerChildren(Resource resource,
	    Resource.Builder builder) throws IOException {

	// Registers children resources recursively
	List<Resource> children = resource.getChildResources();
	if (ObjectUtils.available(children)) {
	    Resource child;
	    for (Resource preChild : children) {
		child = rebuildResource(preChild);
		builder.addChildResource(child);
	    }
	}
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
	MetaData metaData = getMetaData(resource);

	List<ResourceMethod> methods = resource.getAllMethods();
	for (ResourceMethod method : methods) {
	    addMethod(builder, method, metaData);
	}
	// Registers children resources recursively
	registerChildren(resource, builder);

	Resource rebuiltResource = builder.build();

	return rebuiltResource;
    }
}
