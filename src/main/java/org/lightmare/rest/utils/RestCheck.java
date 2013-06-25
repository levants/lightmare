package org.lightmare.rest.utils;

import java.lang.reflect.Method;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.lightmare.cache.MetaContainer;

/**
 * Class to check REST classes
 * 
 * @author levan
 * 
 */
public class RestCheck {

    public static void reload() {

	if (MetaContainer.hasRest()) {
	    RestUtils.reload();
	}
    }

    private static boolean checkAnnotation(Method method) {

	boolean valid = (method.isAnnotationPresent(GET.class)
		|| method.isAnnotationPresent(POST.class)
		|| method.isAnnotationPresent(PUT.class) || method
		.isAnnotationPresent(DELETE.class));

	return valid;
    }

    public static boolean check(Class<?> resourceClass) {
	;
	Method[] methods = resourceClass.getDeclaredMethods();
	int length = methods.length;
	boolean valid = Boolean.FALSE;
	Method method;
	for (int i = 0; i < length && !valid; i++) {
	    method = methods[i];
	    valid = checkAnnotation(method);
	}

	return valid;
    }
}
