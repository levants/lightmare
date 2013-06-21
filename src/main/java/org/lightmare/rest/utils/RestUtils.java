package org.lightmare.rest.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.MetaData;
import org.lightmare.ejb.EjbConnector;
import org.lightmare.rest.RestConfig;
import org.lightmare.rest.providers.RestReloader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.RpcUtils;
import org.lightmare.utils.beans.BeanUtils;
import org.lightmare.utils.reflect.MetaUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for REST resources
 * 
 * @author levan
 * 
 */
public class RestUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static RestConfig config;

    private static RestConfig oldConfig;

    private static void getConfig() {

	if (config == null) {
	    oldConfig = RestConfig.get();
	    config = new RestConfig();
	}
    }

    private static RestConfig get() {

	if (config == null) {
	    synchronized (RestUtils.class) {
		getConfig();
	    }
	}

	return config;
    }

    public static <T> T convert(String json, Class<T> valueClass)
	    throws IOException {

	T value = RpcUtils.read(json, valueClass);

	return value;
    }

    private static boolean checkAnnotation(Method method) {

	boolean valid = (method.isAnnotationPresent(GET.class)
		|| method.isAnnotationPresent(POST.class)
		|| method.isAnnotationPresent(PUT.class) || method
		.isAnnotationPresent(DELETE.class));

	return valid;
    }

    private static boolean check(Class<?> resourceClass) {

	boolean valid = Resource.isAcceptable(resourceClass)
		&& resourceClass.isAnnotationPresent(Path.class);
	Method[] methods = resourceClass.getDeclaredMethods();
	int length = methods.length;
	boolean isMethod = Boolean.FALSE;
	Method method;
	for (int i = 0; i < length && !isMethod && valid; i++) {
	    method = methods[i];
	    isMethod = checkAnnotation(method);
	}

	return valid && isMethod;
    }

    public static Resource defineHandler(Resource resource) throws IOException {

	Resource.Builder builder = Resource.builder();
	List<ResourceMethod> methods = resource.getAllMethods();
	ResourceMethod.Builder methodBuilder;
	Collection<Class<?>> handlers = resource.getHandlerClasses();
	Class<?> beanClass;
	String beanEjbName;
	Iterator<Class<?>> iterator = handlers.iterator();
	beanClass = iterator.next();
	beanEjbName = BeanUtils.beanName(beanClass);
	final MetaData metaData = MetaContainer.getSyncMetaData(beanEjbName);
	MediaType preType;
	for (ResourceMethod method : methods) {
	    methodBuilder = builder.addMethod(method.getHttpMethod());
	    methodBuilder.consumes(method.getConsumedTypes());
	    methodBuilder.produces(method.getProducedTypes());
	    final Method realMethod = method.getInvocable().getHandlingMethod();
	    List<MediaType> types = method.getConsumedTypes();
	    if (ObjectUtils.available(types)) {
		preType = types.iterator().next();
	    } else {
		preType = null;
	    }
	    final MediaType type = preType;
	    methodBuilder
		    .handledBy(new Inflector<ContainerRequestContext, Response>() {

			@Override
			public Response apply(ContainerRequestContext data) {

			    Object value = null;
			    try {
				Object bean = new EjbConnector()
					.connectToBean(metaData);
				value = MetaUtils.invoke(realMethod, bean);

			    } catch (IOException ex) {
				ex.printStackTrace();
			    }

			    ResponseBuilder responseBuilder;
			    if (type == null) {
				responseBuilder = Response.ok(value);
			    } else {
				responseBuilder = Response.ok(value, type);
			    }
			    return responseBuilder.build();
			}
		    });
	}
	Resource intercepted = builder.build();

	return intercepted;
    }

    public static void add(Class<?> beanClass) {

	boolean valid = check(beanClass);
	if (valid) {

	    RestReloader reloader = RestReloader.get();
	    if (ObjectUtils.notNull(reloader)) {
		RestConfig conf = get();
		conf.registerClass(beanClass, oldConfig);
	    }
	}
    }

    public static void remove(Class<?> beanClass) {

	RestReloader reloader = RestReloader.get();
	if (ObjectUtils.notNull(reloader)) {
	    RestConfig conf = get();
	    conf.unregister(beanClass);
	}
    }

    public static void reload() {

	RestReloader reloader = RestReloader.get();
	if (ObjectUtils.notNull(reloader) && ObjectUtils.notNull(config)) {
	    reloader.reload(config);
	}
    }
}
