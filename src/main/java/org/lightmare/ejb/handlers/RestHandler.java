package org.lightmare.ejb.handlers;

import java.lang.reflect.Method;

/**
 * Handler class to call bean methods for REST services
 * 
 * @author levan
 * 
 */
public class RestHandler<T> {

    private final BeanHandler handler;

    private final T bean;

    public RestHandler(BeanHandler handler, T bean) {

	this.handler = handler;
	this.bean = bean;
    }

    public Object invoke(Method method, Object[] args) throws Throwable {

	return handler.invoke(bean, method, args);
    }

}
