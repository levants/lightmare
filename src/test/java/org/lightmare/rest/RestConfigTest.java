package org.lightmare.rest;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.glassfish.jersey.server.model.MethodHandler;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.ResourceModelComponent;
import org.junit.Test;
import org.lightmare.bean.LightMareBean;

public class RestConfigTest {

    @Test
    public void resourceTest() {

	Resource.Builder builder = Resource.builder(LightMareBean.class);
	Resource resource = builder.build();
	System.out.println(resource.getName());
	System.out.println(resource);

	List<ResourceMethod> methods = resource.getAllMethods();
	// ResourceMethod.Builder methodBuilder;
	// String name = resource.getName();
	Collection<Class<?>> handlers = resource.getHandlerClasses();
	System.out.println(handlers);
	Class<?> beanClass;
	Method realMethod;
	List<Parameter> parameters;
	for (ResourceMethod method : methods) {
	    System.out.println(method);
	    realMethod = method.getInvocable().getHandlingMethod();
	    realMethod.getParameterTypes();
	    MethodHandler handler = method.getInvocable().getHandler();
	    List<? extends ResourceModelComponent> components = method
		    .getInvocable().getComponents();
	    parameters = method.getInvocable().getParameters();
	    MethodHandler methodHandler = method.getInvocable().getHandler();
	    System.out.println(methodHandler);
	    for (Parameter parameter : parameters) {
		System.out.println(parameter);
		System.out.println(parameter.getRawType());
	    }
	    System.out.println(components);
	    beanClass = handler.getHandlerClass();
	    System.out.println(beanClass);
	    System.out.println(realMethod);
	}
    }
}
