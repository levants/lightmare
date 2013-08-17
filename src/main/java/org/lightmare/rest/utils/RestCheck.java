package org.lightmare.rest.utils;

import java.lang.reflect.Method;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.lightmare.cache.RestContainer;
import org.lightmare.rest.providers.RestProvider;
import org.lightmare.utils.ObjectUtils;

/**
 * Class to check if {@link Class} is annotated for jax.rs appropriated REST
 * annotations and valid to create
 * {@link org.glassfish.jersey.server.model.Resource} classes
 * 
 * @author levan
 * 
 */
public class RestCheck {

    public static void reload() {

	if (RestContainer.hasRest()) {
	    RestProvider.reload();
	}
    }

    /**
     * Checks annotations on {@link Class} and its {@link Method}s for REST
     * resources
     * 
     * @param method
     * @return <code>boolean</code>
     */
    private static boolean checkAnnotation(Method method) {

	boolean valid = (method.isAnnotationPresent(GET.class)
		|| method.isAnnotationPresent(POST.class)
		|| method.isAnnotationPresent(PUT.class) || method
		.isAnnotationPresent(DELETE.class));

	return valid;
    }

    /**
     * Checks if passed {@link Class} is available to create
     * {@link org.glassfish.jersey.server.model.Resource} instance
     * 
     * @param resourceClass
     * @return <code>boolean</code>
     */
    public static boolean check(Class<?> resourceClass) {

	Method[] methods = resourceClass.getDeclaredMethods();
	int length = methods.length;
	boolean valid = Boolean.FALSE;
	Method method;
	for (int i = 0; i < length && ObjectUtils.notTrue(valid); i++) {
	    method = methods[i];
	    valid = checkAnnotation(method);
	}

	return valid;
    }
}
