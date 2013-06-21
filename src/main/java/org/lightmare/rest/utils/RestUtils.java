package org.lightmare.rest.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;

import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.MethodHandler;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.ResourceMethod.JaxrsType;
import org.lightmare.rest.RestConfig;
import org.lightmare.rest.providers.RestReloader;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.RpcUtils;

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

    public static void defineHandler(Resource resource) {

	Resource.Builder builder = Resource.builder();
	List<ResourceMethod> methods = resource.getAllMethods();
	JaxrsType type;
	ResourceMethod.Builder methodBuilder;
	for (ResourceMethod method : methods) {
	    methodBuilder = builder.addMethod(method.getHttpMethod());
	    methodBuilder.consumes(method.getConsumedTypes());
	    methodBuilder.produces(method.getProducedTypes());
	    methodBuilder
		    .handledBy(new Inflector<ContainerRequestContext, Object>() {

			@Override
			public Object apply(ContainerRequestContext data) {
			    return null;
			}
		    });
	}
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
