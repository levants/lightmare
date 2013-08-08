package org.lightmare.ejb.handlers;

import java.lang.reflect.Method;

/**
 * Handler class to call bean methods for REST services
 * 
 * @author levan
 * 
 */
public class RestHandler {

    private final BeanHandler handler;

    private final Object bean;

    public RestHandler(BeanHandler handler) {

	this.handler = handler;
	this.bean = handler.getBean();
    }

    public Object invoke(Method method, Object[] args) throws Throwable {

	return handler.invoke(bean, method, args);
    }

}
