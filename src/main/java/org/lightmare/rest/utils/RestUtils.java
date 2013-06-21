package org.lightmare.rest.utils;

import java.lang.reflect.Method;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.lightmare.rest.RestConfig;

/**
 * Utility class for REST resources
 * 
 * @author levan
 * 
 */
public class RestUtils {

    private static boolean checkAnnotation(Method method) {

	boolean valid = method.isAccessible()
		&& method.isAnnotationPresent(GET.class)
		|| method.isAnnotationPresent(POST.class)
		|| method.isAnnotationPresent(PUT.class)
		|| method.isAnnotationPresent(DELETE.class);

	return valid;
    }

    private static boolean check(Class<?> resourceClass) {

	boolean valid = resourceClass.isAnnotationPresent(Path.class);
	Method[] methods = resourceClass.getDeclaredMethods();
	int length = methods.length;
	boolean isMethod = Boolean.FALSE;
	Method method;
	for (int i = 0; i < length && !isMethod; i++) {
	    method = methods[i];
	    isMethod = checkAnnotation(method);
	}

	return valid && isMethod;
    }

    public static void add(Class<?> beanClass) {

	boolean valid = check(beanClass);
	if (valid) {

	    RestConfig config = RestConfig.get();
	    config.registerClass(beanClass);
	}
    }

    public static void remove(Class<?> beanClass) {

	RestConfig config = RestConfig.get();
	config.unregister(beanClass);
    }
}
